# Operators

Arithmetic, logical and comparison operators in Java work as in C and C#, with important differences: `boolean` cannot be replaced by a number, and strings are compared with `equals`.

## Arithmetic

| Operator | Example | Result |
|---|---|---|
| `+` `-` `*` | `7 * 2` | 14 |
| `/` | `7 / 2` | 3 — integers drop the fraction |
| `/` | `7.0 / 2` | 3.5 |
| `%` | `-7 % 3` | -1 — sign follows the dividend |

```java
int sum = 25, count = 4;
double avg = (double) sum / count;   // 6.25
int pages = (total + 9) / 10;        // division rounded up
```

Integer division by `0` throws `ArithmeticException`. Dividing a `double` by `0` gives `Infinity` or `NaN`.

## Increment and compound assignment

```java
int x = 5;
int a = x++;   // a = 5, x = 6
int b = ++x;   // b = 7, x = 7

x += 10;
x -= 2;
x *= 3;

byte small = 10;
small += 5;           // allowed: += casts automatically
// small = small + 5; — error: the result is an int
```

## Comparison and logic

```java
int mark = 8;
boolean good = mark >= 7 && mark <= 10;
boolean retake = mark < 4 || absent;
boolean notGood = !good;
```

- `&&` and `||` short-circuit;
- `&` and `|` on `boolean` evaluate both sides;
- `if (1)` does not compile — it must be a `boolean`.

## The Math class

```java
Math.pow(2, 10);       // 1024.0
Math.sqrt(81);         // 9.0
Math.abs(-7);          // 7
Math.max(3, 8);        // 8
Math.round(2.5);       // 3 — nearest, halves up
Math.floor(2.9);       // 2.0
Math.ceil(2.1);        // 3.0
Math.random();         // a random number from 0.0 to 1.0 (exclusive)
```

A random integer from 1 to 10:

```java
int dice = (int) (Math.random() * 10) + 1;
// or
int r = new java.util.Random().nextInt(1, 11);   // Java 17+
```

## Overflow

```java
int big = Integer.MAX_VALUE;   // 2147483647
big++;                          // -2147483648 — wraps around without an error
int safe = Math.addExact(Integer.MAX_VALUE, 1);   // ArithmeticException
```

Use `long` for large values, `BigInteger` for arbitrarily large ones, and `BigDecimal` for money.

## The conditional operator

```java
String status = mark >= 4 ? "passed" : "failed";
int max = a > b ? a : b;
```

## Check yourself

> [!QUIZ]
> What are `10 / 4` and `10 / 4.0`?
> ---
> `2` and `2.5`: integer division drops the fraction.

> [!QUIZ]
> What is `Integer.MAX_VALUE + 1`?
> ---
> A silent overflow: it becomes `Integer.MIN_VALUE`. `Math.addExact` detects it.
