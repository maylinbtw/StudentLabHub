# Parameters: ref, out, in, params

By default an argument is passed **by value**: the method gets a copy, and changes inside the method are not visible outside.

```csharp
static void AddOne(int n)
{
    n++;
}

int x = 5;
AddOne(x);
Console.WriteLine(x); // 5 — only the copy changed
```

## ref — pass by reference

With `ref` the method works with the caller's variable itself. The `ref` keyword is written both in the declaration and in the call:

```csharp
static void AddOne(ref int n)
{
    n++;
}

int x = 5;
AddOne(ref x);
Console.WriteLine(x); // 6
```

The classic example is swapping values:

```csharp
static void Swap(ref int a, ref int b)
{
    (a, b) = (b, a);
}

int first = 1, second = 2;
Swap(ref first, ref second);
Console.WriteLine($"{first} {second}"); // 2 1
```

> [!WARNING]
> A variable passed with `ref` must be initialized before the call.

## out — output parameter

`out` lets a method return several values. The method **must** assign every `out` parameter:

```csharp
static void MinMax(int[] items, out int min, out int max)
{
    min = items.Min();
    max = items.Max();
}

MinMax(new[] { 4, 9, 2, 7 }, out int lo, out int hi);
Console.WriteLine($"min {lo}, max {hi}");
```

```output
min 2, max 9
```

The variable can be declared right in the call: `out int lo`. This is how `TryParse` methods work:

```csharp
Console.Write("Mark: ");
if (int.TryParse(Console.ReadLine(), out int mark))
    Console.WriteLine($"Accepted: {mark}");
else
    Console.WriteLine("Not a number");
```

If you don't need the value, use `_` instead of a name: `int.TryParse(text, out _)`.

## in — read-only

`in` passes an argument by reference but forbids changing it. Useful for large structs to avoid copying:

```csharp
static double Length(in (double X, double Y) v)
{
    // v.X = 0; — error: the parameter is read-only
    return Math.Sqrt(v.X * v.X + v.Y * v.Y);
}
```

| Modifier | Must be initialized before call | Method must assign | Method can change |
|---|---|---|---|
| none | yes | no | only the copy |
| `ref` | yes | no | yes |
| `out` | no | yes | yes |
| `in` | yes | no | no |

## Optional parameters

A parameter can have a default value. Such parameters go at the end of the list:

```csharp
static void Greet(string name, string greeting = "Hello")
{
    Console.WriteLine($"{greeting}, {name}!");
}

Greet("Anna");                 // Hello, Anna!
Greet("Oleg", "Good morning"); // Good morning, Oleg!
```

## Named arguments

An argument can be passed by parameter name — then order doesn't matter, and the call reads more clearly:

```csharp
static void CreateLab(string title, int number, bool done = false) { /* ... */ }

CreateLab(number: 3, title: "Arrays");
CreateLab("Strings", 4, done: true);
```

## params — variable number of arguments

`params` lets you pass any number of comma-separated arguments. Inside the method they arrive as an array:

```csharp
static int Sum(params int[] numbers)
{
    int total = 0;
    foreach (int n in numbers) total += n;
    return total;
}

Console.WriteLine(Sum());           // 0
Console.WriteLine(Sum(5));          // 5
Console.WriteLine(Sum(1, 2, 3, 4)); // 10
```

> [!NOTE]
> There can be only one `params` parameter, and it must be last in the list.

## Check yourself

> [!QUIZ]
> How does `out` differ from `ref`?
> ---
> A `ref` variable must be initialized beforehand, and the method needn't change it. An `out` variable needn't be initialized, but the method must assign it.

> [!QUIZ]
> Where can `params` appear in the parameter list?
> ---
> Only last, and there can be only one.
