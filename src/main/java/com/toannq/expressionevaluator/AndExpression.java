package com.toannq.expressionevaluator;

public record AndExpression(Expression left, Expression right) implements Expression {

  @Override
  public EvaluatedResult evaluate(int caseMask) {
    var leftResult = left.evaluate(caseMask);
    if (!leftResult.matched()) {
      return new EvaluatedResult(false, 0);
    }
    var rightResult = right.evaluate(caseMask);
    if (!rightResult.matched()) {
      return new EvaluatedResult(false, 0);
    }
    return new EvaluatedResult(true, (leftResult.usedMask() | rightResult.usedMask()));
  }
}
