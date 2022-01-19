package com.intuit.service.dsl;

import static java.util.Collections.EMPTY_MAP;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuit.service.dsl.utils.TestUtil;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddBookServicePut {

  public static final String USER_CHANNEL = "SomeUserChannel";

  public static JsonNode requestContextNode;

  public static String addBookDSL;

  public static Map<String, JsonNode> svcEvaluatorReq4Put;
  public static Map<String, JsonNode> svcEvaluatorReq4PutWithMultiArg;
  public static Map<String, JsonNode> svcEvaluatorReq4PutNoArg;

  static {
    String tid = UUID.randomUUID().toString();
    addBookDSL = TestUtil.loadResourceFromFile("addBook/service.service");
    requestContextNode = TestUtil.createRequestContext(tid,
        "{\"newBook\":{\"price\":100,\"name\": \"The Book\",\"id\":\"book-1\"}}",
        String.format("{ \"user_channel\": \"%s\" }", USER_CHANNEL));

    Map<String, Object> newBook = new HashMap<>();
    newBook.put("id", "book-1");
    newBook.put("name", "The Book");
    newBook.put("price", BigDecimal.valueOf(100.00)); // graphQL uses BigDecimal

    Map<String, Object> newBook2 = new HashMap<>();
    newBook2.put("id", "book-2");
    newBook2.put("name", "2nd Book");
    newBook2.put("price", BigDecimal.valueOf(100.00));

    Map<String, Object> addBookSingleArgument = new HashMap<>();
    addBookSingleArgument.put("newBook", newBook);

    Map<String, Object> addBookMultipleArguments = new HashMap<>();
    addBookMultipleArguments.put("newBook1", newBook);
    addBookMultipleArguments.put("newBook2", newBook2);

    svcEvaluatorReq4Put = TestUtil.toRequestContext(addBookSingleArgument, EMPTY_MAP, EMPTY_MAP);
    svcEvaluatorReq4PutWithMultiArg = TestUtil.toRequestContext(addBookMultipleArguments, EMPTY_MAP, EMPTY_MAP);
    svcEvaluatorReq4PutNoArg = TestUtil.toRequestContext(EMPTY_MAP, EMPTY_MAP, EMPTY_MAP);
  }

}
