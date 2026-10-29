# LINQ

**LINQ** — набор методов для обработки коллекций: фильтрация, сортировка, группировка, подсчёт. Работает с массивами, списками, словарями и всем, что реализует `IEnumerable<T>`. Пространство имён `System.Linq` подключено по умолчанию.

## Данные для примеров

```csharp
record Student(string Name, string Group, double Average); // в конце файла или в отдельном файле

var students = new List<Student>
{
    new("Анна", "П-21", 8.4),
    new("Олег", "П-22", 6.1),
    new("Ира",  "П-21", 9.2),
    new("Марк", "П-22", 7.5)
};
```

## Where и Select

`Where` оставляет подходящие элементы, `Select` превращает каждый элемент во что-то другое:

```csharp
var best = students
    .Where(s => s.Average >= 8)
    .Select(s => s.Name);

Console.WriteLine(string.Join(", ", best)); // Анна, Ира
```

## Сортировка

```csharp
var byAverage = students.OrderByDescending(s => s.Average);
var byGroupThenName = students.OrderBy(s => s.Group).ThenBy(s => s.Name);
```

## Агрегаты

```csharp
int[] marks = { 7, 9, 4, 8 };

marks.Count();               // 4
marks.Sum();                 // 28
marks.Average();             // 7
marks.Max();                 // 9
marks.Count(m => m >= 8);    // 2
students.Max(s => s.Average);// 9,2
```

## Проверки и поиск

```csharp
bool anyFailed = students.Any(s => s.Average < 4);   // False
bool allPassed = students.All(s => s.Average >= 6);  // True

Student first = students.First(s => s.Group == "П-22");          // Олег
Student? none = students.FirstOrDefault(s => s.Group == "П-23"); // null
```

> [!WARNING]
> `First` выбрасывает `InvalidOperationException`, если ничего не найдено. Когда элемента может не быть, используйте `FirstOrDefault` и проверяйте на `null`.

## Группировка

```csharp
var groups = students.GroupBy(s => s.Group);

foreach (var g in groups)
    Console.WriteLine($"{g.Key}: средний {g.Average(s => s.Average):F1}");
```

```output
П-21: средний 8,8
П-22: средний 6,8
```

## Другие полезные методы

| Метод | Что делает |
|---|---|
| `Take(n)` / `Skip(n)` | первые n / пропустить n |
| `Distinct()` | убрать повторы |
| `ToList()` / `ToArray()` | собрать результат в коллекцию |
| `ToDictionary(k => ..., v => ...)` | собрать в словарь |
| `Sum`, `Min`, `Max`, `Average` | агрегаты, можно с селектором |

## Отложенное выполнение

Запрос LINQ не выполняется сразу — он запускается при переборе или вызове `ToList()`, `Count()` и т.п.:

```csharp
var list = new List<int> { 1, 2, 3 };
var big = list.Where(x => x > 1);   // ещё ничего не посчитано

list.Add(10);
Console.WriteLine(big.Count());      // 3 — учтён и новый элемент
```

Чтобы зафиксировать результат, вызовите `.ToList()`.

## Проверь себя

> [!QUIZ]
> Чем `First` отличается от `FirstOrDefault`?
> ---
> Если ничего не найдено, `First` выбросит `InvalidOperationException`, а `FirstOrDefault` вернёт значение по умолчанию (`null` для объектов).

> [!QUIZ]
> Когда выполняется запрос `list.Where(...)`?
> ---
> Не сразу, а при переборе или вызове `ToList()`, `Count()` и т.п. — это отложенное выполнение.
