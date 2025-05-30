package com.toannq.expressionevaluator;

public record OrExpression(Expression left, Expression right) implements Expression {

  @Override
  public EvaluatedResult evaluate(int inputMask) {
    var leftResult = left.evaluate(inputMask);
    if (leftResult.matched()) {
      return leftResult;
    }
    var rightResult = right.evaluate(inputMask);
    if (rightResult.matched()) {
      return rightResult;
    }
    return new EvaluatedResult(false, 0);
  }
}
