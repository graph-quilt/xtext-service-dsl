package com.intuit.service.dsl.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuit.dsl.service.Service;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import reactor.util.context.Context;

@Builder
@EqualsAndHashCode
@ToString
@Getter
public class ServiceEvaluatorRequest {

  private Map<String, JsonNode> inputMap;
  @Builder.Default
  private Optional<String> serviceName = Optional.empty();
  @NonNull
  private String serviceId;
  @NonNull
  private Context reactorContext;
  @NonNull
  private Service service;
}
