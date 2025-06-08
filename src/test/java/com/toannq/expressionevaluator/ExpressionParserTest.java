package com.toannq.expressionevaluator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {

  @Test
  void testParseSimpleNumber() {
    var expression = "42";
    var result = ExpressionParser.parse(expression);
    assertNotNull(result);
    assertInstanceOf(EqualExpression.class, result);
    assertEquals(42, ((EqualExpression) result).value());
  }

  @Test
  void testParseSimpleOrExpression() {
    var expression = "1 | 2";
    var result = ExpressionParser.parse(expression);
    assertNotNull(result);
    assertInstanceOf(OrExpression.class, result);
    var orExpression = (OrExpression) result;
    assertInstanceOf(EqualExpression.class, orExpression.left());
    assertInstanceOf(EqualExpression.class, orExpression.right());
    assertEquals(1, ((EqualExpression) orExpression.left()).value());
    assertEquals(2, ((EqualExpression) orExpression.right()).value());
  }

  @Test
  void testParseSimpleAndExpression() {
    var expression = "1 & 2";
    var result = ExpressionParser.parse(expression);
    assertNotNull(result);
    assertInstanceOf(AndExpression.class, result);
    var andExpression = (AndExpression) result;
    assertInstanceOf(EqualExpression.class, andExpression.left());
    assertInstanceOf(EqualExpression.class, andExpression.right());
    assertEquals(1, ((EqualExpression) andExpression.left()).value());
    assertEquals(2, ((EqualExpression) andExpression.right()).value());
  }

  @Test
  void testParseOrAndExpression() {
    var expression = "1 | 2 & 3";
    var result = ExpressionParser.parse(expression);
    assertNotNull(result);
    assertInstanceOf(OrExpression.class, result);
    var orExpression = (OrExpression) result;
    assertInstanceOf(EqualExpression.class, orExpression.left());
    assertInstanceOf(AndExpression.class, orExpression.right());
    assertEquals(1, ((EqualExpression) orExpression.left()).value());
    var andExpression = (AndExpression) orExpression.right();
    assertInstanceOf(EqualExpression.class, andExpression.left());
    assertInstanceOf(EqualExpression.class, andExpression.right());
    assertEquals(2, ((EqualExpression) andExpression.left()).value());
    assertEquals(3, ((EqualExpression) andExpression.right()).value());
  }

  @Test
  void testParseParenthesisOrAndExpression() {
    var expression = "(1 | 2) & 3";
    var result = ExpressionParser.parse(expression);
    assertNotNull(result);
    assertInstanceOf(AndExpression.class, result);
    var andExpression = (AndExpression) result;
    assertInstanceOf(OrExpression.class, andExpression.left());
    assertInstanceOf(EqualExpression.class, andExpression.right());
    assertEquals(3, ((EqualExpression) andExpression.right()).value());
    var orExpression = (OrExpression) andExpression.left();
    assertInstanceOf(EqualExpression.class, orExpression.left());
    assertInstanceOf(EqualExpression.class, orExpression.right());
    assertEquals(1, ((EqualExpression) orExpression.left()).value());
    assertEquals(2, ((EqualExpression) orExpression.right()).value());
  }

  @Test
  void testParseComplexExpression() {
    var expression = "(1 & 2) | (3 & (4 | 5))";
    var result = ExpressionParser.parse(expression);
    assertNotNull(result);
    assertInstanceOf(OrExpression.class, result);
    var orExpression = (OrExpression) result;
    assertInstanceOf(AndExpression.class, orExpression.left());
    assertInstanceOf(AndExpression.class, orExpression.right());
    var leftAnd = (AndExpression) orExpression.left();
    assertInstanceOf(EqualExpression.class, leftAnd.left());
    assertInstanceOf(EqualExpression.class, leftAnd.right());
    assertEquals(1, ((EqualExpression) leftAnd.left()).value());
    assertEquals(2, ((EqualExpression) leftAnd.right()).value());
    var rightAnd = (AndExpression) orExpression.right();
    assertInstanceOf(EqualExpression.class, rightAnd.left());
    assertInstanceOf(OrExpression.class, rightAnd.right());
    assertEquals(3, ((EqualExpression) rightAnd.left()).value());
    var nestedOr = (OrExpression) rightAnd.right();
    assertInstanceOf(EqualExpression.class, nestedOr.left());
    assertInstanceOf(EqualExpression.class, nestedOr.right());
    assertEquals(4, ((EqualExpression) nestedOr.left()).value());
    assertEquals(5, ((EqualExpression) nestedOr.right()).value());
  }

  @Test
  void testParseInvalidExpression_misingClosingParenthesis() {
    var expression = "((1 & 2) | (3 & (4 | 5))";
    var exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(expression));
    assertEquals("Mismatched close parentheses", exception.getMessage());
  }

  @Test
  void testParseInvalidExpression_misingOpeningParenthesis() {
    var expression = "(1 & 2) | (3 & (4 | 5)))";
    var exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(expression));
    assertEquals("Mismatched open parentheses", exception.getMessage());
  }

  @Test
  void testParseInvalidExpression_notAllowedCharacters() {
    var expression = "(1 | 2)a";
    var exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(expression));
    assertEquals("Unexpected character: a", exception.getMessage());
  }

  @Test
  void testParseEmptyExpression() {
    var expression = "";
    var exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(expression));
    assertEquals("Expression cannot be null or empty", exception.getMessage());
  }

  @Test
  void testParseNullExpression() {
    var exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(null));
    assertEquals("Expression cannot be null or empty", exception.getMessage());
  }

  @Test
  void testParseBlankExpression() {
    var expression = "    ";
    var exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(expression));
    assertEquals("Expression cannot be blank", exception.getMessage());
  }
}