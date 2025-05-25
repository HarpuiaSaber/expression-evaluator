package com.toannq.expressionevaluator;

import java.nio.charset.StandardCharsets;

public class ExpressionParser {
  public static final byte BIT_OR = '|';
  public static final byte LOGICAL_AND_OPERATOR = '&';
  public static final byte OPEN_PARENTHESIS = '(';
  public static final byte CLOSE_PARENTHESIS = ')';
  private final byte[] expression;
  private int pos;

  private ExpressionParser(String expression) {
    this.expression = expression.replaceAll("\\s+", "").getBytes(StandardCharsets.ISO_8859_1);
    this.pos = 0;
  }

  public static Expression parse(String expression) {
    return new ExpressionParser(expression).parse();
  }

  private Expression parse() {
    return parseOrExpression();
  }

  private boolean match(byte b) {
    if (pos < expression.length && expression[pos] == b) {
      pos++;
      return true;
    }
    return false;
  }

  private void expect(byte b) {
    if (!match(b)) {
      throw new RuntimeException("Expected " + (char) b + " at position " + pos);
    }
  }

  private Expression parseOrExpression() {
    var result = parseAndExpression();
    while (matchOrOperator()) {
      result = new OrExpression(result, parseOperand());
    }
    return result;
  }

  private boolean matchOrOperator() {
    var nextPos = pos + 1;
    if (nextPos < expression.length && expression[pos] == BIT_OR && expression[nextPos] == BIT_OR) {
      pos = nextPos + 1;
      return true;
    }
    return false;
  }

  private Expression parseAndExpression() {
    var result = parseOperand();
    while (matchAndOperator()) {
      result = new AndExpression(result, parseOperand());
    }
    return result;
  }

  private boolean matchAndOperator() {
    return match(LOGICAL_AND_OPERATOR);
  }

  private Expression parseOperand() {
    if (match(OPEN_PARENTHESIS)) {
      var result = parseOrExpression();
      expect(CLOSE_PARENTHESIS);
      return result;
    }
    return new EqualExpression(parseNumber());
  }

  private int parseNumber() {
    var i = pos;
    var value = 0;
    while (i < expression.length && isDigit(expression[i])) {
      value = value * 10 + (expression[i] - '0');
      i++;
    }
    if (pos != i) {
      pos = i;
      return value;
    }
    throw new RuntimeException("Expected number at position " + i);
  }

  private boolean isDigit(byte b) {
    return '0' <= b && b <= '9';
  }


}
