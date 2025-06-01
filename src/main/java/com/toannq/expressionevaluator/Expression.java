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

  EvaluatedResult evaluate(int caseMask);
}
