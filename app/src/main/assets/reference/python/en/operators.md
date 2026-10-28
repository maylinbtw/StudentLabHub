# Operators

## Arithmetic

| Operator | Example | Result |
|---|---|---|
| `+` `-` `*` | `7 * 2` | 14 |
| `/` | `7 / 2` | 3.5 — always a float |
| `//` | `7 // 2` | 3 — floor division |
| `%` | `7 % 2` | 1 — remainder |
| `**` | `2 ** 10` | 1024 — power |

```python
print(-7 // 2)     # -4 — rounds down, not toward zero
print(-7 % 3)      # 2 — the remainder takes the divisor's sign
print(divmod(17, 5))   # (3, 2) — quotient and remainder at once
```

> Unlike C++, Java and C#, `/` in Python always gives a `float`: `6 / 2` is `3.0`. Use `//` for an integer result.

## Compound assignment

```python
x = 10
x += 5     # 15
x -= 3     # 12
x *= 2     # 24
x //= 5    # 4
x **= 2    # 16
```

Python has no `++` or `--`: write `x += 1`.

## Comparison

```python
mark = 8
print(mark >= 7)            # True
print(4 <= mark <= 10)      # True — comparisons can be chained
print("abc" < "abd")        # True — strings compare character by character
print([1, 2] == [1, 2])     # True — compares contents
```

`is` checks whether two names refer to the same object, not whether values are equal. Use it only with `None`: `if value is None:`.

## Logical operators

Python spells logical operators as words:

| Operator | Example |
|---|---|
| `and` | `mark >= 4 and not absent` |
| `or` | `weekend or holiday` |
| `not` | `not passed` |

```python
age = 19
has_pass = True
print(age >= 18 and has_pass)    # True
```

## Truthiness

In conditions every value is converted to `bool`. `0`, `0.0`, `""`, `[]`, `{}` and `None` are false, everything else is true:

```python
labs = []
if not labs:
    print("The list of labs is empty")

name = input("Name: ")
if name:
    print("A name was entered")
```

`and` and `or` return one of their operands, not necessarily `True`/`False`. This is used for default values:

```python
group = input("Group: ") or "CS-44"
```

## Math: math

```python
import math

math.sqrt(81)        # 9.0
math.pi              # 3.141592653589793
math.ceil(2.1)       # 3
math.floor(2.9)      # 2
round(2.567, 2)      # 2.57
round(2.5)           # 2 — "banker's" rounding to even
abs(-7)              # 7
max(3, 8, 5)         # 8
```

## Precedence (highest first)

1. `**`
2. unary `-x`, `+x`
3. `*`, `/`, `//`, `%`
4. `+`, `-`
5. comparisons `<`, `==`, `in`, `is` and others
6. `not`
7. `and`
8. `or`

Note that `-2 ** 2` equals `-4` because the power is evaluated before the unary minus.
