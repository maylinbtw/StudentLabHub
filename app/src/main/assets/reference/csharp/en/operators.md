# Arithmetic operators

Operators work on **operands** — numbers, variables or expressions. The result can be printed right away or stored in a variable.

## Basic operators

| Operator | Meaning | Example | Result |
|---|---|---|---|
| `+` | addition | `7 + 2` | 9 |
| `-` | subtraction | `7 - 2` | 5 |
| `*` | multiplication | `7 * 2` | 14 |
| `/` | division | `7 / 2` | 3 |
| `%` | remainder | `7 % 2` | 1 |

## Integer division

If both operands are integers, the result is an integer too — the fractional part is **discarded**:

```csharp
int a = 7, b = 2;
Console.WriteLine(a / b);          // 3
Console.WriteLine(a / (double)b);  // 3.5
Console.WriteLine(7.0 / 2);        // 3.5
```

> [!WARNING]
> A classic mistake: `double avg = sum / count;` with integer `sum` and `count` produces an integer that is only then turned into a `double`. Cast one operand: `(double)sum / count`.

Dividing an integer by zero throws `DivideByZeroException`. Dividing a `double` by zero does not: the result is `∞` (`double.PositiveInfinity`) or `NaN` for `0.0 / 0`.

## Remainder

`%` is often used to check whether a number is even or divisible:

```csharp
int n = 15;
Console.WriteLine(n % 2 == 0 ? "even" : "odd");
Console.WriteLine(n % 5 == 0);    // True — divisible by 5
Console.WriteLine(-7 % 3);        // -1: the sign comes from the left operand
```

## Increment and decrement

`++` adds 1 to a variable, `--` subtracts 1. Where the operator stands matters:

- the **prefix** form `++x` changes the value first and returns the new one;
- the **postfix** form `x++` returns the old value and changes it afterwards.

```csharp
int x = 5;
int y = ++x;   // x = 6, y = 6

int p = 5;
int q = p++;   // q = 5, p = 6

Console.WriteLine($"x={x} y={y} p={p} q={q}");
```

```output
x=6 y=6 p=6 q=5
```

## Compound assignment

A shorthand for "change the variable by a value":

| Shorthand | Same as |
|---|---|
| `a += 5` | `a = a + 5` |
| `a -= 5` | `a = a - 5` |
| `a *= 5` | `a = a * 5` |
| `a /= 5` | `a = a / 5` |
| `a %= 5` | `a = a % 5` |

```csharp
int score = 10;
score += 5;   // 15
score *= 2;   // 30
```

`+=` works with strings too: `text += "!"` appends a character.

## Operator precedence

An expression is evaluated by precedence, and operators of the same level go from left to right:

1. `x++`, `x--` (postfix)
2. `++x`, `--x`, unary `-`
3. `*`, `/`, `%`
4. `+`, `-`
5. `=`, `+=`, `-=` and other assignments (right to left)

```csharp
int r1 = 2 + 3 * 4;      // 14
int r2 = (2 + 3) * 4;    // 20
int r3 = 10 - 4 - 3;     // 3: (10 - 4) - 3
```

Parentheses always change the order and make code clearer — don't skimp on them.

## The Math class

Everything else is in the static `Math` class:

```csharp
Console.WriteLine(Math.Pow(2, 10));      // 1024 — power
Console.WriteLine(Math.Sqrt(81));        // 9 — square root
Console.WriteLine(Math.Abs(-7));         // 7 — absolute value
Console.WriteLine(Math.Round(2.567, 2)); // 2.57 — rounding
Console.WriteLine(Math.Max(3, 8));       // 8
Console.WriteLine(Math.PI);              // 3.141592653589793
```

> `Math.Round` uses "banker's" rounding by default: `Math.Round(2.5)` is 2 and `Math.Round(3.5)` is 4. For the usual rounding pass `MidpointRounding.AwayFromZero`.
