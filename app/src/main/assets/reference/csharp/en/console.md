# Console input and output

The `Console` class handles the console. It can print text and read lines or single keys.

## Output: Write and WriteLine

`Console.WriteLine` prints a value followed by a line break, `Console.Write` prints without one:

```csharp
Console.Write("Lab ");
Console.Write(5);
Console.WriteLine();          // no arguments = just a line break
Console.WriteLine("submitted");
```

```output
Lab 5
submitted
```

## String interpolation

The most convenient way to build a string from values is **interpolation**. Put `$` before the quotes and write expressions in curly braces:

```csharp
string name = "Ivan";
int done = 7, total = 10;

Console.WriteLine($"{name} submitted {done} of {total} labs");
Console.WriteLine($"Remaining: {total - done}");
```

```output
Ivan submitted 7 of 10 labs
Remaining: 3
```

Older styles you will see in textbooks:

```csharp
Console.WriteLine(name + " submitted " + done + " labs");     // concatenation
Console.WriteLine("{0} submitted {1} labs", name, done);       // composite formatting
```

## Number formatting

A format goes after a colon inside `{ }`:

| Format | Value | Result |
|---|---|---|
| `{x:F2}` | 3.14159 | 3.14 |
| `{x:N0}` | 1234567 | 1,234,567 |
| `{x:P1}` | 0.256 | 25.6% |
| `{x:C}` | 99.5 | $99.50 |
| `{x,5}` | 42 | `   42` (width 5) |
| `{x:D3}` | 7 | 007 |

```csharp
double avg = 4.3333;
Console.WriteLine($"Average grade: {avg:F1}");
```

```output
Average grade: 4.3
```

> The decimal separator and currency symbol depend on the system language: a Russian locale prints a comma, an English one prints a dot.

## Input: ReadLine

`Console.ReadLine()` waits for the user to type a line and press Enter, and returns it as `string?`. If input has ended (for example, an empty file is redirected), it returns `null`.

```csharp
Console.Write("Enter your group: ");
string? group = Console.ReadLine();
Console.WriteLine($"Group: {group}");
```

## Reading numbers

`ReadLine` always returns a string. To get a number, the string has to be **converted**:

```csharp
Console.Write("Age: ");
int age = int.Parse(Console.ReadLine()!);

Console.Write("Height in meters: ");
double height = double.Parse(Console.ReadLine()!);

Console.WriteLine($"Next year you will be {age + 1}");
```

The `!` tells the compiler we are sure the value is not `null`.

> [!WARNING]
> If the input is not a number (say, "twenty"), `int.Parse` throws a `FormatException` and the program crashes. The safe way is `int.TryParse`.

```csharp
Console.Write("Number of labs: ");
if (int.TryParse(Console.ReadLine(), out int count))
{
    Console.WriteLine($"Accepted: {count}");
}
else
{
    Console.WriteLine("That is not an integer");
}
```

`TryParse` returns `true` when the conversion succeeds and puts the result into the variable after `out`.

## Reading a key

`Console.ReadKey()` waits for a single key press. It is often placed at the end of a program so the console window does not close immediately:

```csharp
Console.WriteLine("Press any key...");
Console.ReadKey();
```

## Non-Latin characters

If question marks appear instead of non-Latin letters, set the encoding at the start of the program:

```csharp
Console.OutputEncoding = System.Text.Encoding.UTF8;
Console.InputEncoding = System.Text.Encoding.UTF8;
```

## Check yourself

> [!QUIZ]
> What does `Console.ReadLine()` return?
> ---
> A `string?`: the entered text without the newline, or `null` if input has ended.

> [!QUIZ]
> How do you print a number with two decimal places?
> ---
> With a format in interpolation: `Console.WriteLine($"{x:F2}");`.
