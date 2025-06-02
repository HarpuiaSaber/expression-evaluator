package com.toannq.expressionevaluator;

import java.util.List;

public interface Expression {

  static int mask(List<Integer> values) {
    int caseMask = 0;
    for (var value : values) {
      caseMask |= (1 << value);
    }
    return caseMask;
  }

  static boolean isMatched(Expression expression, int caseMask) {
    var result = expression.evaluate(caseMask);
    return result.matched() && ((caseMask & result.usedMask()) == result.usedMask());
  }

  EvaluatedResult evaluate(int caseMask);
}
