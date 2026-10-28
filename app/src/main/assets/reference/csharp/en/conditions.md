# The if statement

The `if` statement runs code only when a condition is true. The condition is any expression of type `bool`.

## if

```csharp
int mark = 9;

if (mark >= 9)
{
    Console.WriteLine("Excellent!");
}
```

```output
Excellent!
```

When the block has a single statement, the braces can be omitted, but it is better to always keep them: adding a second line later is easier and safer.

## if … else

The `else` branch runs when the condition is false:

```csharp
int mark = 3;

if (mark >= 4)
{
    Console.WriteLine("Passed");
}
else
{
    Console.WriteLine("Retake");
}
```

```output
Retake
```

## else if chains

With more than two options, conditions are checked one by one from top to bottom. The first matching branch runs, the rest are skipped:

```csharp
int mark = 7;
string result;

if (mark >= 9)
    result = "excellent";
else if (mark >= 7)
    result = "good";
else if (mark >= 4)
    result = "satisfactory";
else
    result = "unsatisfactory";

Console.WriteLine(result);
```

```output
good
```

> Order matters. If `mark >= 4` came first, a 9 would also end up as "satisfactory".

## Nested conditions

A branch can contain another `if`:

```csharp
bool isDone = true;
bool isDefended = false;

if (isDone)
{
    if (isDefended)
        Console.WriteLine("Work closed");
    else
        Console.WriteLine("Needs defending");
}
else
{
    Console.WriteLine("Needs doing");
}
```

Deep nesting is hard to read. It can often be replaced with `&&` or an early return from the method.

## The conditional operator

`? :` is a short form of `if … else` that produces a value:

```text
condition ? value_if_true : value_if_false
```

```csharp
int mark = 5;
string status = mark >= 4 ? "passed" : "failed";
Console.WriteLine($"Status: {status}");
```

```output
Status: passed
```

Both values must have the same type (or convert to a common one). Conditional operators can be nested, but such code is hard to read.

## Null operators

There are short operators for `null` checks:

```csharp
string? group = null;

string shown = group ?? "not selected";   // if null, take the right side
int? length = group?.Length;              // if null, the result is null too
group ??= "CS-44";                        // assign only if null
```

## Common mistakes

- a semicolon after the condition: `if (x > 0);` — the block after it always runs;
- comparing `double` values with `==`: because of rounding errors, check `Math.Abs(a - b) < 1e-9` instead;
- redundant comparison with `true`: write `if (isDone)` instead of `if (isDone == true)`.
