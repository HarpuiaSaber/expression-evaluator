package com.toannq.expressionevaluator;

import java.nio.charset.StandardCharsets;

public final class ExpressionParser {
  private static final byte BIT_OR = '|';
  private static final byte BIT_AND = '&';
  private static final byte OPEN_PARENTHESIS = '(';
  private static final byte CLOSE_PARENTHESIS = ')';
  private static final int INITIAL_CAPACITY = 1;

  private final byte[] expression;
  private byte[] operatorStack;
  private Expression[] expressionStack;
  private int pos;
  private int operatorPos;
  private int expressionPos;

  /**
   * Parses the given string expression and returns an {@link Expression} tree
   * representing the parsed logical expression.
   *
   * @param expression the string expression to parse; must not be null or empty
   * @return an {@link Expression} object representing the parsed logical or arithmetic expression
   * @throws IllegalArgumentException if the expression is null, empty, contains
   *                                  invalid characters, or has mismatched parentheses
   */
  public static Expression parse(String expression) {
    return parse(expression, INITIAL_CAPACITY);
  }

  /**
   * Parses the given string expression and returns an {@link Expression} tree
   * representing the parsed logical expression.
   *
   * @param expression      the string expression to parse; must not be null or empty
   * @param initialCapacity the initial capacity for the internal parser stacks
   * @return an {@link Expression} object representing the parsed expression
   * @throws IllegalArgumentException if the expression is null, empty, contains
   *                                  invalid characters, or has mismatched parentheses
   */
  public static Expression parse(String expression, int initialCapacity) {
    if (expression == null || expression.isEmpty()) {
      throw new IllegalArgumentException("Expression cannot be null or empty");
    }
    return new ExpressionParser(expression, initialCapacity).parse();
  }

  private ExpressionParser(String expression, int initialCapacity) {
    this.expression = expression.getBytes(StandardCharsets.ISO_8859_1);
    this.operatorStack = new byte[initialCapacity];
    this.expressionStack = new Expression[initialCapacity + 1];
    this.pos = 0;
    this.operatorPos = -1;
    this.expressionPos = -1;
  }

  /**
   * Adds an {@link Expression} to the internal stack, expanding the stack's capacity if needed.
   *
   * @param expr the {@link Expression} to be added to the stack
   */
  private void pushExpression(Expression expr) {
    if (++expressionPos == expressionStack.length) {
      var copy = new Expression[expressionStack.length * 2];
      System.arraycopy(expressionStack, 0, copy, 0, expressionStack.length);
      expressionStack = copy;
    }
    expressionStack[expressionPos] = expr;
  }

  /**
   * Pops and returns the {@link Expression} at the top of the expression stack.
   * The method decreases the expression stack's position pointer in this process.
   *
   * @return the {@link Expression} at the top of the stack
   */
  Expression popExpression() {
    return expressionStack[expressionPos--];
  }

  /**
   * Retrieves the {@link Expression} currently at the top of the expression stack.
   *
   * @return the {@link Expression} object located at the top of the expression stack
   */
  Expression peekExpression() {
    return expressionStack[expressionPos];
  }

  /**
   * Adds an operator to the internal stack, expanding the stack's capacity if needed.
   *
   * @param op the operator to be added to the stack, represented as a byte
   */
  void pushOperator(byte op) {
    if (++operatorPos == operatorStack.length) {
      var copy = new byte[operatorStack.length * 2];
      System.arraycopy(operatorStack, 0, copy, 0, operatorStack.length);
      operatorStack = copy;
    }
    operatorStack[operatorPos] = op;
  }

  /**
   * Pops and returns the operator at the top of the operator stack.
   * The method decreases the operator stack's position pointer in this process.
   *
   * @return the operator at the top of the stack, represented as a byte
   */
  byte popOperator() {
    return operatorStack[operatorPos--];
  }

  /**
   * Retrieves the operator currently at the top of the operator stack.
   *
   * @return the operator at the top of the stack, represented as a byte
   */
  byte peekOperator() {
    return operatorStack[operatorPos];
  }

  boolean hasOperators() {
    return operatorPos >= 0;
  }

  private Expression parse() {
    byte b;
    for (; pos < expression.length; pos++) {
      b = expression[pos];
      if (b == ' ') {
        continue;
      }
      if (b == OPEN_PARENTHESIS) {
        pushOperator(b);
      } else if (isDigit(b)) {
        consumeNumber();
      } else if (b == CLOSE_PARENTHESIS) {
        reduceGroupExpression();
      } else if (isLogicalOperator(b)) {
        consumeOperator(b);
      } else {
        throw new IllegalArgumentException("Unexpected character: " + (char) b);
      }
    }
    if (expressionPos < 0) {
      throw new IllegalArgumentException("Expression cannot be blank");
    }
    while (hasOperators()) {
      if (peekOperator() == OPEN_PARENTHESIS) {
        throw new IllegalArgumentException("Mismatched close parentheses");
      }
      applyOperator(operatorStack[operatorPos--]);
    }
    return peekExpression();
  }

  private boolean isDigit(byte b) {
    return '0' <= b && b <= '9';
  }

  private void consumeNumber() {
    var value = parseNumber();
    pushExpression(new EqualExpression(value));
  }

  private int parseNumber() {
    var value = 0;
    while (pos < expression.length && isDigit(expression[pos])) {
      value = value * 10 + expression[pos++] - '0';
    }
    pos--;
    return value;
  }

  private void reduceGroupExpression() {
    while (hasOperators() && peekOperator() != OPEN_PARENTHESIS) {
      applyOperator(popOperator());
    }
    if (!hasOperators()) {
      throw new IllegalArgumentException("Mismatched open parentheses");
    }
    popOperator();
  }

  private void applyOperator(byte operator) {
    var right = popExpression();
    var left = popExpression();
    if (operator == BIT_AND) {
      pushExpression(new AndExpression(left, right));
    } else if (operator == BIT_OR) {
      pushExpression(new OrExpression(left, right));
    }
  }

  private boolean isLogicalOperator(byte b) {
    return b == BIT_OR || b == BIT_AND;
  }

  private void consumeOperator(byte operator) {
    var precedence = precedenceOf(operator);
    while (hasOperators() && precedence <= precedenceOf(peekOperator())) {
      applyOperator(popOperator());
    }
    pushOperator(operator);
  }

  /**
   * Determines the precedence level of a given operator.
   *
   * @param operator the operator, represented as a byte
   * @return an integer representing the precedence level of the operator.
   * Higher values indicate higher precedence.
   */
  private int precedenceOf(byte operator) {
    return switch (operator) {
      case BIT_AND -> 2;
      case BIT_OR -> 1;
      default -> 0;
    };
  }
}
