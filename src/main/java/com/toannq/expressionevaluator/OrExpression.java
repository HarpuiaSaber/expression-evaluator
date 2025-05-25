package com.toannq.expressionevaluator;

import java.util.BitSet;

public class OrExpression extends AbstractExpression {
  private final Expression left;
  private final Expression right;

  public OrExpression(Expression left, Expression right) {
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
    if (leftResult.matched()) {
      return leftResult;
    }
    var rightResult = right.evaluate(values);
    if (rightResult.matched()) {
      return rightResult;
    }
    return new EvaluatedResult(false, EMPTY);
  }
}
