# Loops

A loop repeats a block of code while a condition holds. C# has four kinds of loops: `for`, `while`, `do…while` and `foreach`. The last one is covered in the article about arrays.

## The for loop

`for` is handy when the number of repetitions is known in advance:

```text
for (initializer; condition; step)
{
    loop body
}
```

```csharp
for (int i = 1; i <= 5; i++)
{
    Console.Write($"{i} ");
}
```

```output
1 2 3 4 5 
```

How it works:

1. The initializer runs once: `int i = 1`.
2. The condition `i <= 5` is checked. If it is false, the loop ends.
3. The body runs.
4. The step `i++` runs, and everything repeats from step 2.

The step can be anything: `i += 2`, `i--`, `i *= 2`.

```csharp
for (int i = 10; i > 0; i -= 3)
    Console.Write($"{i} ");   // 10 7 4 1
```

## The while loop

`while` checks the condition **before** each repetition. If the condition is false from the start, the body never runs:

```csharp
int left = 3;

while (left > 0)
{
    Console.WriteLine($"Labs left: {left}");
    left--;
}
```

```output
Labs left: 3
Labs left: 2
Labs left: 1
```

`while` fits when the number of repetitions is unknown, for example when reading input until an exit command.

## The do…while loop

`do…while` checks the condition **after** the body, so the body runs at least once:

```csharp
int number;

do
{
    Console.Write("Enter a number from 1 to 10: ");
}
while (!int.TryParse(Console.ReadLine(), out number) || number < 1 || number > 10);

Console.WriteLine($"Accepted: {number}");
```

This is the usual way to keep asking until the user enters a valid value.

> Note the semicolon after `while (…)` at the end of `do…while` — without it you get an error.

## break and continue

- `break` ends the loop immediately;
- `continue` skips the rest of the current repetition and moves on to the next one.

```csharp
for (int i = 1; i <= 10; i++)
{
    if (i % 2 == 0)
        continue;       // skip even numbers
    if (i > 7)
        break;          // stop after 7
    Console.Write($"{i} ");
}
```

```output
1 3 5 7 
```

## Nested loops

A loop can sit inside another one. The inner loop runs completely on every step of the outer loop:

```csharp
for (int row = 1; row <= 3; row++)
{
    for (int col = 1; col <= 3; col++)
    {
        Console.Write($"{row * col,4}");
    }
    Console.WriteLine();
}
```

```output
   1   2   3
   2   4   6
   3   6   9
```

`break` in a nested loop ends only the inner loop.

## Infinite loops

If the condition never becomes false, the loop never ends. Sometimes this is done on purpose, with `break` as the way out:

```csharp
while (true)
{
    Console.Write("> ");
    string? input = Console.ReadLine();
    if (input == "exit")
        break;
    Console.WriteLine($"You typed: {input}");
}
```

> [!WARNING]
> If your program "hangs", check whether the loop changes the variable used in the condition. A forgotten `i++` is the most common cause of an infinite loop.
