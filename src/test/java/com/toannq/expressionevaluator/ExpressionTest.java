package com.toannq.expressionevaluator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

  @Test
  void evaluate_singleValueExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1");
    var input = Expression.mask(List.of(1));
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedMask());
  }

  @Test
  void evaluate_singleValueExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1");
    var input = Expression.mask(List.of(2));
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedMask());
  }

  @Test
  void evaluate_simpleMatchingOrExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1 | 2");
    var input = Expression.mask(List.of(1));
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedMask());
  }

  @Test
  void evaluate_simpleNonMatchingOrExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1 | 2");
    var input = Expression.mask(List.of(3));
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedMask());
  }

  @Test
  void evaluate_simpleMatchingAndExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1 & 2");
    var input = Expression.mask(List.of(1, 2));
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    var expectedUsed = (1 << 1) | (1 << 2);
    assertEquals(expectedUsed, result.usedMask());
  }

  @Test
  void evaluate_simpleMatchingAndExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1 & 2");
    var input = Expression.mask(List.of(1));
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedMask());
  }

  @Test
  void evaluate_operatorPrecedenceExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("1 | 2 & 3");
    var input = Expression.mask(List.of(1));
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedMask());
    input = Expression.mask(List.of(1, 2));
    result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1), result.usedMask());
  }

  @Test
  void evaluate_operatorPrecedenceExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("1 | 2 & 3");
    var input = Expression.mask(List.of(3));
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedMask());
  }

  @Test
  void evaluate_complexExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("(1 | 2) & 3");
    var input = Expression.mask(List.of(1, 3));
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1) | (1 << 3), result.usedMask());
  }

  @Test
  void evaluate_complexExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("(1 | 2) & 3");
    var input = Expression.mask(List.of(2));
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedMask());
  }

  @Test
  void evaluate_complexAndOrExpression_returnsMatchedResult() {
    var expression = ExpressionParser.parse("(1 & 2) | 3");
    var input = Expression.mask(List.of(1, 2));
    var result = expression.evaluate(input);
    assertTrue(result.matched());
    assertEquals((1 << 1) | (1 << 2), result.usedMask());
  }

  @Test
  void evaluate_complexAndOrExpression_returnsUnmatchedResult() {
    var expression = ExpressionParser.parse("(1 & 2) | 3");
    var input = Expression.mask(List.of(4));
    var result = expression.evaluate(input);
    assertFalse(result.matched());
    assertEquals(0, result.usedMask());
  }
}