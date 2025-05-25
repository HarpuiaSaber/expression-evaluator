package com.toannq.expressionevaluator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {

  /**
   * Tests for the {@link ExpressionParser#parse(String)} method.
   * This method is expected to parse a given logical expression string into an {@link Expression} object.
   */

  @Test
  void testParseSimpleNumber() {
    // Arrange
    String expression = "42";

    // Act
    Expression result = ExpressionParser.parse(expression);

    // Assert
    assertNotNull(result, "The result should not be null.");
    assertTrue(result instanceof EqualExpression, "The result should be an instance of EqualExpression.");
    assertEquals(42, ((EqualExpression) result).getValue(), "The value of the parsed expression should be 42.");
  }

  @Test
  void testParseSimpleOrExpression() {
    // Arrange
    String expression = "1 || 2";

    // Act
    Expression result = ExpressionParser.parse(expression);

    // Assert
    assertNotNull(result, "The result should not be null.");
    assertTrue(result instanceof OrExpression, "The result should be an instance of OrExpression.");
    OrExpression orExpression = (OrExpression) result;

    assertTrue(orExpression.getLeft() instanceof EqualExpression, "The left operand should be an EqualExpression.");
    assertTrue(orExpression.getRight() instanceof EqualExpression, "The right operand should be an EqualExpression.");

    assertEquals(1, ((EqualExpression) orExpression.getLeft()).getValue(), "Left operand should be 1.");
    assertEquals(2, ((EqualExpression) orExpression.getRight()).getValue(), "Right operand should be 2.");
  }

  @Test
  void testParseSimpleAndExpression() {
    // Arrange
    String expression = "1 & 2";

    // Act
    Expression result = ExpressionParser.parse(expression);

    // Assert
    assertNotNull(result, "The result should not be null.");
    assertTrue(result instanceof AndExpression, "The result should be an instance of OrExpression.");
    AndExpression andExpression = (AndExpression) result;

    assertTrue(andExpression.getLeft() instanceof EqualExpression, "The left operand should be an EqualExpression.");
    assertTrue(andExpression.getRight() instanceof EqualExpression, "The right operand should be an EqualExpression.");

    assertEquals(1, ((EqualExpression) andExpression.getLeft()).getValue(), "Left operand should be 1.");
    assertEquals(2, ((EqualExpression) andExpression.getRight()).getValue(), "Right operand should be 2.");
  }

  @Test
  void testParseExpressionWithParentheses() {
    // Arrange
    String expression = "1 || 2 & 3";

    // Act
    Expression result = ExpressionParser.parse(expression);

    // Assert
    assertNotNull(result, "The result should not be null.");
    assertTrue(result instanceof OrExpression, "The result should be an instance of OrExpression.");
  }

  @Test
  void testParseInvalidExpression() {
    // Arrange
    String expression = "(1 || 2";

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> ExpressionParser.parse(expression),
        "An exception should be thrown for an invalid expression.");
    assertTrue(exception.getMessage().contains("Expected"),
        "The exception message should indicate a missing parenthesis.");
  }

  @Test
  void testParseEmptyExpression() {
    // Arrange
    String expression = "";

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> ExpressionParser.parse(expression),
        "An exception should be thrown for an empty expression.");
    assertTrue(exception.getMessage().contains("Expected"),
        "The exception message should indicate the invalid input.");
  }

  @Test
  void testParseComplexExpression() {
    // Arrange
    String expression = "(1 & 2) || (3 & (4 || 5))";

    // Act
    Expression result = ExpressionParser.parse(expression);

    // Assert
    assertNotNull(result, "The result should not be null.");
    assertTrue(result instanceof OrExpression, "The result should be an instance of OrExpression.");
  }
}