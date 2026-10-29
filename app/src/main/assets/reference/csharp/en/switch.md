# The switch statement

`switch` compares one value against a set of options. It is more convenient than a long `else if` chain when the same variable is checked each time.

## The switch statement

```csharp
int day = 3;

switch (day)
{
    case 1:
        Console.WriteLine("Monday");
        break;
    case 2:
        Console.WriteLine("Tuesday");
        break;
    case 3:
        Console.WriteLine("Wednesday");
        break;
    default:
        Console.WriteLine("Another day");
        break;
}
```

```output
Wednesday
```

- each `case` is followed by a constant of the same type as the checked value;
- `break` ends the `switch`;
- the `default` branch runs when no `case` matches. It is optional.

> [!WARNING]
> Unlike C++, C# does not allow falling through from one non-empty `case` into the next. Every branch must end with `break`, `return`, `throw` or `goto case`, otherwise you get error CS0163.

## Several values in one branch

Empty `case` labels can be stacked — they share one piece of code:

```csharp
switch (day)
{
    case 6:
    case 7:
        Console.WriteLine("Weekend");
        break;
    default:
        Console.WriteLine("Study");
        break;
}
```

## switch on strings

You can switch on numbers, characters, strings and enums:

```csharp
string command = "help";

switch (command)
{
    case "start":
        Console.WriteLine("Starting");
        break;
    case "help":
        Console.WriteLine("List of commands");
        break;
    default:
        Console.WriteLine("Unknown command");
        break;
}
```

String comparison in `switch` is case-sensitive: `"Help"` does not match `"help"`. Normalize the case beforehand: `command.ToLower()`.

## Conditions in case: when

Since C# 7 a `case` can have an extra condition:

```csharp
int mark = 8;

switch (mark)
{
    case int m when m >= 9:
        Console.WriteLine("Excellent");
        break;
    case int m when m >= 4:
        Console.WriteLine("Passed");
        break;
    default:
        Console.WriteLine("Retake");
        break;
}
```

## The switch expression

C# 8 added a compact form — the **switch expression**. It produces a value directly:

```csharp
int day = 6;

string name = day switch
{
    1 => "Mon",
    2 => "Tue",
    3 => "Wed",
    4 => "Thu",
    5 => "Fri",
    6 or 7 => "Weekend",
    _ => "Error"
};

Console.WriteLine(name);
```

```output
Weekend
```

- options are separated by commas, the result follows the `=>` arrow;
- `_` means "anything else", like `default`;
- if nothing matches and there is no `_`, an exception is thrown at run time.

## Relational patterns

Ranges are easy to check in a switch expression:

```csharp
int score = 73;

string grade = score switch
{
    >= 90 => "A",
    >= 75 => "B",
    >= 60 => "C",
    _ => "F"
};

Console.WriteLine(grade); // C
```

> The `or`, `and` and `>=` patterns in `case` labels and switch expressions appeared in C# 9. In older versions use `when`.

## Check yourself

> [!QUIZ]
> What happens if you forget `break` in a classic `switch` case?
> ---
> Compile error CS0163: C# doesn't allow falling through to the next `case` when the current one has code.

> [!QUIZ]
> What does `_` mean in a `switch` expression?
> ---
> The "everything else" branch, like `default`.
