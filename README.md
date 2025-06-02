# Expression parser and evaluator

> [!NOTE]
> This only supports my business logic.

## Business logic

When collecting data from multiple sources, we sometimes evaluate based on which cases have occurred to decide which
data or configuration to use. Instead of writing complex conditional logic, we assign numeric identifiers to each case
and evaluate them using logical expressions. Example, checking if case 1 and 2 have both occurred.

## Expression Parser

The ExpressionParser class provides a static method to parse logical expressions and build an expression tree.
```
Expression ExpressionParser.parse(String expression)
```
You can pass an expression string ranging from simple to complex:

- Simple: `"1"`, `"1 & 2"`, `"1 || 2"`, etc
  -Complex: `"1 || 2 & 3"`, `"(1 || 2) & 3"`, `"(1 & 2) || (3 & (4 || 5))"`, etc

### Operators and Syntax:

- Numbers (`"1"`, `"2"`, etc.): Represent individual cases or conditions.
- `"&"`: logical and operator (not bitwise and).
- `"||"`: logical or operator.
- `"("`, `")"`: opening and closing parentheses.

Parse the expression and return an expression tree.
Examples:
```
Input: "1"
Output: Expression tree representing:

Equal(1)
```
```
Input: "1 & 2"
Output: Expression tree representing:

And
├── Equal(1)
└── Equal(2)
```
```
Input: "1 || 2 & 3"
Output: Expression tree representing:

Or
├── Equal(1)
└── And
    ├── Equal(2)
    └── Equal(3)

```
```
Input: "(1 || 2) & 3"
Output: Expression tree representing:

And
├── Or
│   ├── Equal(1)
│   └── Equal(2)
└── Equal(3)

```
```
Input: "(1 & 2) || (3 & (4 || 5))"
Output: Expression tree representing:

Or
├── And
│   ├── Equal(1)
│   └── Equal(2)
└──  And
    ├── Equal(3)
    └── Or
        ├── Equal(4)
        └── Equal(5)

```

> [!NOTE]
> - Only the following elements are supported in the expression string: numbers, `&`, `||`, `(`, and `)`.
> - The `&` symbol (bitwise AND) is used to represent logical AND in the expression syntax. However, during evaluation, it
  operates with short-circuit logic similar to Java’s `&&`.
> - The parser respects Java’s logical operator precedence: `&` has higher precedence than `||`.
> - The `ExpressionParser` class is not designed to be shared as a singleton or managed by a DI
  framework (like Spring). For thread safety, always use the static `ExpressionParser.parse(...)` method directly.


## Expression Evaluator

The result of `ExpressionParser.parse(...)` is an expression tree composed of different types of expression nodes:

- `EqualExpression`
- `AndExpression`
- `OrExpression`

Each expression node implements an `evaluate(...)` method that takes a bitmask representing the occurred cases and returns an `EvaluatedResult`, which includes both:
- a boolean result (`matched`), and
- a bitmask of used cases (`used`) in the expression.

Use `Expression.mask(...)` to generate the bitmask once and avoid duplicate masking.

Follow this code to check for matching cases:
```
var expression = ExpressionParser.parse("1 & 2"); // parse expression string
var cases = List.of(1, 2); //get from data or configurations
var caseMask = Expression.mask(cases); // mask cases
var result = expression.evaluate(caseMask); // evaluate
if (result.matched() && ((caseMask & result.usedMask()) == result.usedMask())) {
  //matched
}
```
The `Expression` interface also provides a convenience method `Expression.isMatched(...)` to check if an expression matches the given masked cases directly.
Alternative version:
```
var expression = ExpressionParser.parse("1 & 2"); // parse expression string
var cases = List.of(1, 2); //get from data or configurations
var caseMask = Expression.mask(cases); // mask cases
if (Expression.isMatched(expression, caseMask)) {
  // matched
}
```
### Evaluation Logic:

- `EqualExpression`: Returns `true` if the input bitmask contains the expected case.
- `AndExpression` and `OrExpression`: Evaluate their child expressions using short-circuit logic, similar to how logical
  operators work in Java:
    - `AndExpression` returns `false` as soon as first child evaluates to `false`.
    - `OrExpression` returns `true` as soon as first child evaluates to `true`.

For more on short-circuit logic, see: https://en.wikipedia.org/wiki/Short-circuit_evaluation.
