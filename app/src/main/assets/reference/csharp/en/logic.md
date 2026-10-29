# Comparison and logical operators

Comparison and logical operators return a `bool` — `true` or `false`. Every condition and loop is built on them.

## Comparison operators

| Operator | Meaning | Example | Result |
|---|---|---|---|
| `==` | equal | `5 == 5` | True |
| `!=` | not equal | `5 != 3` | True |
| `<` | less than | `2 < 7` | True |
| `>` | greater than | `2 > 7` | False |
| `<=` | less or equal | `4 <= 4` | True |
| `>=` | greater or equal | `3 >= 9` | False |

```csharp
int mark = 7;
bool passed = mark >= 4;
Console.WriteLine(passed);
```

```output
True
```

> [!WARNING]
> Don't confuse `=` (assignment) with `==` (comparison). In C#, `if (mark = 5)` does not compile because the result of an assignment is an `int`, not a `bool`.

## Comparing strings

For strings, `==` compares the content, not the references:

```csharp
string a = "CS-44";
string b = "cs-44";

Console.WriteLine(a == b);                                              // False
Console.WriteLine(a.Equals(b, StringComparison.OrdinalIgnoreCase));    // True
```

`<` and `>` are not defined for strings — use `string.Compare(a, b)` for ordering.

## Logical operators

| Operator | Name | `true` when… |
|---|---|---|
| `&&` | AND | both operands are `true` |
| `\|\|` | OR | at least one is `true` |
| `!` | NOT | the operand is `false` |
| `^` | XOR | the operands differ |

```csharp
int age = 19;
bool hasPass = true;

bool canEnter = age >= 18 && hasPass;     // True
bool weekend = false, holiday = true;
bool dayOff = weekend || holiday;          // True
bool workDay = !dayOff;                    // False
```

## Short-circuit evaluation

`&&` and `||` skip the right side when the left side already decides the result. This protects you from errors:

```csharp
string? name = null;

if (name != null && name.Length > 3)
{
    Console.WriteLine("Long name");
}
```

If `name` is `null`, `name.Length` is never evaluated and no exception is thrown.

> `&` and `|` also work as AND/OR on `bool`, but they always evaluate both sides. In conditions you almost always want `&&` and `||`.

## Precedence

From highest to lowest: `!` → comparisons `<`, `>`, `<=`, `>=` → `==`, `!=` → `&&` → `||`. So `a || b && c` means `a || (b && c)`. When in doubt, add parentheses:

```csharp
bool result = (age >= 18 && hasPass) || isTeacher;
```

## Range checks

A common task is checking that a number lies within a range:

```csharp
int score = 8;
bool good = score >= 7 && score <= 10;

// the same with pattern matching (C# 9+)
bool good2 = score is >= 7 and <= 10;
```

## Check yourself

> [!QUIZ]
> How does `&&` differ from `&` for `bool`?
> ---
> `&&` skips the right side if the left is already `false` (short-circuit). `&` evaluates both sides.

> [!QUIZ]
> What is `!(true || false)`?
> ---
> `false`: `true || false` is `true`, and `!` inverts it.
