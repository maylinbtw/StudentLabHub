# Type conversions

You often need to turn a value of one type into another: an integer into a fraction, a string into a number, a number into a string. C# offers several ways, and it is important to know which of them can lose data.

## Implicit conversions

When a conversion is **safe** (no data is lost), the compiler does it for you:

```csharp
int count = 42;
long big = count;       // int → long
double real = count;    // int → double
float f = 'A';          // char → float (character code 65)
```

The safe chain for numbers is `byte` → `short` → `int` → `long` → `float` → `double`. Conversions from integer types to `decimal` are implicit as well.

## Explicit conversions (casts)

When data may be lost, the compiler requires an explicit **cast** — the type in parentheses before the value:

```csharp
double price = 9.99;
int whole = (int)price;      // 9 — the fraction is dropped, no rounding

long big = 3_000_000_000;
int overflow = (int)big;     // -1294967296 — the value did not fit
```

> [!WARNING]
> When a number is cast to a smaller type, the extra bits are simply cut off, and the program keeps running with a wrong value without any error.

## checked: catching overflow

Inside a `checked` block or expression, overflow throws an `OverflowException` instead of failing silently:

```csharp
long big = 3_000_000_000;
try
{
    int value = checked((int)big);
}
catch (OverflowException)
{
    Console.WriteLine("The number does not fit into int");
}
```

```output
The number does not fit into int
```

## String → number

| Method | If the string is invalid |
|---|---|
| `int.Parse(s)` | throws `FormatException` |
| `int.TryParse(s, out int n)` | returns `false`, `n = 0` |
| `Convert.ToInt32(s)` | throws; returns 0 for `null` |

```csharp
int a = int.Parse("125");
double b = double.Parse("3.5");          // with an English locale — a dot

if (int.TryParse("12a", out int c))
    Console.WriteLine(c);
else
    Console.WriteLine("Not a number");
```

```output
Not a number
```

> The decimal separator depends on the system language. To always read numbers with a dot, use `double.Parse("3.5", CultureInfo.InvariantCulture)` (namespace `System.Globalization`).

## Number → string

Every value has a `ToString()` method. You can pass a format to it:

```csharp
int n = 7;
double avg = 4.567;

string s1 = n.ToString();          // "7"
string s2 = avg.ToString("F1");    // "4.6"
string s3 = n.ToString("D3");      // "007"
string s4 = $"{avg:F2}";           // "4.57" — the same via interpolation
```

## The Convert class

`Convert` translates between the basic types. Unlike a cast, it **rounds** when turning a fraction into an integer (using banker's rounding):

```csharp
Console.WriteLine((int)2.7);              // 2
Console.WriteLine(Convert.ToInt32(2.7));  // 3
Console.WriteLine(Convert.ToInt32(2.5));  // 2 — to the nearest even number
Console.WriteLine(Convert.ToBoolean(1));  // True
Console.WriteLine(Convert.ToString(255, 2)); // 11111111 — in binary
```

## Summary

- Lossless — implicit, nothing to write.
- Possibly lossy — `(type)value`, and `checked` when overflow is a risk.
- User input — `TryParse`, so the program does not crash.
- Number to text — interpolation or `ToString("format")`.

## Check yourself

> [!QUIZ]
> How does `int.Parse` differ from `int.TryParse`?
> ---
> `Parse` throws on an invalid string, `TryParse` returns `false` without throwing.

> [!QUIZ]
> What is `(int)3.99`?
> ---
> 3 — an explicit cast to `int` drops the fraction without rounding.
