package com.intuit.service.dsl.resource.loader;

import com.intuit.service.dsl.AddBookServicePut;
import org.assertj.core.api.Assertions;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Assert;
import org.junit.Test;

public class StringContentResourceLoaderTest {

  @Test
  public void test() {
    Resource resource = new StringContentResourceLoader(AddBookServicePut.addBookDSL).getResource();
    Assert.assertNotNull(resource);
  }

  @Test
  public void testBadService() {
    Assertions.assertThatThrownBy(() ->
        new StringContentResourceLoader("foo").getResource()
    ).hasMessageContaining("Issues found in service adapter");
  }
}
