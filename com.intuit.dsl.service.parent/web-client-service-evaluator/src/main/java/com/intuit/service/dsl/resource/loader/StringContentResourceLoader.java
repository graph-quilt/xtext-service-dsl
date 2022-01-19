package com.intuit.service.dsl.resource.loader;

import com.google.inject.Injector;
import com.intuit.dsl.ServiceStandaloneSetupGenerated;
import com.intuit.service.dsl.evaluator.exceptions.ServiceEvaluatorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;


public class StringContentResourceLoader implements ServiceResourceLoader {

  private XtextResource xtextResource = null;
  private static Injector serviceInjector = new ServiceStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
  private String dslContent;

  public StringContentResourceLoader(String dslContent) {
    this.dslContent = dslContent;
    loadFlowResources();
  }

  private void loadFlowResources() {
    URI uri = URI.createFileURI("service.service");
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.dslContent.getBytes());
    xtextResource = (XtextResource) serviceInjector.getInstance(IResourceFactory.class)
        .createResource(uri);
    try {
      xtextResource.load(byteArrayInputStream, null);
    } catch (IOException e) {
      throw new ServiceEvaluatorException("IO Error while loading DSL xtext resource.", e);
    }
  }

  @Override
  public Resource getResource() {
    return xtextResource;
  }
}
