package com.toannq.expressionevaluator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

  @Test
  void evaluate_singleValueExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1");
    var input = (1 << 1);
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedBits());
  }

  @Test
  void evaluate_singleValueExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1");
    var input = (1 << 2);
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedBits());
  }

  @Test
  void evaluate_simpleMatchingOrExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1 || 2");
    var input = (1 << 1);
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedBits());
  }

  @Test
  void evaluate_simpleNonMatchingOrExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1 || 2");
    var input = (1 << 3);
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedBits());
  }

  @Test
  void evaluate_simpleMatchingAndExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1 & 2");
    var input = (1 << 1) | (1 << 2);
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    var expectedUsed = (1 << 1) | (1 << 2);
    assertEquals(expectedUsed, result.usedBits());
  }

  @Test
  void evaluate_simpleMatchingAndExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1 & 2");
    var input = (1 << 1);
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedBits());
  }

  @Test
  void evaluate_operatorPrecedenceExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1 || 2 & 3");
    var input = (1 << 1);
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedBits());
    input = (1 << 1) | (1 << 2);
    result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedBits());
  }

  @Test
  void evaluate_operatorPrecedenceExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1 || 2 & 3");
    var input = (1 << 3);
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedBits());
  }

  @Test
  void evaluate_complexExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("(1 || 2) & 3");
    var input = (1 << 1) | (1 << 3);
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1) | (1 << 3), result.usedBits());
  }

  @Test
  void evaluate_complexExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("(1 || 2) & 3");
    var input = (1 << 2);
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedBits());
  }

  @Test
  void evaluate_complexAndOrExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("(1 & 2) || 3");
    var input = (1 << 1) | (1 << 2);
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1) | (1 << 2), result.usedBits());
  }

  @Test
  void evaluate_complexAndOrExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("(1 & 2) || 3");
    var input = 1 << 4;
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedBits());
  }
}