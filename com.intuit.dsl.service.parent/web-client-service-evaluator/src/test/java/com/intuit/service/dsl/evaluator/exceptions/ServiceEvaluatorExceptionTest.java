package com.intuit.service.dsl.evaluator.exceptions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;

public class ServiceEvaluatorExceptionTest {

  @Test
  public void createdNewServiceEvaluatorException() {
    String errMsg = "SomeError Message";
    ServiceEvaluatorException serviceEvaluatorException = new ServiceEvaluatorException(errMsg);
    assertThat(serviceEvaluatorException).hasMessage(errMsg);
  }

  @Test
  public void createdNewServiceEvaluatorWithThrowableException() {
    String errMsg = "SomeError";
    NullPointerException nullPointerException = new NullPointerException("Null");
    ServiceEvaluatorException serviceEvaluatorException = new ServiceEvaluatorException(errMsg, nullPointerException);
    assertThat(serviceEvaluatorException).hasMessage(errMsg);
    assertThat(serviceEvaluatorException).hasCause(nullPointerException);
  }
}
