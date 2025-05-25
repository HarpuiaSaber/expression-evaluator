package com.toannq.expressionevaluator;

import java.util.BitSet;

public class EqualExpression extends AbstractExpression {
  private final int value;

  public EqualExpression(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  @Override
  public EvaluatedResult evaluate(BitSet values) {
    if (values.get(value)) {
      var used = new BitSet(8);//increase by case quantity
      used.set(value);
      return new EvaluatedResult(true, used);
    }
    return new EvaluatedResult(false, EMPTY);
  }
}
