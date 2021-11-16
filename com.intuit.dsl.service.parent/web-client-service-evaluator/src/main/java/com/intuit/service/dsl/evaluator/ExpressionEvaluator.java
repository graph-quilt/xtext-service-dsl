package com.intuit.service.dsl.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.intuit.dsl.service.Property;
import com.intuit.dsl.service.PropertyExpresssion;
import com.intuit.dsl.service.SchemaVariable;
import com.intuit.dsl.service.StringLiteral;
import com.intuit.dsl.service.Variable;
import com.intuit.dsl.service.VariableReference;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

public class ExpressionEvaluator {

  private Map<String, JsonNode> inputMap;
  private ServiceConfiguration serviceConfiguration;

  @Builder
  public ExpressionEvaluator(Map<String, JsonNode> inputMap, ServiceConfiguration serviceConfiguration) {
    this.inputMap = inputMap;
    this.serviceConfiguration = serviceConfiguration;
  }

  private JsonNode evaluate(StringLiteral string) {
    return new TextNode(string.getValue());
  }

  private JsonNode evaluate(VariableReference variableRef) {
    return evaluate(variableRef.getRef());
  }

  private JsonNode evaluate(Variable variable) {
    SchemaVariable schemaVariable = variable.getSchemaVariable();
    return this.evaluate(schemaVariable);
  }

  private JsonNode evaluate(SchemaVariable schemaVariable) {
    return this
        .evaluate(schemaVariable.getKey(), getNodeFromInputMap(IterableExtensions.head(schemaVariable.getKey())));
  }

  private JsonNode evaluate(PropertyExpresssion property) {
    return this.evaluate(property.getRef());
  }

  private JsonNode evaluate(Property property) {
    return new TextNode(serviceConfiguration.get(property.getKey()));
  }

  private JsonNode evaluate(EList<String> keys, JsonNode node) {
    // Traverse through the path
    if (Objects.isNull(node)) {
      return null;
    }

    if (Objects.nonNull(keys)) {
      for (int i = 1; i < keys.size(); i++) {
        node = node.get(keys.get(i));
        // if any node is null return null
        if (Objects.isNull(node)) {
          return null;
        }
      }
    }
    return node;
  }

  private JsonNode getNodeFromInputMap(String key) {
    if (inputMap == null) {
      return null;
    }
    JsonNode keyNode = this.inputMap.get(key);
//    if (Objects.nonNull(key.getExp())) {
//      JsonNode value = evaluate(key.getExp());
//      return keyNode.get(value.asText());
//    } else {
    return keyNode;
    //}
  }

  public JsonNode evaluate(EObject eObject) {
    if (eObject instanceof PropertyExpresssion) {
      return this.evaluate((PropertyExpresssion) eObject);
    } else if (eObject instanceof StringLiteral) {
      return this.evaluate((StringLiteral) eObject);
    } else if (eObject instanceof Variable) {
      return this.evaluate((Variable) eObject);
    } else if (eObject instanceof VariableReference) {
      return this.evaluate((VariableReference) eObject);
    } else if (eObject instanceof Property) {
      return this.evaluate((Property) eObject);
    } else if (eObject instanceof SchemaVariable) {
      return this.evaluate((SchemaVariable) eObject);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " + Collections
          .singletonList(eObject).toString());
    }
  }
}
