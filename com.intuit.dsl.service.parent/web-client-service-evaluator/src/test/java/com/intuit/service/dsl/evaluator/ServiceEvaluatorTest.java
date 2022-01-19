package com.intuit.service.dsl.evaluator;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.intuit.dsl.service.Service;
import com.intuit.service.dsl.AddBookServicePost;
import com.intuit.service.dsl.AddBookServicePut;
import com.intuit.service.dsl.BookByIdService;
import com.intuit.service.dsl.BooksService;
import com.intuit.service.dsl.JsonUtils;
import com.intuit.service.dsl.evaluator.exceptions.ServiceEvaluatorException;
import com.intuit.service.dsl.utils.TestUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ServiceEvaluatorTest {

  private static int WIREMOCK_PORT = 4060;

  private ServiceEvaluator serviceEvaluator;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);  // consider using mockito to mock webclient

  @Before
  public void setup() {
     serviceEvaluator = makeServiceEvaluator(TestUtil.createServiceConfiguration(TestUtil.TIMEOUT, WIREMOCK_PORT));
  }

  @Test
  public void canEvaluateServiceNodeWithPostMethod() throws ExecutionException, InterruptedException, IOException {
    // GIVEN
    String svcEndpoint = "/books";

    stubFor(post(urlPathMatching(svcEndpoint))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"id\": \"book-1\",\"name\": \"The Book\",\"price\": 100}")
            .withHeader("Content-Type", "application/json;charset=UTF-8")));

    String serviceName = "addBooks";
    final Service service = TestUtil.getService("main/flow/service.service",
        AddBookServicePost.addBookDSL, serviceName);
    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
        .createServiceEvaluatorRequest(service, AddBookServicePost.svcEvaluatorReq4Post, serviceName);

    // WHEN
    String result = serviceEvaluator
        .evaluate(serviceEvaluatorRequest)
        .get();

    // THEN
    String requestBodyCriteria = "$[?(@.id == 'book-1' " +
        "&& @.name == 'The Book' " +
        "&& @.price == 100.00 " +
        ")]";
    verify(exactly(1), postRequestedFor(urlPathMatching(svcEndpoint))
        .withRequestBody(matchingJsonPath(requestBodyCriteria)));
    Map<String, Object> resultMap = JsonUtils.MAPPER.readValue(result, Map.class);
    assertThat(resultMap).containsOnlyKeys("id", "name", "price");
  }

  @Test
  public void canEvaluateServiceNodeWithPutMethod() throws ExecutionException, InterruptedException, IOException {
    // GIVEN
    String svcEndpoint = "/books";

    String serviceName = "addBooksPUT";
    final Service service = TestUtil.getService("main/flow/service.service",
        AddBookServicePut.addBookDSL, serviceName);
    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
        .createServiceEvaluatorRequest(service, AddBookServicePut.svcEvaluatorReq4Put, serviceName);

    stubFor(put(urlPathMatching(svcEndpoint))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"id\": \"book-1\",\"name\": \"The Book\",\"price\": 100}")
            .withHeader("Content-Type", "application/json;charset=UTF-8")));

    // WHEN
    String result = serviceEvaluator.evaluate(serviceEvaluatorRequest).get();

    // THEN
    String requestBodyCriteria = "$[?(@.id == 'book-1' " +
        "&& @.name == 'The Book' " +
        "&& @.price == 100.00 " +
        ")]";
    verify(exactly(1), putRequestedFor(urlPathMatching(svcEndpoint))
        .withRequestBody(matchingJsonPath(requestBodyCriteria)));
    Map<String, Object> resultMap = JsonUtils.MAPPER.readValue(result, Map.class);
    assertThat(resultMap).containsOnlyKeys("id", "name", "price");
  }

  @Test
  public void canEvaluateServiceNodeWithGetMethod() throws ExecutionException, InterruptedException, IOException {
    // GIVEN
    String svcEndpoint = "/books/book-1";

    stubFor(get(urlPathMatching(svcEndpoint))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"id\": \"book-1\",\"name\": \"The Book\",\"price\": 100, \"status\" : \"AVAILABLE\"}")
            .withHeader("Content-Type", "application/json;charset=UTF-8")));
    // WHEN
    String result = serviceEvaluator.evaluate(BookByIdService.svcEvaluatorReq4GetWithParam).get();

    // THEN
    verify(exactly(1), getRequestedFor(urlPathMatching(svcEndpoint))
        .withRequestBody(absent()));
    Map<String, Object> resultMap = JsonUtils.MAPPER.readValue(result, Map.class);
    assertThat(resultMap).containsOnlyKeys("id", "name", "price", "status");
  }

  @Test
  public void canEvaluateServiceNodeForGetMethodWithMultipleArgument()
      throws ExecutionException, InterruptedException, JsonProcessingException {
    // GIVEN
    String svcEndpoint = "/books/book-1";

    stubFor(get(urlPathMatching(svcEndpoint))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"id\": \"book-1\",\"name\": \"The Book\",\"price\": 100, \"status\" : \"AVAILABLE\"}")
            .withHeader("Content-Type", "application/json;charset=UTF-8")));

    // WHEN
    String result = serviceEvaluator.evaluate(BookByIdService.svcEvaluatorReq4GetWithParamMultiArg).get();

    // THEN
    verify(exactly(1), getRequestedFor(urlPathMatching(svcEndpoint))
        .withRequestBody(absent()));
    Map<String, Object> resultMap = JsonUtils.MAPPER.readValue(result, Map.class);
    assertThat(resultMap).containsOnlyKeys("id", "name", "price", "status");
  }

  @Test
  public void canEvaluateServiceNodeWithNoArgumentForGetMethod()
      throws ExecutionException, InterruptedException, IOException {
    // GIVEN
    String svcEndpoint = "/books";

    final Service service = TestUtil.getService("main/flow/service.service",
        BooksService.booksDSL, null);
    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
        .createServiceEvaluatorRequest(service, AddBookServicePut.svcEvaluatorReq4Put, null);

    stubFor(get(urlPathMatching(svcEndpoint))
        .withQueryParam("limit", equalTo("10"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("[{\"id\": \"book-1\",\"name\": \"The Book\",\"price\": 100, \"status\" : \"AVAILABLE\"}]")
            .withHeader("Content-Type", "application/json;charset=UTF-8")));

    // WHEN
    String result = serviceEvaluator.evaluate(BooksService.svcEvaluatorReq4GETNoParam).get();

    // THEN
    verify(exactly(1), getRequestedFor(urlPathMatching(svcEndpoint))
        .withRequestBody(absent()));
    List<Map<String, Object>> resultMap = JsonUtils.MAPPER.readValue(result, List.class);
    assertThat(resultMap).isNotNull();
  }

//  @Test(expected = ServiceEvaluatorException.class)
//  public void cannotEvaluateWithInvalidDSLFile() {
//    // GIVEN
//    ServiceResourceLoader booksResourceLoader = TestUtil.createResourceLoader("main/flow/invalidFileName.service",
//        BooksService.booksDSL, serviceConfiguration);
//
//    // WHEN .. THEN throws exception
//    makeServiceEvaluator(BooksService.svcEvaluatorReq4InvalidDSL,
//        booksResourceLoader, "GraphQL.Query");
//  }

//  @Test(expected = NullPointerException.class)
//  public void cannotCreateWithNoRequestType() {
//    // GIVEN
//    ServiceResourceLoader booksResourceLoader = TestUtil.createResourceLoader("main/flow/invalidFileName.service",
//        BooksService.booksDSL, serviceConfiguration);
//
//    // when .. then throws exception
//    ServiceEvaluator.builder()
//        .serviceEvaluatorRequest(BooksService.svcEvaluatorReq4NoRequestTypeInSvcEvaluator)
//        .resourceLoader(booksResourceLoader)
//        .webClient(TestUtil.webClient)
//        .serviceId("TEST_SERVICE")
//        //.requestType(requestType) <-- this is ommited
//        .build();
//  }

  @Test
  public void canEvaluateServiceNodeWithPostAndBodyAnnotation()
      throws ExecutionException, InterruptedException, IOException {
    // GIVEN
    String svcEndpoint = "/books";
    final Service service = TestUtil.getService("main/flow/service.service",
        AddBookServicePost.addBookDSL, "addBooksWithBody");
    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
        .createServiceEvaluatorRequest(service, AddBookServicePost.svcEvaluatorReqBodyAnnotation, "addBooksWithBody");


    stubFor(post(urlPathMatching(svcEndpoint))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"id\": \"book-1\",\"name\": \"The Book\",\"price\": 100}")
            .withHeader("Content-Type", "application/json;charset=UTF-8")));

    // WHEN
    String result = serviceEvaluator.evaluate(serviceEvaluatorRequest).get();

    // THEN
    String requestBodyCriteria1 = "$.newBook[?(@.id == 'book-1' " +
        "&& @.name == 'The Book' " +
        ")]";
    String requestBodyCriteria2 = "$[?(@.newBookId == 'book-1')]";
    String requestBodyCriteria3 = "$.chapters[?(@ == 'Chapter-3')]";
    String requestBodyCriteria4 = "$[?(@.price == 100.00)]";
    String requestBodyCriteria5 = "$[?(@.isPublished == true)]";
    String requestBodyCriteria6 = "$[?(@.isHardPrint == false)]";
    String requestBodyCriteria7 = "$[?(@.author == null)]";
    verify(exactly(1), postRequestedFor(urlPathMatching(svcEndpoint))
        .withRequestBody(matchingJsonPath(requestBodyCriteria1))
        .withRequestBody(matchingJsonPath(requestBodyCriteria2))
        .withRequestBody(matchingJsonPath(requestBodyCriteria3))
        .withRequestBody(matchingJsonPath(requestBodyCriteria4))
        .withRequestBody(matchingJsonPath(requestBodyCriteria5))
        .withRequestBody(matchingJsonPath(requestBodyCriteria6))
        .withRequestBody(matchingJsonPath(requestBodyCriteria7))
    );
    Map<String, Object> resultMap = JsonUtils.MAPPER.readValue(result, Map.class);
    assertThat(resultMap).containsOnlyKeys("id", "name", "price");
  }

  @Test
  public void canEvaluateServiceNodeWithPostAndNoKeyBodyAnnotation()
      throws ExecutionException, InterruptedException, IOException {
    // GIVEN
    String svcEndpoint = "/custombody/nokey/books";

    final Service service = TestUtil.getService("main/flow/service.service",
        AddBookServicePost.addBookDSL, "addBooksNoKeyBody");
    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
        .createServiceEvaluatorRequest(service, AddBookServicePost.svcEvaluatorReqNoKeyBodyAnnotation, "addBooksNoKeyBody");

    stubFor(post(urlPathMatching(svcEndpoint))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"id\": \"book-1\",\"name\": \"The Book\",\"price\": 100}")
            .withHeader("Content-Type", "application/json;charset=UTF-8")));

    // WHEN
    String result = serviceEvaluator.evaluate(serviceEvaluatorRequest).get();

    // THEN
    String requestBodyCriteria = "$[?(@.id == 'book-1' " +
        "&& @.name == 'The Book' " +
        ")]";
    verify(exactly(1), postRequestedFor(urlPathMatching(svcEndpoint))
        .withRequestBody(matchingJsonPath(requestBodyCriteria)));

    Map<String, Object> resultMap = JsonUtils.MAPPER.readValue(result, Map.class);
    assertThat(resultMap).containsOnlyKeys("id", "name", "price");
  }

  @Test(expected = ServiceEvaluatorException.class)
  public void evaluateFailedServiceNodeWithPostWithMultipleArguments() {
    // NOTE:  Without using @Body, i.e default body construction, only 1 arg is supported for POST.
    // GIVEN
    final Service service = TestUtil.getService("main/flow/service.service",
        AddBookServicePost.addBookDSL, "addBooks");
    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
        .createServiceEvaluatorRequest(service, AddBookServicePost.svcEvaluatorReq4PostWithMultiArg, "addBooks");

    // WHEN
    serviceEvaluator.evaluate(serviceEvaluatorRequest);
  }

  @Test(expected = ServiceEvaluatorException.class)
  public void evaluateFailedServiceNodeWithPutWithMultipleArguments() {
    // NOTE:  Without using @Body, i.e default body construction, only 1 arg is supported for PUT.
    // GIVEN
    final Service service = TestUtil.getService("main/flow/service.service",
        AddBookServicePost.addBookDSL, "addBooksPUT");
    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
        .createServiceEvaluatorRequest(service, AddBookServicePost.svcEvaluatorReq4PostWithMultiArg, "addBooksPUT");

    // WHEN
    serviceEvaluator.evaluate(serviceEvaluatorRequest);
  }
//
// ToDo: Think about this case
//
//  @Test(expected = NullPointerException.class)
//  public void evaluateFailedServiceNodeWithPutWithNoArgument() {
//    // GIVEN
//
//    final Service service = TestUtil.getService("main/flow/service.service",
//        AddBookServicePut.addBookDSL, "addBooksPUT");
//    ServiceEvaluatorRequest serviceEvaluatorRequest = TestUtil
//        .createServiceEvaluatorRequest(service, AddBookServicePut.svcEvaluatorReq4PutNoArg, "addBooksPUT");
//
//    // WHEN
//    serviceEvaluator.evaluate(serviceEvaluatorRequest);
//  }

  @Test(expected = ServiceEvaluatorException.class)
  public void evaluateFailedCannotCreateBodyNoKey4StringValue()  {
    serviceEvaluator.evaluate(BookByIdService.svcEvaluatorReqErrStringNokey);
  }

  @Test(expected = ServiceEvaluatorException.class)
  public void evaluateFailedCannotCreateBodyNoKey4ArrayValue() {
    serviceEvaluator.evaluate(BookByIdService.svcEvaluatorReqErrArrayNokey);
  }

  @Test(expected = ServiceEvaluatorException.class)
  public void evaluateFailedPathParamExpressionNotString() {
    serviceEvaluator.evaluate(BookByIdService.svcEvaluatorReqErrPathParamNotString);
  }

  @Test(expected = ServiceEvaluatorException.class)
  public void evaluateFailedPathParamExpressionNull() {
    serviceEvaluator.evaluate(BookByIdService.svcEvaluatorReqErrPathParamNull);
  }

  @Test(expected = ServiceEvaluatorException.class)
  public void evaluateFailedNoServiceNode() {
    TestUtil.getService("main/flow/service.service", BookByIdService.bookByIdDSLErrNoServiceNode, null);
  }

  private static ServiceEvaluator makeServiceEvaluator(
      ServiceConfiguration serviceConfiguration
  ) {
    return ServiceEvaluator.builder()
        .serviceConfiguration(serviceConfiguration)
        .webClient(TestUtil.webClient)
        .build();
  }

}
