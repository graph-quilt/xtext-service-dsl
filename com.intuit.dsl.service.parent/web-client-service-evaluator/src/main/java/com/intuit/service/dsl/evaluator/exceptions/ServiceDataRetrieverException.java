package com.intuit.service.dsl.evaluator.exceptions;

/**
 * This class is thrown when non 2XX status code is returned by the server.
 */
public class ServiceDataRetrieverException extends RuntimeException {
  public ServiceDataRetrieverException(int  statusCode, String body) {
    super("Server error encountered.  statusCode=" + statusCode + " responseBody=" + body);
  }
}
