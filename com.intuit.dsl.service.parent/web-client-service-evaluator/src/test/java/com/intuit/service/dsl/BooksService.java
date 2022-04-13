package com.intuit.service.dsl;

import static java.util.Collections.EMPTY_MAP;

import com.google.common.collect.ImmutableMap;
import com.intuit.dsl.service.Service;
import com.intuit.service.dsl.evaluator.ServiceEvaluatorRequest;
import com.intuit.service.dsl.utils.TestUtil;
import java.util.Optional;
import reactor.util.context.Context;

public class BooksService {

  public static String booksDSL;

  // a ServiceEvaluatorRequest must be created for each scenario.  tests are run
  // in parallel in CI/CD
  public static ServiceEvaluatorRequest svcEvaluatorReq4GETNoParam;
  public static ServiceEvaluatorRequest svcEvaluatorReq4InvalidDSL;
  public static ServiceEvaluatorRequest svcEvaluatorReq4NoRequestTypeInSvcEvaluator;

  static {
    booksDSL = TestUtil.loadResourceFromFile("books/service.service");

    final Service service = TestUtil.getService("main/flow/service.service",
        booksDSL, null);
    svcEvaluatorReq4GETNoParam = ServiceEvaluatorRequest.builder()
        .reactorContext(Context.empty())
        .serviceName(Optional.empty())
        .service(service)
        .inputMap(TestUtil.toRequestContext(ImmutableMap.of("limit", "10"),EMPTY_MAP, EMPTY_MAP))
        .namespace("TEST_BOOK_SVC")
        .build();

    //noinspection unchecked
//    svcEvaluatorReq4InvalidDSL = ServiceEvaluatorRequest.builder()
//        .reactorContext(Context.empty())
//        .serviceName(Optional.empty())
//        .service(service)
//        .inputMap(TestUtil.toRequestContext(ImmutableMap.of("limit", "10"),EMPTY_MAP, EMPTY_MAP))
//        .namespace("TEST_BOOK_SVC")
//        .build();
//
//
//    svcEvaluatorReq4InvalidDSL = ServiceEvaluatorRequest.builder()
//        .reactorContext(Context.empty())
//        .cookies(Collections.EMPTY_MAP)
//        .headers(Collections.EMPTY_MAP)
//        .arguments(ImmutableMap.of("limit", "10"))
//        .namespace("TEST_BOOK_SVC")
//        .build();
//
//    svcEvaluatorReq4NoRequestTypeInSvcEvaluator = ServiceEvaluatorRequest.builder()
//        .reactorContext(Context.empty())
//        .cookies(Collections.EMPTY_MAP)
//        .headers(Collections.EMPTY_MAP)
//        .arguments(ImmutableMap.of("limit", "10"))
//        .namespace("TEST_BOOK_SVC")
//        .build();
  }

}
