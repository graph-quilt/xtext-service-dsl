package com.intuit.service.dsl;

import static java.util.Collections.EMPTY_MAP;

import com.intuit.dsl.service.Service;
import com.intuit.service.dsl.evaluator.ServiceEvaluatorRequest;
import com.intuit.service.dsl.utils.TestUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import reactor.util.context.Context;

public class BookByIdService {

  public static String bookByIdDSL;
  public static String bookByIdDSLErrStringNokey;
  public static String bookByIdDSLErrArrayNokey;
  public static String bookByIdDSLErrPathParamNotString;
  public static String bookByIdDSLErrPathParamNull;
  public static String bookByIdDSLErrNoServiceNode;

  public static ServiceEvaluatorRequest svcEvaluatorReq4GetWithParam;
  public static ServiceEvaluatorRequest svcEvaluatorReqErrStringNokey;
  public static ServiceEvaluatorRequest svcEvaluatorReqErrArrayNokey;
  public static ServiceEvaluatorRequest svcEvaluatorReqErrPathParamNotString;
  public static ServiceEvaluatorRequest svcEvaluatorReqErrPathParamNull;
  public static ServiceEvaluatorRequest svcEvaluatorReqErrNoServiceNode;
  public static ServiceEvaluatorRequest svcEvaluatorReq4GetWithParamMultiArg;

  static {
    bookByIdDSL = TestUtil.loadResourceFromFile("bookById/service.service");
    bookByIdDSLErrStringNokey = TestUtil.loadResourceFromFile("bookById/service-err-string-nokey.service");
    bookByIdDSLErrArrayNokey = TestUtil.loadResourceFromFile("bookById/service-err-array-nokey.service");
    bookByIdDSLErrPathParamNotString = TestUtil.loadResourceFromFile("bookById/service-err-param-not-string.service");
    bookByIdDSLErrPathParamNull = TestUtil.loadResourceFromFile("bookById/service-err-param-null.service");
    bookByIdDSLErrNoServiceNode = "";

    List<String> chapters = Arrays.asList("Chapter-1", "Chapter-2", "Chapter-3");
    Map<String, Object> bookByIdArguments = new HashMap<>();
    bookByIdArguments.put("id", "book-1");

    Map<String, Object> bookByIdMultiArguments = new HashMap<>();
    bookByIdMultiArguments.put("id", "book-1");
    bookByIdMultiArguments.put("chapters", chapters);

    //noinspection unchecked
    final Service service = TestUtil.getService("main/flow/service.service",
        BookByIdService.bookByIdDSL, null);
    svcEvaluatorReq4GetWithParam = ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .serviceName(Optional.empty())
        .service(service)
        .serviceId("TEST_BOOK_SVC")
        .inputMap(TestUtil.toRequestContext(bookByIdArguments,EMPTY_MAP, EMPTY_MAP))
        .build();

    final Service bookByIdDSLErrStringNokeyService = TestUtil.getService("main/flow/service.service",
        BookByIdService.bookByIdDSLErrStringNokey, null);
    svcEvaluatorReqErrStringNokey = ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .serviceName(Optional.empty())
        .service(bookByIdDSLErrStringNokeyService)
        .serviceId("TEST_BOOK_SVC")
        .inputMap(TestUtil.toRequestContext(bookByIdArguments,EMPTY_MAP, EMPTY_MAP))
        .build();

    final Service svcEvaluatorReqErrArrayNokeyService = TestUtil.getService("main/flow/service.service",
        BookByIdService.bookByIdDSLErrArrayNokey, null);
    svcEvaluatorReqErrArrayNokey = ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .serviceName(Optional.empty())
        .serviceId("TEST_BOOK_SVC")
        .service(svcEvaluatorReqErrArrayNokeyService)
        .inputMap(TestUtil.toRequestContext(bookByIdMultiArguments,EMPTY_MAP, EMPTY_MAP))
        .build();

    final Service errPathParamNotStringService = TestUtil.getService("main/flow/service.service",
        BookByIdService.bookByIdDSLErrPathParamNotString, null);
    svcEvaluatorReqErrPathParamNotString = ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .serviceName(Optional.empty())
        .service(errPathParamNotStringService)
        .inputMap(TestUtil.toRequestContext(bookByIdMultiArguments,EMPTY_MAP, EMPTY_MAP))
        .serviceId("TEST_BOOK_SVC")
        .build();


    final Service errPathParamNullService = TestUtil.getService("main/flow/service.service",
        BookByIdService.bookByIdDSLErrPathParamNull, null);
    svcEvaluatorReqErrPathParamNull = ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .serviceName(Optional.empty())
        .service(errPathParamNullService)
        .inputMap(TestUtil.toRequestContext(bookByIdMultiArguments,EMPTY_MAP, EMPTY_MAP))
        .serviceId("TEST_BOOK_SVC")
        .build();

//    final Service errNoServiceNodeService = TestUtil.getService("main/flow/service.service",
//        BookByIdService.bookByIdDSLErrNoServiceNode, null);
//    svcEvaluatorReqErrNoServiceNode = ServiceEvaluatorRequest.builder()
//        .reactorContext(Context.empty())
//        .serviceName(Optional.empty())
//        .service(errNoServiceNodeService)
//        .inputMap(TestUtil.toRequestContext(bookByIdMultiArguments,EMPTY_MAP, EMPTY_MAP))
//          .serviceId("TEST_BOOK_SVC")
//        .build();

    final Service getWithParamMultiArgService = TestUtil.getService("main/flow/service.service",
        BookByIdService.bookByIdDSL, null);
    svcEvaluatorReq4GetWithParamMultiArg = ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .serviceName(Optional.empty())
        .service(getWithParamMultiArgService)
        .inputMap(TestUtil.toRequestContext(bookByIdMultiArguments,EMPTY_MAP, EMPTY_MAP))
        .serviceId("TEST_BOOK_SVC")
        .build();

  }

}
