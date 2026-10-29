# Exceptions: try, catch, finally

An **exception** is an error at run time: division by zero, a malformed number, an index outside an array. If an exception isn't handled, the program stops with an error message.

```csharp
int a = 10, b = 0;
Console.WriteLine(a / b); // DivideByZeroException
```

## try and catch

Risky code goes into `try`, and the reaction to an error goes into `catch`:

```csharp
try
{
    Console.Write("Number: ");
    int n = int.Parse(Console.ReadLine()!);
    Console.WriteLine(100 / n);
}
catch (FormatException)
{
    Console.WriteLine("Enter a whole number");
}
catch (DivideByZeroException)
{
    Console.WriteLine("Cannot divide by zero");
}
```

When an exception occurs in `try`, the rest of the block is skipped and the first matching `catch` runs. After it the program continues.

## The exception object

In `catch` you can get the object itself and read the details:

```csharp
try
{
    int[] marks = { 7, 8 };
    Console.WriteLine(marks[5]);
}
catch (IndexOutOfRangeException ex)
{
    Console.WriteLine($"Error: {ex.Message}");
}
```

```output
Error: Index was outside the bounds of the array.
```

## Common exceptions

| Exception | When it happens |
|---|---|
| `FormatException` | `int.Parse("abc")` — the string has the wrong format |
| `DivideByZeroException` | integer division by 0 |
| `IndexOutOfRangeException` | index outside the array bounds |
| `NullReferenceException` | accessing members of a `null` object |
| `OverflowException` | overflow inside a `checked` block |
| `ArgumentException` | an invalid argument was passed to a method |
| `FileNotFoundException` | the file was not found |

## Order of catch blocks

Blocks are checked top to bottom. More specific types go first, the general `Exception` goes last:

```csharp
try
{
    // ...
}
catch (FormatException)
{
    Console.WriteLine("Wrong format");
}
catch (Exception ex)
{
    Console.WriteLine($"Something went wrong: {ex.Message}");
}
```

> [!WARNING]
> If `catch (Exception)` comes first, the other blocks can never run — the compiler reports error CS0160.

## finally

The `finally` block always runs — with or without an error. Releasing resources goes there:

```csharp
StreamReader? reader = null;
try
{
    reader = new StreamReader("marks.txt");
    Console.WriteLine(reader.ReadLine());
}
catch (FileNotFoundException)
{
    Console.WriteLine("File not found");
}
finally
{
    reader?.Dispose();
    Console.WriteLine("Done");
}
```

For files and other resources `using` is shorter — it calls `Dispose` itself:

```csharp
using var reader = new StreamReader("marks.txt");
Console.WriteLine(reader.ReadLine());
```

## throw — raising an exception

A method can report an error itself with `throw`:

```csharp
static double Average(int[] marks)
{
    if (marks.Length == 0)
        throw new ArgumentException("The marks array is empty");
    return marks.Average();
}
```

## Exceptions or checks

Exceptions are for unexpected situations. If an error is expected (the user typed something that isn't a number), check in advance:

```csharp
if (int.TryParse(input, out int n))
    Console.WriteLine(n * 2);
else
    Console.WriteLine("Not a number");
```

`TryParse` doesn't throw and is faster than `try/catch` around `Parse`.
