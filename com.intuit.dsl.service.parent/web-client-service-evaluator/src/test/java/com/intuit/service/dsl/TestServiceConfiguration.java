package com.intuit.service.dsl;

import com.intuit.service.dsl.evaluator.ServiceConfiguration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration lookup help per Service Provider.
 */
public class TestServiceConfiguration implements ServiceConfiguration {

  private final Map<String, String> properties = new HashMap<>();

  public TestServiceConfiguration(Map<String, String> properties) {
    if (properties != null)
      this.properties.putAll(properties);
  }

  public void add(String key, String value) {
    properties.put(key, value);
  }

  public String get(String propertyName) {
    return this.properties.get(propertyName);
  }
}