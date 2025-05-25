package com.toannq.expressionevaluator;

import java.util.BitSet;

public class AndExpression extends AbstractExpression {
  private final Expression left;
  private final Expression right;

  public AndExpression(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  public Expression getLeft() {
    return left;
  }

  public Expression getRight() {
    return right;
  }

  @Override
  public EvaluatedResult evaluate(BitSet values) {
    var leftResult = left.evaluate(values);
    if (!leftResult.matched()) {
      return new EvaluatedResult(false, EMPTY);
    }
    var rightResult = right.evaluate(values);
    if (!rightResult.matched()) {
      return new EvaluatedResult(false, EMPTY);
    }
    leftResult.usedValues().or(rightResult.usedValues());
    return new EvaluatedResult(true, leftResult.usedValues());
  }
}
