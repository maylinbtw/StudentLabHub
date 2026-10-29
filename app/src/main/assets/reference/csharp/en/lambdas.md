# Lambda expressions and delegates

A **lambda expression** is a short unnamed function that can be stored in a variable or passed to a method as an argument.

## Syntax

Parameters go to the left of `=>`, the result or body to the right:

```csharp
x => x * 2                     // one parameter
(a, b) => a + b                // several parameters
() => Console.WriteLine("Hi")  // no parameters
s =>                           // a multi-line body
{
    string t = s.Trim();
    return t.ToUpper();
}
```

## Func and Action

To store a lambda in a variable you need a **delegate** type. The built-in ones are used most:

| Type | What it describes |
|---|---|
| `Func<int, int>` | takes an `int`, returns an `int` |
| `Func<int, int, bool>` | takes two `int`s, returns a `bool` |
| `Func<string>` | no parameters, returns a `string` |
| `Action<string>` | takes a `string`, returns nothing |
| `Predicate<T>` | takes a `T`, returns a `bool` (a condition) |

In `Func` the last type is always the result type.

```csharp
Func<int, int> square = x => x * x;
Func<int, int, int> add = (a, b) => a + b;
Action<string> print = s => Console.WriteLine($"> {s}");

Console.WriteLine(square(5));  // 25
Console.WriteLine(add(2, 3));  // 5
print("done");                 // > done
```

## Passing to a method

The main benefit of lambdas is passing behavior into a method:

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

`List.Find`, `List.RemoveAll`, `List.Sort` and all of LINQ work the same way.

## Capturing variables

A lambda sees variables from the surrounding code and can change them:

```csharp
int limit = 6;
Func<int, bool> passed = m => m >= limit;

Console.WriteLine(passed(7)); // True
limit = 8;
Console.WriteLine(passed(7)); // False — the current value of limit is used
```

## Method references

Instead of a lambda you can pass the name of an existing method with a matching signature:

```csharp
static bool IsEven(int n) => n % 2 == 0;

var evens = Filter(marks, IsEven);
var names = new List<string> { "anna", "oleg" };
names.ForEach(Console.WriteLine);
```

> [!NOTE]
> If a lambda parameter is unused, it can be named `_`: `list.ForEach(_ => count++)`.

## Check yourself

> [!QUIZ]
> What does the type `Func<int, bool>` describe?
> ---
> A function that takes an `int` and returns a `bool`. The last type in `Func` is the result.

> [!QUIZ]
> How does `Action<T>` differ from `Func<T, TResult>`?
> ---
> `Action` returns nothing, `Func` returns a result.
