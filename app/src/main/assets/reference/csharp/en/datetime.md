# Date and time

C# has `DateTime` for dates and times and `TimeSpan` for intervals. .NET 6 added separate `DateOnly` and `TimeOnly`.

## Current date

```csharp
DateTime now = DateTime.Now;       // local date and time
DateTime today = DateTime.Today;   // today, 00:00
DateTime utc = DateTime.UtcNow;    // UTC time

Console.WriteLine(now.Year);       // 2026
Console.WriteLine(now.Month);      // 10
Console.WriteLine(now.DayOfWeek);  // Thursday
```

## Creating a date

```csharp
var deadline = new DateTime(2026, 11, 15);           // November 15, 2026
var lesson = new DateTime(2026, 11, 3, 9, 30, 0);   // + 09:30:00
```

## Formatting

```csharp
var d = new DateTime(2026, 11, 3, 9, 30, 0);

Console.WriteLine(d.ToString("dd.MM.yyyy"));     // 03.11.2026
Console.WriteLine(d.ToString("HH:mm"));          // 09:30
Console.WriteLine($"{d:dddd, MMMM dd}");         // Tuesday, November 03
Console.WriteLine(d.ToShortDateString());        // depends on system culture
```

| Code | Meaning |
|---|---|
| `dd` | day, 2 digits |
| `MM` | month, 2 digits |
| `MMMM` | month name |
| `yyyy` | year |
| `HH` / `hh` | hours 24 / 12 |
| `mm` | minutes |
| `ss` | seconds |
| `ddd`, `dddd` | weekday short / full |

> [!WARNING]
> `MM` is the month, `mm` is minutes. Mixing them up is a common mistake: `"dd.mm.yyyy"` prints minutes instead of the month.

## Parsing a string

```csharp
DateTime a = DateTime.Parse("11/15/2026");   // depends on system culture

bool ok = DateTime.TryParseExact(
    "2026-11-15", "yyyy-MM-dd",
    CultureInfo.InvariantCulture, DateTimeStyles.None,
    out DateTime b);
```

`CultureInfo` and `DateTimeStyles` need `using System.Globalization;`.

## Arithmetic

```csharp
var start = new DateTime(2026, 11, 1);

DateTime later = start.AddDays(14);        // Nov 15, 2026
DateTime earlier = start.AddMonths(-1);    // Oct 1, 2026

TimeSpan left = new DateTime(2026, 11, 15) - DateTime.Today;
Console.WriteLine($"{left.Days} days until the deadline");
```

Dates are compared with regular operators:

```csharp
if (DateTime.Now > deadline)
    Console.WriteLine("The deadline has passed");
```

## TimeSpan

```csharp
var pair = TimeSpan.FromMinutes(90);
Console.WriteLine(pair);            // 01:30:00
Console.WriteLine(pair.TotalHours); // 1.5

TimeSpan t = new TimeSpan(1, 20, 0); // 1 h 20 min
var end = new DateTime(2026, 11, 3, 8, 0, 0) + t; // 09:20
```

## DateOnly and TimeOnly

If you need only a date (a deadline) or only a time (the start of a class), separate types are more convenient:

```csharp
var due = new DateOnly(2026, 11, 15);
var begin = new TimeOnly(8, 0);

Console.WriteLine(due.AddDays(7));        // 11/22/2026
Console.WriteLine(begin.AddMinutes(95));  // 9:35 AM
```

## Check yourself

> [!QUIZ]
> How does `MM` differ from `mm` in a date format?
> ---
> `MM` is the month, `mm` is minutes.

> [!QUIZ]
> What type do you get when subtracting two `DateTime`s?
> ---
> `TimeSpan` — a time interval, e.g. `left.Days`.
