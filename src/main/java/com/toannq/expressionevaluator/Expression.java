package com.toannq.expressionevaluator;

import java.util.BitSet;

public interface Expression {
  EvaluatedResult evaluate(BitSet values);
}
