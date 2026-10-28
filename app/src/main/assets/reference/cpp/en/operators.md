# Operators

C++ operators look like those of other C-family languages, with a few twists: integers convert to `bool` implicitly, and overflow is not checked.

## Arithmetic

| Operator | Example | Result |
|---|---|---|
| `+` `-` `*` | `7 * 2` | 14 |
| `/` | `7 / 2` | 3 — integer division |
| `/` | `7.0 / 2` | 3.5 |
| `%` | `7 % 2` | 1 — remainder, integers only |

```cpp
int sum = 25, count = 4;
double bad = sum / count;                          // 6 — integer division
double good = static_cast<double>(sum) / count;    // 6.25
```

> [!WARNING]
> Integer division by zero is **undefined behavior**: the program may crash or print garbage. Check the divisor beforehand.

The remainder of fractional numbers is computed by `fmod` from `<cmath>`.

## Increment and assignment

```cpp
int x = 5;
int a = x++;   // a = 5, x = 6 — postfix form
int b = ++x;   // b = 7, x = 7 — prefix form

x += 3;        // x = x + 3
x *= 2;        // x = x * 2
x %= 5;        // x = x % 5
```

> Don't modify the same variable twice in one expression: the result of `i = i++ + ++i;` is not defined by the standard.

## Comparison and logic

| Operator | Meaning |
|---|---|
| `==` `!=` | equal, not equal |
| `<` `>` `<=` `>=` | greater, less |
| `&&` | AND |
| `\|\|` | OR |
| `!` | NOT |

```cpp
int mark = 8;
bool good = mark >= 7 && mark <= 10;     // true
bool retake = !(mark >= 4);              // false
```

`&&` and `||` evaluate the right side only when it is needed for the result.

## Numbers as booleans

Any number can be used as a condition in C++: `0` is false, everything else is true:

```cpp
int left = 3;
if (left)                    // same as if (left != 0)
    cout << "Labs remaining\n";

bool flag = 42;              // true
int n = true + true;         // 2
```

> [!WARNING]
> A classic bug: `if (x = 5)` instead of `if (x == 5)`. It is an assignment whose result, 5, is "true", so the condition always holds. The compiler warns with `-Wall`.

## Math: cmath

```cpp
#include <cmath>

double p = pow(2, 10);       // 1024
double r = sqrt(81.0);       // 9
double m = fabs(-3.5);       // 3.5
double c = ceil(2.1);        // 3 — up
double f = floor(2.9);       // 2 — down
double rnd = round(2.5);     // 3 — nearest, halves away from zero
```

`abs` for integers lives in `<cstdlib>`, `fabs` and `abs` for fractions in `<cmath>`.

## Overflow

```cpp
#include <climits>

int big = INT_MAX;           // 2147483647
big = big + 1;               // undefined behavior, in practice a negative number
unsigned int u = 0;
u = u - 1;                   // 4294967295 — wrap-around is defined for unsigned
```

If values can be large, use `long long`.

## Precedence (highest first)

1. `x++`, `x--`, function call `f()`, subscript `a[i]`
2. `++x`, `--x`, `!`, unary `-`, casts
3. `*`, `/`, `%`
4. `+`, `-`
5. `<<`, `>>`
6. `<`, `<=`, `>`, `>=`
7. `==`, `!=`
8. `&&`
9. `||`
10. `?:`, `=`, `+=` and other assignments

Because of item 5, printing an expression with a comparison needs parentheses: `cout << (a < b);`.
