package com.toannq.expressionevaluator;

public record EqualExpression(int value, int mask) implements Expression {
  public EqualExpression(int value) {
    this(value, 1 << value);
  }

  @Override
  public EvaluatedResult evaluate(int caseMask) {
    boolean matched = (caseMask & mask) != 0;
    return matched ? new EvaluatedResult(true, mask) : new EvaluatedResult(false, 0);
  }
}
