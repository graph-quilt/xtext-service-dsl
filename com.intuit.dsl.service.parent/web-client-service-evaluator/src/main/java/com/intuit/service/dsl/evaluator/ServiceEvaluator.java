package com.intuit.service.dsl.evaluator;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.MILLIS;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.intuit.dsl.service.RequestArgument;
import com.intuit.dsl.service.Service;
import com.intuit.service.dsl.JsonUtils;
import com.intuit.service.dsl.evaluator.exceptions.ServiceDataRetrieverException;
import com.intuit.service.dsl.evaluator.exceptions.ServiceEvaluatorException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.ecore.EObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

@Slf4j
public class ServiceEvaluator {

  private static final String REQUEST_LOGGING_HINTS = "hints";

  // Constructor args
  private ServiceConfiguration serviceConfiguration;
  private WebClient webClient;

  // DSL evaluated properties
  private String uri;
  private Duration timeout;

  private Service service;
  private Map<String, JsonNode> inputMap;

  private final ObjectNode bodyRootNode = JsonUtils.MAPPER.createObjectNode();
  public static final String BODY = "Body";
  public static final String PATH_PARAM = "PathParam";
  public static final String QUERY_PARAM = "Query";
  public static final String HEADER = "Header";

  // Setup arguments
  private HttpMethod method;
  private MultiValueMap<String, String> headers;
  private Map<String, String> pathParams;
  private MultiValueMap<String, String> queryParams;

  @Builder
  ServiceEvaluator(WebClient webClient, ServiceConfiguration serviceConfiguration) {
    Objects.requireNonNull(serviceConfiguration, "Service Configuration cannot be null");
    Objects.requireNonNull(webClient, "WebClient cannot be null");
    this.webClient = webClient;
    this.serviceConfiguration = serviceConfiguration;
  }

  public CompletableFuture<String> evaluate(ServiceEvaluatorRequest serviceEvaluatorRequest) {
    Objects.requireNonNull(serviceEvaluatorRequest, "ServiceEvaluatorRequest cannot be null");
    Objects.requireNonNull(serviceEvaluatorRequest.getService(), "Service cannot be null");
    this.service = serviceEvaluatorRequest.getService();
    this.inputMap = serviceEvaluatorRequest.getInputMap();
    getStaticProperties();
    Map<String, Object> hints = new HashMap<>(1);
    hints.put("service", serviceEvaluatorRequest.getServiceName());

    return webClient
        .method(method)
        .uri(new DefaultUriBuilderFactory(uri)
            .builder()
            .queryParams(queryParams)
            .build(pathParams))
        .attribute(REQUEST_LOGGING_HINTS, hints)
        .headers(httpHeaders -> httpHeaders.putAll(headers))
        .body(createCustomBody())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .exchange()
        .subscriberContext(serviceEvaluatorRequest.getReactorContext())
        .timeout(timeout)
        .flatMap(this::handleServiceResponse)
        .toFuture();
  }

  private void getStaticProperties() {
    method = HttpMethod.valueOf(service.getMethod());
    List<RequestArgument> argumentList = service.getRequestArguments();
    final String url = evaluateAsString(service.getUrl());
    String path = evaluateAsString(service.getPath());
    Objects.requireNonNull(url, "BaseUrl cannot be null");
    Objects.requireNonNull(path, "Path cannot be null");
    uri = url.concat(path);
    timeout = Duration.of(Long.parseLong(evaluateAsString(service.getTimeout())), MILLIS);
    headers = CollectionUtils
        .toMultiValueMap(argumentList.stream().filter(requestArgument -> HEADER.equals(requestArgument.getType())).
            collect(Collectors.toMap(RequestArgument::getKey, arg -> Arrays.asList(evaluateAsString(arg.getExp())))));

    pathParams = argumentList.stream()
        .filter(requestArgument -> PATH_PARAM.equals(requestArgument.getType()))
        .collect(Collectors.toMap(RequestArgument::getKey, arg -> {
          String value = evaluateAsString(arg.getExp());
          if (Objects.isNull(value)) {
            throw new ServiceEvaluatorException(format("@PathParam null for key %s.", arg.getKey()));
          }
          return value;
        }));

    queryParams = CollectionUtils
        .toMultiValueMap(
            argumentList.stream()
                .filter(requestArgument -> QUERY_PARAM.equals(requestArgument.getType()))
                .map(argument -> Pair.of(argument.getKey(), evaluateAsString(argument.getExp())))
                .filter(pair -> Objects.nonNull(pair.getRight()))
                .collect(Collectors.toMap(p -> p.getLeft(), p -> Arrays.asList(p.getRight())))
        );

    argumentList.stream()
        .filter(requestArgument -> BODY.equals(requestArgument.getType()))
        .forEach(body -> {
          JsonNode valueNode = evaluateExpression(body.getExp());
          if (valueNode == null) {
            valueNode = NullNode.getInstance();
          }
          addToBody(body.getKey(), valueNode);
        });
  }

  private void addToBody(String key, JsonNode valueNode) {
    switch (valueNode.getNodeType()) {
      case STRING:
      case NUMBER:
      case BOOLEAN:
      case BINARY:
      case POJO:
      case MISSING:
      case NULL:
        addValueNodeToBody(key, (ValueNode) valueNode);
        break;
      case OBJECT:
        addObjectNodeToBody(key, (ObjectNode) valueNode);
        break;
      case ARRAY:
        addArrayNodeToBody(key, (ArrayNode) valueNode);
        break;
      default:
        break;
    }
  }

  private void addObjectNodeToBody(String key, ObjectNode valueNode) {
    if (StringUtils.isEmpty(key)) {
      bodyRootNode.setAll(valueNode);
    } else {
      bodyRootNode.set(key, valueNode);
    }
  }

  private void addValueNodeToBody(String key, ValueNode valueNode) {
    if (StringUtils.isNotEmpty(key)) {
      bodyRootNode.set(key, valueNode);
    } else {
      throw new ServiceEvaluatorException(format("Missing @Body key %s for  value %s",
          key, valueNode.asText()));
    }
  }

  private void addArrayNodeToBody(String key, ArrayNode valueNode) {
    if (StringUtils.isNotEmpty(key)) {
      bodyRootNode.set(key, valueNode);
    } else {
      throw new ServiceEvaluatorException(format("Missing @Body key %s for an array node", key));
    }
  }

  private String evaluateAsString(EObject eObject) {
    JsonNode jsonNode = evaluateExpression(eObject);
    if (Objects.isNull(jsonNode)) {
      return null;
    }
    if (jsonNode.getNodeType() == JsonNodeType.STRING || jsonNode.getNodeType() == JsonNodeType.NUMBER) {
      return jsonNode.asText();
    } else {
      throw new ServiceEvaluatorException(
          format("Cannot evaluate node type %s to string.", jsonNode.getNodeType()));
    }
  }

  private JsonNode evaluateExpression(EObject eObject) {
    return ExpressionEvaluator.builder().serviceConfiguration(serviceConfiguration).inputMap(inputMap).build()
        .evaluate(eObject);
  }


  private BodyInserter<?, ? super ClientHttpRequest> createCustomBody() {
    if (method == HttpMethod.POST || method == HttpMethod.PUT) {
      return BodyInserters
          .fromPublisher(Mono.just(Objects.requireNonNull(bodyRootNode).toString()), String.class);
    }
    return BodyInserters.empty();
  }

  private Mono<String> handleServiceResponse(ClientResponse response) {
    final HttpStatus statusCode = response.statusCode();
    if (statusCode.is2xxSuccessful()) {
      return response.bodyToMono(String.class);
    } else {
      return response.bodyToMono(String.class)
          .defaultIfEmpty("")
          .flatMap(body -> Mono.error(new ServiceDataRetrieverException(statusCode.value(), body)));
    }
  }

}
