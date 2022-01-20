package com.intuit.service.dsl.resource.loader;

import com.google.inject.Injector;
import com.intuit.dsl.ServiceStandaloneSetupGenerated;
import com.intuit.service.dsl.evaluator.exceptions.ServiceEvaluatorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.springframework.util.CollectionUtils;


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
      List<Issue> issues = validate(xtextResource);
      if (!CollectionUtils.isEmpty(issues)) {
        String message = issues.stream().map(i -> StringUtils.join(i.getLineNumber(),":",i.getMessage()))
            .reduce("", (s, s2) -> StringUtils.joinWith( "\n",s, s2));
        throw new ServiceEvaluatorException("Issues found in service adapter - " + message);
      }
    } catch (IOException e) {
      throw new ServiceEvaluatorException("IO Error while loading DSL xtext resource.", e);
    }
  }

  @Override
  public Resource getResource() {
    return xtextResource;
  }

  private List<Issue> validate(XtextResource xtextResource) {
    IResourceValidator validator = xtextResource.getResourceServiceProvider().getResourceValidator();
    return validator.validate(xtextResource, CheckMode.ALL, CancelIndicator.NullImpl);
  }
}
