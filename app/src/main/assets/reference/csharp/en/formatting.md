# Formatting and StringBuilder

## Number formatting

In an interpolated string, a **format** goes after a colon:

```csharp
double avg = 8.456;
int count = 1234567;
double share = 0.256;

Console.WriteLine($"{avg:F2}");     // 8.46 — 2 decimal places
Console.WriteLine($"{count:N0}");   // 1,234,567 — with separators
Console.WriteLine($"{share:P1}");   // 25.6% — percent
Console.WriteLine($"{42:D5}");      // 00042 — pad with zeros
Console.WriteLine($"{255:X}");      // FF — hexadecimal
```

| Format | Meaning |
|---|---|
| `F2` | fixed point, 2 digits |
| `N0` | number with group separators |
| `P1` | percent, 1 digit |
| `D5` | integer with at least 5 digits |
| `X`, `X4` | hexadecimal |
| `C` | currency of the current culture |

## Alignment

A number after a comma sets the field width: positive aligns right, negative aligns left. Handy for tables:

```csharp
var rows = new[] { ("Math", 9), ("Programming", 10), ("Physics", 7) };

foreach (var (subject, mark) in rows)
    Console.WriteLine($"{subject,-12}|{mark,4}");
```

```output
Math        |   9
Programming |  10
Physics     |   7
```

## Culture

The decimal separator and date format depend on the system language. For files and data exchange, use `CultureInfo.InvariantCulture`:

```csharp
using System.Globalization;

double x = 3.5;
Console.WriteLine(x.ToString(CultureInfo.InvariantCulture)); // 3.5
double y = double.Parse("2.75", CultureInfo.InvariantCulture);
```

> [!WARNING]
> On a computer with a culture that uses a comma (for example, Russian or German), `double.Parse("2.75")` throws `FormatException` or reads the number wrong: `2,75` is expected there.

## StringBuilder

Strings in C# are immutable: each `+=` creates a new string. In a loop with thousands of iterations this is slow. `StringBuilder` builds text in one buffer:

```csharp
using System.Text;

var sb = new StringBuilder();
sb.AppendLine("Marks report");
sb.AppendLine(new string('=', 12));

for (int i = 1; i <= 3; i++)
    sb.Append("Lab ").Append(i).AppendLine(": submitted");

sb.Replace("submitted", "passed");
string report = sb.ToString();
Console.Write(report);
```

```output
Marks report
============
Lab 1: passed
Lab 2: passed
Lab 3: passed
```

| Method | What it does |
|---|---|
| `Append(x)` | add to the end |
| `AppendLine(x)` | add and start a new line |
| `Insert(i, x)` | insert at an index |
| `Remove(i, n)` | remove n characters |
| `Replace(a, b)` | replace |
| `Length`, `Clear()` | length, clear |

> [!NOTE]
> For two or three concatenations, plain interpolation is simpler and no slower. `StringBuilder` is for strings built in a loop.
