package com.intuit.service.dsl;

import static java.util.Collections.EMPTY_MAP;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuit.service.dsl.utils.TestUtil;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.reactive.function.server.ServerRequest;


public class AddBookServicePost {

  public static final String USER_CHANNEL = "SomeUserChannel";

  public static JsonNode requestContextNode;

  public static String addBookDSL;

  public static Map svcEvaluatorReq4Post;
  public static Map svcEvaluatorReqBodyAnnotation;
  public static Map svcEvaluatorReqNoKeyBodyAnnotation;
  public static Map svcEvaluatorReq4PostWithMultiArg;

  //static final ServiceAdapterRequest addBookServiceAdapterRequest;

  static {
    String tid = UUID.randomUUID().toString();
    ServerRequest serverRequest = TestUtil.createServerRequest();

    addBookDSL = TestUtil.loadResourceFromFile("addBook/service.service");
    //serviceConfiguration = createServiceConfiguration(TIMEOUT);
    requestContextNode = TestUtil.createRequestContext(tid,
        "{\"newBook\":{\"price\":100,\"name\": \"The Book\",\"id\":\"book-1\"}}",
        String.format("{ \"user_channel\": \"%s\" }", USER_CHANNEL));

    //GraphQLContext testGraphQLContext = createGraphQLContext(serverRequest);

    List<String> chapters = Arrays.asList("Chapter-1", "Chapter-2", "Chapter-3");

    Map<String, Object> newBook = new HashMap<>();
    newBook.put("id", "book-1");
    newBook.put("name", "The Book");
    newBook.put("price", BigDecimal.valueOf(100.00)); // graphQL uses BigDecimal
    newBook.put("chapters", chapters); // graphQL uses BigDecimal
    newBook.put("isPublished", Boolean.TRUE); // graphQL uses BigDecimal
    newBook.put("isHardPrint", Boolean.FALSE); // graphQL uses BigDecimal
    newBook.put("author", null); // graphQL uses BigDecimal

    Map<String, Object> newBook2 = new HashMap<>();
    newBook2.put("id", "book-2");
    newBook2.put("name", "2nd Book");
    newBook2.put("price", BigDecimal.valueOf(100.00));
    newBook2.put("chapters", chapters);
    newBook2.put("isPublished", Boolean.TRUE);
    newBook2.put("isHardPrint", Boolean.FALSE);
    newBook2.put("author", null);

    Map<String, Object> addBookSingleArgument = new HashMap<>();
    addBookSingleArgument.put("newBook", newBook);

    Map<String, Object> addBookMultipleArguments = new HashMap<>();
    addBookMultipleArguments.put("newBook1", newBook);
    addBookMultipleArguments.put("newBook2", newBook2);

    svcEvaluatorReq4Post = TestUtil.toRequestContext(addBookSingleArgument, EMPTY_MAP, EMPTY_MAP);
    svcEvaluatorReqBodyAnnotation = TestUtil.toRequestContext(addBookSingleArgument, EMPTY_MAP, EMPTY_MAP);
    svcEvaluatorReqNoKeyBodyAnnotation = TestUtil.toRequestContext(addBookSingleArgument, EMPTY_MAP, EMPTY_MAP);
    svcEvaluatorReq4PostWithMultiArg = TestUtil.toRequestContext(addBookMultipleArguments, EMPTY_MAP, EMPTY_MAP);


  }

}
