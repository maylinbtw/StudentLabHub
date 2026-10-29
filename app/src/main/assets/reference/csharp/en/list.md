# List<T>

`List<T>` is a list that grows and shrinks as you add and remove elements. `T` is the element type: `List<int>`, `List<string>`, `List<Student>`.

## Creating

```csharp
var marks = new List<int>();                          // empty
var days = new List<string> { "Mon", "Tue", "Wed" };  // with values
List<double> prices = [9.5, 12.0];                    // C# 12
```

## Adding and removing

```csharp
var subjects = new List<string> { "Math", "Physics" };

subjects.Add("Chemistry");            // to the end
subjects.Insert(0, "History");        // at an index
subjects.AddRange(new[] { "Programming", "DB" });

subjects.Remove("Physics");           // by value (first occurrence)
subjects.RemoveAt(0);                 // by index
subjects.RemoveAll(s => s.Length < 3);// by condition

Console.WriteLine(string.Join(", ", subjects));
```

```output
Math, Chemistry, Programming
```

## Access and search

```csharp
var marks = new List<int> { 7, 9, 4, 9 };

Console.WriteLine(marks[0]);            // 7
Console.WriteLine(marks.Count);         // 4 — a list has Count, not Length
Console.WriteLine(marks.Contains(4));   // True
Console.WriteLine(marks.IndexOf(9));    // 1
Console.WriteLine(marks.Find(m => m > 7));     // 9 — the first match
Console.WriteLine(marks.Exists(m => m < 5));   // True
```

> [!WARNING]
> The index must be from `0` to `Count - 1`, otherwise you get `ArgumentOutOfRangeException`.

## Iterating and sorting

```csharp
var marks = new List<int> { 7, 9, 4 };

marks.Sort();          // 4 7 9
marks.Reverse();       // 9 7 4

foreach (int m in marks)
    Console.Write($"{m} ");
```

A list of objects is sorted by saying what to compare:

```csharp
var students = new List<Student> { /* ... */ };
students.Sort((a, b) => a.Name.CompareTo(b.Name));
```

> [!WARNING]
> Inside `foreach` you can't add or remove elements of the same list — you get `InvalidOperationException`. To remove by condition, use `RemoveAll` or a `for` loop running backwards.

## Array or list

| | Array `T[]` | `List<T>` |
|---|---|---|
| size | fixed | changes |
| count | `Length` | `Count` |
| adding | not possible | `Add`, `Insert` |
| when to use | the size is known in advance | data arrives gradually |

Conversions: `list.ToArray()` and `array.ToList()`.

## Check yourself

> [!QUIZ]
> How do you get the number of elements in a `List<T>`?
> ---
> With the `Count` property (arrays use `Length`).

> [!QUIZ]
> Why can't you remove list items inside a `foreach` over the same list?
> ---
> You get `InvalidOperationException`: the collection changed during iteration. Use `RemoveAll` or a backwards `for`.
