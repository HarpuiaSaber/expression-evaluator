package com.toannq.expressionevaluator;

import java.util.BitSet;

public record EvaluatedResult(boolean matched, BitSet usedValues) {
}
