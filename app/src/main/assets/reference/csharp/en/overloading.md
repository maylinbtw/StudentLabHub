# Method overloading

**Overloading** means several methods with the same name but different parameters. The compiler picks the right one based on the call's arguments.

## Example

```csharp
static int Max(int a, int b) => a > b ? a : b;

static int Max(int a, int b, int c) => Max(Max(a, b), c);

static double Max(double a, double b) => a > b ? a : b;

Console.WriteLine(Max(3, 8));       // 8 — Max(int, int) is called
Console.WriteLine(Max(3, 8, 5));    // 8 — Max(int, int, int) is called
Console.WriteLine(Max(2.5, 1.5));   // 2.5 — Max(double, double) is called
```

`Console.WriteLine` works this way: it has versions for `int`, `double`, `string`, `bool` and other types.

## What overloads can differ in

Methods are different if their **signature** differs:

- number of parameters;
- parameter types;
- order of parameter types;
- `ref`, `out`, `in` modifiers.

```csharp
static void Print(int n) { }
static void Print(string s) { }
static void Print(int n, string s) { }
static void Print(string s, int n) { }
```

## What is not overloading

The return type is **not** part of the signature:

```csharp
static int Parse(string s) => int.Parse(s);
static double Parse(string s) => double.Parse(s); // CS0111: already defined
```

Parameter names don't count either: `Print(int a)` and `Print(int b)` are the same method.

> [!WARNING]
> `ref` and `out` are distinguished only from the version without a modifier. You can't declare both `F(ref int x)` and `F(out int x)`.

## How an overload is chosen

1. First, an exact type match is looked for.
2. If there is none — a version that needs an implicit conversion (for example, `int` → `long` → `double`).
3. If several versions fit equally well, you get an ambiguous call error CS0121.

```csharp
static void Show(long x) => Console.WriteLine("long");
static void Show(double x) => Console.WriteLine("double");

Show(5);     // long — int is closer to long than to double
Show(5.0);   // double
```

## Overloads or default parameters

Often one method with optional parameters is enough instead of several overloads:

```csharp
// two overloads
static void Log(string text) => Log(text, "INFO");
static void Log(string text, string level) =>
    Console.WriteLine($"[{level}] {text}");

// the same with one method
static void Log2(string text, string level = "INFO") =>
    Console.WriteLine($"[{level}] {text}");
```

Overloads are needed when versions take **different types** or behave differently. If only a default value changes, an optional parameter is simpler.
