package com.intuit.service.dsl.evaluator;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuit.dsl.service.Property;
import com.intuit.dsl.service.PropertyExpresssion;
import com.intuit.dsl.service.SchemaVariable;
import com.intuit.dsl.service.ServiceFactory;
import com.intuit.dsl.service.StringLiteral;
import com.intuit.dsl.service.Variable;
import com.intuit.dsl.service.VariableReference;
import com.intuit.service.dsl.AddBookServicePost;
import com.intuit.service.dsl.utils.TestUtil;
import com.intuit.service.dsl.evaluator.expression.ExpressionEvaluator;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class ExpressionEvaluatorTest {

  private ExpressionEvaluator expressionEvaluator;
  private ExpressionEvaluator expressionEvaluatorWithNoInput;
  private ExpressionEvaluator expressionEvaluatorWithEmpty;

  @Before
  public void setup() {
    ServiceConfiguration serviceConfiguration = TestUtil.createServiceConfiguration(TestUtil.TIMEOUT, 0);

    Map<String, JsonNode> inputMap = new HashMap<>();
    inputMap.put("requestContext", AddBookServicePost.requestContextNode);

    expressionEvaluator = ExpressionEvaluator.builder()
        .inputMap(inputMap)
        .serviceConfiguration(serviceConfiguration)
        .build();

    expressionEvaluatorWithNoInput = ExpressionEvaluator.builder()
        .serviceConfiguration(serviceConfiguration)
        .build();

    expressionEvaluatorWithEmpty = ExpressionEvaluator.builder()
        .inputMap(new HashMap<>())
        .serviceConfiguration(serviceConfiguration)
        .build();
  }

  @Test
  public void canEvaluatePropertyExpressions() {
    // GIVEN
    Property property = ServiceFactory.eINSTANCE.createProperty();
    property.setKey("timeout");

    PropertyExpresssion propertyExpresson = ServiceFactory.eINSTANCE.createPropertyExpresssion();
    propertyExpresson.setRef(property);

    // WHEN
    JsonNode result = expressionEvaluator.evaluate(propertyExpresson);

    // THEN
    assertThat(Integer.parseInt(result.asText())).isEqualTo(TestUtil.TIMEOUT);
  }

  @Test
  public void canEvaluateStringLiteral() {
    // GIVEN
    StringLiteral stringLiteral = ServiceFactory.eINSTANCE.createStringLiteral();
    stringLiteral.setValue("SomeLiteralValue");

    // WHEN
    JsonNode result = expressionEvaluator.evaluate(stringLiteral);

    // THEN
    assertThat(result.asText()).isEqualTo("SomeLiteralValue");
  }

  @Test
  public void canEvaluateProperty() {
    // GIVEN
    Property property = ServiceFactory.eINSTANCE.createProperty();
    property.setKey("timeout");

    // WHEN
    JsonNode result = expressionEvaluator.evaluate(property);

    // THEN
    assertThat(Integer.parseInt(result.asText())).isEqualTo(TestUtil.TIMEOUT);
  }

  @Test
  public void canEvaluateVariableReference() {

    SchemaVariable schemaVariable = ServiceFactory.eINSTANCE.createSchemaVariable();
    schemaVariable.getKey().add("requestContext");
    schemaVariable.getKey().add("headers");
    schemaVariable.getKey().add("user_channel");

    Variable variable = ServiceFactory.eINSTANCE.createVariable();
    variable.setSchemaVariable(schemaVariable);

    VariableReference variablerReference = ServiceFactory.eINSTANCE.createVariableReference();
    variablerReference.setRef(variable);

    // WHEN and THEN
    assertThat(expressionEvaluator.evaluate(schemaVariable).asText()).isEqualTo(AddBookServicePost.USER_CHANNEL);
    assertThat(expressionEvaluator.evaluate(variable).asText()).isEqualTo(AddBookServicePost.USER_CHANNEL);
    assertThat(expressionEvaluator.evaluate(variablerReference).asText()).isEqualTo(AddBookServicePost.USER_CHANNEL);
  }

  @Test
  public void cannotEvaluateVariableNotPresentInRequestContext() {
    SchemaVariable schemaVariable = ServiceFactory.eINSTANCE.createSchemaVariable();
    schemaVariable.getKey().add("requestContext");
    schemaVariable.getKey().add("headers");
    schemaVariable.getKey().add("not_in_request");

    Variable variable = ServiceFactory.eINSTANCE.createVariable();
    variable.setSchemaVariable(schemaVariable);

    // WHEN and THEN
    assertThat(expressionEvaluator.evaluate(variable)).isNull();
  }

  @Test
  public void cannotEvaluateWithoutInputMap() {
    // GIVEN
    SchemaVariable schemaVariable = ServiceFactory.eINSTANCE.createSchemaVariable();
    schemaVariable.getKey().add("requestContext");
    schemaVariable.getKey().add("headers");
    schemaVariable.getKey().add("not_in_request");

    Variable variable = ServiceFactory.eINSTANCE.createVariable();
    variable.setSchemaVariable(schemaVariable);

    // WHEN and THEN
    assertThat(expressionEvaluatorWithNoInput.evaluate(variable)).isNull();
  }

  @Test
  public void cannotEvaluateWithoutRequestContext() {
    // GIVEN
    SchemaVariable schemaVariable = ServiceFactory.eINSTANCE.createSchemaVariable();
    schemaVariable.getKey().add("requestContext");
    schemaVariable.getKey().add("headers");
    schemaVariable.getKey().add("not_in_request");

    Variable variable = ServiceFactory.eINSTANCE.createVariable();
    variable.setSchemaVariable(schemaVariable);

    // WHEN and THEN
    assertThat(expressionEvaluatorWithEmpty.evaluate(variable)).isNull();
  }

}
