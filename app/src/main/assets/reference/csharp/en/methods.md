# Methods

A **method** is a named block of code that does one job. You can call it from different places in a program instead of copying the same code.

## Declaring a method

```csharp
static void SayHello()
{
    Console.WriteLine("Hello!");
}
```

Parts of the declaration:

| Part | Meaning |
|---|---|
| `static` | the method belongs to the class, no object is needed |
| `void` | result type; `void` means the method returns nothing |
| `SayHello` | method name, written in PascalCase by convention |
| `()` | parameter list, empty here |
| `{ }` | method body |

## Calling a method

To run a method, write its name followed by parentheses:

```csharp
SayHello();
SayHello();
```

```output
Hello!
Hello!
```

> [!NOTE]
> In a program with top-level statements (no `Main`), methods can be declared right in `Program.cs`, after the main code. Inside a class they go next to `Main`.

## Parameters

**Parameters** are the inputs of a method. They are listed in parentheses with their types, and **arguments** are passed when calling:

```csharp
static void PrintMark(string subject, int mark)
{
    Console.WriteLine($"{subject}: {mark}");
}

PrintMark("Math", 9);
PrintMark("Physics", 7);
```

```output
Math: 9
Physics: 7
```

Arguments must match the parameters in number, order and type.

## Returning a value: return

If a method computes a result, specify its type instead of `void` and return the value with `return`:

```csharp
static double Average(int a, int b, int c)
{
    return (a + b + c) / 3.0;
}

double avg = Average(8, 9, 7);
Console.WriteLine(avg); // 8
```

`return` ends the method immediately. In a method with a result type, every execution path must end with `return`:

```csharp
static string Grade(int mark)
{
    if (mark >= 9) return "excellent";
    if (mark >= 6) return "good";
    if (mark >= 4) return "pass";
    return "fail";
}
```

> [!WARNING]
> Remove the last `return` and you get error CS0161: not all code paths return a value.

In a `void` method, `return;` without a value is used to exit early:

```csharp
static void PrintPositive(int n)
{
    if (n <= 0) return;
    Console.WriteLine(n);
}
```

## Expression-bodied methods

If the body is a single expression, it can be written with `=>`:

```csharp
static int Square(int x) => x * x;
static bool IsEven(int n) => n % 2 == 0;
static void Line() => Console.WriteLine(new string('-', 20));
```

## Local variables

Variables declared inside a method are visible only there. Two methods can have variables with the same names — they don't interfere:

```csharp
static int Sum(int[] items)
{
    int total = 0;
    foreach (int item in items) total += item;
    return total;
}
```

`total` exists only while `Sum` runs and disappears when the method returns.

## Check yourself

> [!QUIZ]
> What does `void` mean in a method declaration?
> ---
> The method returns nothing.

> [!QUIZ]
> When do you get error CS0161?
> ---
> When not every execution path of a method with a result type ends with `return`.
