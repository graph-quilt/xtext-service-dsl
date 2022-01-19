package com.intuit.service.dsl.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.intuit.service.dsl.JsonUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;

public class JsonUtilsTest {

  @Test
  public void canExtractArgumentsWithVariables() {
    AssertionsForClassTypes.assertThat(JsonUtils.MAPPER).isNotNull();
  }

}
