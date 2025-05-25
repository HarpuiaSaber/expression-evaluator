package com.toannq.expressionevaluator;

import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

  /**
   * Tests for the evaluate method in the Expression interface using ExpressionParser.
   * The ExpressionParser generates an Expression using numbers (<100) and various operators ('||', '&&').
   * Custom inputs are tested to verify the evaluation logic for different expression patterns.
   */

  @Test
  void evaluate_simpleMatchingExpression_returnsMatchedResult() {
    // Arrange
    Expression expression = ExpressionParser.parse("1 || 2"); // Example logical expression
    BitSet input = new BitSet();
    input.set(1); // Represents 1 = true
    // 2 = false, as it's unset

    // Act
    EvaluatedResult result = expression.evaluate(input);

    // Assert
    assertTrue(result.matched());
    BitSet expectedUsed = new BitSet();
    expectedUsed.set(1); // Used value 1
    assertEquals(expectedUsed, result.usedValues());
  }

  @Test
  void evaluate_simpleNonMatchingExpression_returnsUnmatchedResult() {
    // Arrange
    Expression expression = ExpressionParser.parse("1 || 2"); // Example logical expression
    BitSet input = new BitSet();
    // 1 = false, as it's unset
    // 2 = false, as it's unset

    // Act
    EvaluatedResult result = expression.evaluate(input);

    // Assert
    assertFalse(result.matched());
    BitSet expectedUsed = new BitSet();
    assertEquals(expectedUsed, result.usedValues());
  }

  @Test
  void evaluate_complexExpression_returnsMatchedResult() {
    // Arrange
    Expression expression = ExpressionParser.parse("(1 || 2) & 3"); // Example logical expression
    BitSet input = new BitSet();
    input.set(1); // Represents 1 = true
    // 2 = false, as it's unset
    input.set(3); // Represents 3 = true

    // Act
    EvaluatedResult result = expression.evaluate(input);

    // Assert
    assertTrue(result.matched());
    BitSet expectedUsed = new BitSet();
    expectedUsed.set(1); // Used value 1
    expectedUsed.set(3); // Used value 3
    assertEquals(expectedUsed, result.usedValues());
  }

  @Test
  void evaluate_complexExpression_returnsUnmatchedResult() {
    // Arrange
    Expression expression = ExpressionParser.parse("(1 || 2) & 3"); // Example logical expression
    BitSet input = new BitSet();
    input.set(2); // Represents 2 = true
    // 1 = false, as it's unset
    // 3 = false, as it's unset

    // Act
    EvaluatedResult result = expression.evaluate(input);

    // Assert
    assertFalse(result.matched());
    BitSet expectedUsed = new BitSet();
    assertEquals(expectedUsed, result.usedValues());
  }

  @Test
  void evaluate_singleValueExpression_returnsMatchedResult() {
    // Arrange
    Expression expression = ExpressionParser.parse("1"); // Single value expression
    BitSet input = new BitSet();
    input.set(1);

    // Act
    EvaluatedResult result = expression.evaluate(input);

    // Assert
    assertTrue(result.matched());
    BitSet expectedUsed = new BitSet();
    expectedUsed.set(1); // Used value 1
    assertEquals(expectedUsed, result.usedValues());
  }

  @Test
  void evaluate_singleValueExpression_returnsUnmatchedResult() {
    // Arrange
    Expression expression = ExpressionParser.parse("1"); // Single value expression
    BitSet input = new BitSet();
    input.set(2);

    // Act
    EvaluatedResult result = expression.evaluate(input);

    // Assert
    assertFalse(result.matched());
    BitSet expectedUsed = new BitSet();
    assertEquals(expectedUsed, result.usedValues());
  }
}