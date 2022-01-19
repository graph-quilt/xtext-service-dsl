package com.intuit.service.dsl.evaluator.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intuit.dsl.service.Model;
import com.intuit.dsl.service.Service;
import com.intuit.service.dsl.JsonUtils;
import com.intuit.service.dsl.evaluator.exceptions.ServiceEvaluatorException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.springframework.http.HttpCookie;
import org.springframework.web.reactive.function.server.ServerRequest;

public class ServiceEvaluatorUtils {

  private ServiceEvaluatorUtils() {
  }

//  private static Map<String, String> getCookiesFromServerRequest(ServerRequest serverRequest) {
//    Map<String, String> cookies = new HashMap<>();
//    // CSM uses singleValueMap.
//    Map<String, HttpCookie> inCookies = serverRequest.cookies().toSingleValueMap();
//    inCookies.keySet().forEach(key -> {
//      if (inCookies.get(key).getValue().length() > 0) {
//        cookies.put(key, inCookies.get(key).getValue());
//      }
//    });
//    return cookies;
//  }



  public static Service getService(Resource resource, String serviceName) {
    return getServices(resource).filter(service -> StringUtils.equals(service.getId(), serviceName))
        .findFirst().orElse(getService(resource));
  }

  private static Service getService(Resource resource) {
    return getServices(resource).findFirst()
        .orElseThrow(() -> new ServiceEvaluatorException("Failed to find service node"));
  }

  private static Stream<Service> getServices(Resource resource) {
    return resource.getContents().stream()
        .filter(eObject -> eObject instanceof Model)
        .flatMap(eObject -> ((Model) eObject).getService().stream());
  }

}
