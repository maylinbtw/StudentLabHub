# Дата и время

Для дат и времени в C# есть `DateTime`, для промежутков — `TimeSpan`. С .NET 6 появились отдельные `DateOnly` и `TimeOnly`.

## Текущая дата

```csharp
DateTime now = DateTime.Now;       // дата и время на компьютере
DateTime today = DateTime.Today;   // сегодня, 00:00
DateTime utc = DateTime.UtcNow;    // время UTC

Console.WriteLine(now.Year);       // 2026
Console.WriteLine(now.Month);      // 10
Console.WriteLine(now.DayOfWeek);  // Thursday
```

## Создание даты

```csharp
var deadline = new DateTime(2026, 11, 15);           // 15 ноября 2026
var lesson = new DateTime(2026, 11, 3, 9, 30, 0);   // + 09:30:00
```

## Форматирование

```csharp
var d = new DateTime(2026, 11, 3, 9, 30, 0);

Console.WriteLine(d.ToString("dd.MM.yyyy"));     // 03.11.2026
Console.WriteLine(d.ToString("HH:mm"));          // 09:30
Console.WriteLine($"{d:dd MMMM, dddd}");         // 03 ноября, вторник
Console.WriteLine(d.ToShortDateString());        // по культуре системы
```

| Код | Значение |
|---|---|
| `dd` | день, 2 цифры |
| `MM` | месяц, 2 цифры |
| `MMMM` | месяц словом |
| `yyyy` | год |
| `HH` / `hh` | часы 24 / 12 |
| `mm` | минуты |
| `ss` | секунды |
| `ddd`, `dddd` | день недели кратко / полностью |

> [!WARNING]
> `MM` — месяц, `mm` — минуты. Перепутать их — частая ошибка: `"dd.mm.yyyy"` выведет минуты вместо месяца.

## Разбор строки

```csharp
DateTime a = DateTime.Parse("15.11.2026");   // по культуре системы

bool ok = DateTime.TryParseExact(
    "2026-11-15", "yyyy-MM-dd",
    CultureInfo.InvariantCulture, DateTimeStyles.None,
    out DateTime b);
```

Для `CultureInfo` и `DateTimeStyles` нужен `using System.Globalization;`.

## Арифметика

```csharp
var start = new DateTime(2026, 11, 1);

DateTime later = start.AddDays(14);        // 15.11.2026
DateTime earlier = start.AddMonths(-1);    // 01.10.2026

TimeSpan left = new DateTime(2026, 11, 15) - DateTime.Today;
Console.WriteLine($"До сдачи {left.Days} дн.");
```

Даты сравниваются обычными операторами:

```csharp
if (DateTime.Now > deadline)
    Console.WriteLine("Срок прошёл");
```

## TimeSpan

```csharp
var pair = TimeSpan.FromMinutes(90);
Console.WriteLine(pair);            // 01:30:00
Console.WriteLine(pair.TotalHours); // 1,5

TimeSpan t = new TimeSpan(1, 20, 0); // 1 ч 20 мин
var end = new DateTime(2026, 11, 3, 8, 0, 0) + t; // 09:20
```

## DateOnly и TimeOnly

Если нужна только дата (дедлайн) или только время (начало пары), удобнее отдельные типы:

```csharp
var due = new DateOnly(2026, 11, 15);
var begin = new TimeOnly(8, 0);

Console.WriteLine(due.AddDays(7));        // 22.11.2026
Console.WriteLine(begin.AddMinutes(95));  // 9:35
```

## Проверь себя

> [!QUIZ]
> Чем `MM` отличается от `mm` в формате даты?
> ---
> `MM` — месяц, `mm` — минуты.

> [!QUIZ]
> Какой тип получится при вычитании двух `DateTime`?
> ---
> `TimeSpan` — промежуток времени, например `left.Days`.
