package com.toannq.expressionevaluator;

public record OrExpression(Expression left, Expression right) implements Expression {

  @Override
  public EvaluatedResult evaluate(int caseMask) {
    var leftResult = left.evaluate(caseMask);
    if (leftResult.matched()) {
      return leftResult;
    }
    var rightResult = right.evaluate(caseMask);
    if (rightResult.matched()) {
      return rightResult;
    }
    return new EvaluatedResult(false, 0);
  }
}
