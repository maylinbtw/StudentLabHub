# LINQ

**LINQ** is a set of methods for processing collections: filtering, sorting, grouping, counting. It works with arrays, lists, dictionaries and anything that implements `IEnumerable<T>`. The `System.Linq` namespace is included by default.

## Sample data

```csharp
record Student(string Name, string Group, double Average); // at the end of the file or in a separate file

var students = new List<Student>
{
    new("Anna", "P-21", 8.4),
    new("Oleg", "P-22", 6.1),
    new("Ira",  "P-21", 9.2),
    new("Mark", "P-22", 7.5)
};
```

## Where and Select

`Where` keeps matching elements, `Select` turns each element into something else:

```csharp
var best = students
    .Where(s => s.Average >= 8)
    .Select(s => s.Name);

Console.WriteLine(string.Join(", ", best)); // Anna, Ira
```

## Sorting

```csharp
var byAverage = students.OrderByDescending(s => s.Average);
var byGroupThenName = students.OrderBy(s => s.Group).ThenBy(s => s.Name);
```

## Aggregates

```csharp
int[] marks = { 7, 9, 4, 8 };

marks.Count();               // 4
marks.Sum();                 // 28
marks.Average();             // 7
marks.Max();                 // 9
marks.Count(m => m >= 8);    // 2
students.Max(s => s.Average);// 9.2
```

## Checks and search

```csharp
bool anyFailed = students.Any(s => s.Average < 4);   // False
bool allPassed = students.All(s => s.Average >= 6);  // True

Student first = students.First(s => s.Group == "P-22");          // Oleg
Student? none = students.FirstOrDefault(s => s.Group == "P-23"); // null
```

> [!WARNING]
> `First` throws `InvalidOperationException` if nothing is found. When an element may be missing, use `FirstOrDefault` and check for `null`.

## Grouping

```csharp
var groups = students.GroupBy(s => s.Group);

foreach (var g in groups)
    Console.WriteLine($"{g.Key}: average {g.Average(s => s.Average):F1}");
```

```output
P-21: average 8.8
P-22: average 6.8
```

## Other useful methods

| Method | What it does |
|---|---|
| `Take(n)` / `Skip(n)` | first n / skip n |
| `Distinct()` | remove duplicates |
| `ToList()` / `ToArray()` | collect the result into a collection |
| `ToDictionary(k => ..., v => ...)` | collect into a dictionary |
| `Sum`, `Min`, `Max`, `Average` | aggregates, optionally with a selector |

## Deferred execution

A LINQ query doesn't run immediately — it runs when iterated or when `ToList()`, `Count()` etc. is called:

```csharp
var list = new List<int> { 1, 2, 3 };
var big = list.Where(x => x > 1);   // nothing computed yet

list.Add(10);
Console.WriteLine(big.Count());      // 3 — the new element is counted too
```

To fix the result, call `.ToList()`.

## Check yourself

> [!QUIZ]
> How does `First` differ from `FirstOrDefault`?
> ---
> If nothing is found, `First` throws `InvalidOperationException`, while `FirstOrDefault` returns the default (`null` for objects).

> [!QUIZ]
> When does `list.Where(...)` actually run?
> ---
> Not immediately, but when iterated or when `ToList()`, `Count()` etc. is called — deferred execution.
