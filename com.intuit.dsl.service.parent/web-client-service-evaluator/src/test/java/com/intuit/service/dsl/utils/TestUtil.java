package com.intuit.service.dsl.utils;

import static com.intuit.service.dsl.JsonUtils.MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.intuit.dsl.service.Service;
import com.intuit.service.dsl.JsonUtils;
import com.intuit.service.dsl.TestServiceConfiguration;
import com.intuit.service.dsl.resource.loader.StringContentResourceLoader;
import com.intuit.service.dsl.evaluator.ServiceConfiguration;
import com.intuit.service.dsl.evaluator.ServiceEvaluatorRequest;
import com.intuit.service.dsl.evaluator.utils.ServiceEvaluatorUtils;
import com.intuit.service.dsl.resource.loader.ServiceResourceLoader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpCookie;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.util.context.Context;

public class TestUtil {

  private static final String HEADERS = "headers";
  private static final String COOKIES = "cookies";
  private static final String ARGUMENTS = "arguments";
  public static final int TIMEOUT = 1000;
  public static WebClient webClient = WebClient.builder().build();

  public static String loadResourceFromFile(String path) {
    try {
      return Resources
          .toString(Resources.getResource(path), Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    }
  }

  public static Service getService(String path, String content, String serviceName) {
    return ServiceEvaluatorUtils
        .getService(createResourceLoader(content).getResource(), serviceName);
  }

  public static ServiceResourceLoader createResourceLoader(String resourceContent) {
    return new StringContentResourceLoader(resourceContent);
  }

  public static ServiceConfiguration createServiceConfiguration(int timeout, int port) {
    TestServiceConfiguration serviceConfiguration = new TestServiceConfiguration(null);
    serviceConfiguration.add("timeout", Integer.toString(timeout));
    serviceConfiguration.add("endpoint", "http://localhost:" + port);
    return serviceConfiguration;
  }

  public static ServiceEvaluatorRequest createServiceEvaluatorRequest(Service service, Map<String,JsonNode> inputMap, String serviceName) {
    return ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .inputMap(inputMap)
        .serviceName(Optional.ofNullable(serviceName))
        .service(service)
        .build();
  }

  public static JsonNode createRequestContext(String tid, String argumentsJson, String headersJson) {
    String transactionCtxJson = String.format("{\"test\":false,\"securityScan\":false,"
        + "\"tid\":\"%s\",\"testValueForHeader\":{\"present\":false},"
        + "\"intuit_tid\":\"empty-55ab90fa-8289-4fdf-ba36-025b774250f1\"}", tid);

    String requestCtxStrng = String.format(
        "{ \"arguments\": %s, \"cookies\": null, \"headers\": %s, \"transactionContext\": %s }",
        argumentsJson, headersJson, transactionCtxJson);

    try {
      return MAPPER.readTree(requestCtxStrng);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static Map<String,JsonNode> toRequestContext(Map<String,Object> arguments, Map<String,Object> cookies, Map<String,Object> headers) {
    //TransactionContext txnCtx = serviceEvaluatorRequest.getReactorContext().get(TransactionContext.class);
    ObjectNode requestContextNode = JsonUtils.MAPPER.createObjectNode();
//    requestContextNode.replace(TRANSACTION_CONTEXT, MAPPER
//        .convertValue(txnCtx, JsonNode.class));
    requestContextNode
        .replace(ARGUMENTS, JsonUtils.MAPPER.convertValue(arguments, JsonNode.class));
    requestContextNode.replace(COOKIES, JsonUtils.MAPPER.convertValue(cookies,
        JsonNode.class));
    requestContextNode.replace(HEADERS, JsonUtils.MAPPER.convertValue(headers,
        JsonNode.class));
    return ImmutableMap.of("requestContext",requestContextNode);
  }

  public static ServerRequest createServerRequest() {
    return MockServerRequest.builder()
        .header("intuit_test", "true")
        .cookie(new HttpCookie("testCookie", "testCookieValue"))
        .cookie(new HttpCookie("noValueCookie", ""))
        .build();
  }


}
