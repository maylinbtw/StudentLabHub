# Форматирование и StringBuilder

## Форматирование чисел

В интерполированной строке после двоеточия указывают **формат**:

```csharp
double avg = 8.456;
int count = 1234567;
double share = 0.256;

Console.WriteLine($"{avg:F2}");     // 8,46 — 2 знака после запятой
Console.WriteLine($"{count:N0}");   // 1 234 567 — с разделителями
Console.WriteLine($"{share:P1}");   // 25,6 % — проценты
Console.WriteLine($"{42:D5}");      // 00042 — дополнить нулями
Console.WriteLine($"{255:X}");      // FF — шестнадцатеричное
```

| Формат | Значение |
|---|---|
| `F2` | фиксированная точка, 2 знака |
| `N0` | число с разделителями групп |
| `P1` | процент, 1 знак |
| `D5` | целое минимум из 5 цифр |
| `X`, `X4` | шестнадцатеричное |
| `C` | денежный формат текущей культуры |

## Выравнивание

Число после запятой задаёт ширину поля: положительное — выравнивание вправо, отрицательное — влево. Удобно для таблиц:

```csharp
var rows = new[] { ("Математика", 9), ("ОАиП", 10), ("Физика", 7) };

foreach (var (subject, mark) in rows)
    Console.WriteLine($"{subject,-12}|{mark,4}");
```

```output
Математика  |   9
ОАиП        |  10
Физика      |   7
```

## Культура

Разделитель дробной части и формат дат зависят от языка системы. Для файлов и обмена данными используют `CultureInfo.InvariantCulture`:

```csharp
using System.Globalization;

double x = 3.5;
Console.WriteLine(x.ToString(CultureInfo.InvariantCulture)); // 3.5
double y = double.Parse("2.75", CultureInfo.InvariantCulture);
```

> [!WARNING]
> `double.Parse("2.75")` на компьютере с русской культурой выбросит `FormatException` или прочитает число неправильно: там ожидается `2,75`.

## StringBuilder

Строки в C# неизменяемы: каждое `+=` создаёт новую строку. В цикле на тысячи итераций это медленно. `StringBuilder` собирает текст в одном буфере:

```csharp
using System.Text;

var sb = new StringBuilder();
sb.AppendLine("Отчёт по оценкам");
sb.AppendLine(new string('=', 16));

for (int i = 1; i <= 3; i++)
    sb.Append("Лаб ").Append(i).AppendLine(": сдана");

sb.Replace("сдана", "зачтено");
string report = sb.ToString();
Console.Write(report);
```

```output
Отчёт по оценкам
================
Лаб 1: зачтено
Лаб 2: зачтено
Лаб 3: зачтено
```

| Метод | Что делает |
|---|---|
| `Append(x)` | добавить в конец |
| `AppendLine(x)` | добавить и перевести строку |
| `Insert(i, x)` | вставить по индексу |
| `Remove(i, n)` | удалить n символов |
| `Replace(a, b)` | заменить |
| `Length`, `Clear()` | длина, очистка |

> [!NOTE]
> Для двух-трёх склеиваний обычная интерполяция проще и не медленнее. `StringBuilder` нужен, когда строка собирается в цикле.
