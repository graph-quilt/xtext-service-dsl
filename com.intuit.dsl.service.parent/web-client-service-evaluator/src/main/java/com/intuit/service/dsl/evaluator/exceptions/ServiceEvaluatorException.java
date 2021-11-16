package com.intuit.service.dsl.evaluator.exceptions;

public class ServiceEvaluatorException extends RuntimeException {

  public ServiceEvaluatorException(String msg, Throwable e) {
    super(msg, e);
  }

  public ServiceEvaluatorException(String msg) {
    super(msg);
  }
}
