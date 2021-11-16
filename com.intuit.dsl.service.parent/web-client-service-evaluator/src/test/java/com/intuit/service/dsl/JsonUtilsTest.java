package com.intuit.service.dsl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;

public class JsonUtilsTest {

  @Test
  public void canExtractArgumentsWithVariables() {
    assertThat(JsonUtils.MAPPER).isNotNull();
  }

}
