# Лямбда-выражения и делегаты

**Лямбда-выражение** — короткая безымянная функция, которую можно сохранить в переменную или передать в метод как аргумент.

## Синтаксис

Слева от `=>` параметры, справа — результат или тело:

```csharp
x => x * 2                     // один параметр
(a, b) => a + b                // несколько параметров
() => Console.WriteLine("Hi")  // без параметров
s =>                           // с телом из нескольких строк
{
    string t = s.Trim();
    return t.ToUpper();
}
```

## Func и Action

Чтобы сохранить лямбду в переменную, нужен тип-**делегат**. Чаще всего используют готовые:

| Тип | Что описывает |
|---|---|
| `Func<int, int>` | принимает `int`, возвращает `int` |
| `Func<int, int, bool>` | принимает два `int`, возвращает `bool` |
| `Func<string>` | без параметров, возвращает `string` |
| `Action<string>` | принимает `string`, ничего не возвращает |
| `Predicate<T>` | принимает `T`, возвращает `bool` (условие) |

В `Func` последний тип — всегда тип результата.

```csharp
Func<int, int> square = x => x * x;
Func<int, int, int> add = (a, b) => a + b;
Action<string> print = s => Console.WriteLine($"> {s}");

Console.WriteLine(square(5));  // 25
Console.WriteLine(add(2, 3));  // 5
print("готово");               // > готово
```

## Передача в метод

Главная польза лямбд — передать поведение в метод:

```csharp
static List<int> Filter(List<int> items, Func<int, bool> condition)
{
    var result = new List<int>();
    foreach (int item in items)
        if (condition(item)) result.Add(item);
    return result;
}

var marks = new List<int> { 3, 8, 5, 10, 4 };

var good = Filter(marks, m => m >= 8);
var failed = Filter(marks, m => m < 4);

Console.WriteLine(string.Join(" ", good));    // 8 10
Console.WriteLine(string.Join(" ", failed));  // 3
```

Так же устроены `List.Find`, `List.RemoveAll`, `List.Sort` и весь LINQ.

## Захват переменных

Лямбда видит переменные из окружающего кода и может их менять:

```csharp
int limit = 6;
Func<int, bool> passed = m => m >= limit;

Console.WriteLine(passed(7)); // True
limit = 8;
Console.WriteLine(passed(7)); // False — берётся текущее значение limit
```

## Ссылка на метод

Вместо лямбды можно передать имя готового метода с подходящей сигнатурой:

```csharp
static bool IsEven(int n) => n % 2 == 0;

var evens = Filter(marks, IsEven);
var names = new List<string> { "анна", "олег" };
names.ForEach(Console.WriteLine);
```

> [!NOTE]
> Если параметр лямбды не используется, его можно назвать `_`: `list.ForEach(_ => count++)`.

## Проверь себя

> [!QUIZ]
> Что описывает тип `Func<int, bool>`?
> ---
> Функцию, которая принимает `int` и возвращает `bool`. Последний тип в `Func` — результат.

> [!QUIZ]
> Чем `Action<T>` отличается от `Func<T, TResult>`?
> ---
> `Action` ничего не возвращает, `Func` возвращает результат.
