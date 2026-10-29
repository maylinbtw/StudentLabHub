# Data types

C# is a **statically and strongly typed** language: every variable has a type known at compile time. The type defines which values can be stored, how much memory they take and which operations are allowed.

## Integers

| Type | Size | Range |
|---|---|---|
| `byte` | 1 byte | 0 … 255 |
| `sbyte` | 1 byte | −128 … 127 |
| `short` | 2 bytes | −32,768 … 32,767 |
| `ushort` | 2 bytes | 0 … 65,535 |
| `int` | 4 bytes | about ±2.1 billion |
| `uint` | 4 bytes | 0 … about 4.3 billion |
| `long` | 8 bytes | about ±9.2·10¹⁸ |
| `ulong` | 8 bytes | 0 … about 1.8·10¹⁹ |

Most of the time you use `int`, and `long` for very large values. Exact limits are available as `int.MinValue`, `int.MaxValue` and so on.

```csharp
int students = 28;
long population = 9_200_000_000; // _ separates digit groups for readability
byte level = 255;

Console.WriteLine(int.MaxValue);
```

```output
2147483647
```

## Fractional numbers

| Type | Size | Precision | Suffix |
|---|---|---|---|
| `float` | 4 bytes | ~6–9 digits | `f` |
| `double` | 8 bytes | ~15–17 digits | — (or `d`) |
| `decimal` | 16 bytes | 28–29 digits | `m` |

A fractional literal without a suffix is a `double`. Other types require the suffix:

```csharp
double distance = 12.5;
float speed = 3.6f;
decimal price = 199.99m; // store money in decimal
```

> `double` stores numbers in binary, so `0.1 + 0.2` gives `0.30000000000000004`. Use `decimal` for money — it works in base 10 without such errors.

## Boolean type

`bool` has only two values: `true` and `false`. Comparisons return it and conditions use it:

```csharp
bool isPassed = true;
bool isAdult = age >= 18;
```

Unlike C++, a number cannot stand in for `bool`: `if (1)` is an error in C#.

## Characters and strings

`char` is a single Unicode character in single quotes. `string` is a sequence of characters in double quotes:

```csharp
char grade = 'A';
string group = "CS-44";
string path = @"C:\Projects\Lab1"; // @ — backslashes are not escapes
string empty = "";
```

Special characters use `\`: `\n` is a line break, `\t` a tab, `\"` a quote, `\\` the backslash itself.

## The object type

`object` is the base type of every other type. An `object` variable can hold anything, but to work with the value you have to cast it back to the right type:

```csharp
object value = 42;
int number = (int)value;
```

## Value and reference types

| Value types | Reference types |
|---|---|
| `int`, `double`, `bool`, `char`, `decimal`, structs, enums | `string`, `object`, arrays, classes |
| the variable holds the value itself | the variable holds a reference to an object |
| assignment copies the value | assignment copies the reference |

The difference matters more in the chapter about classes. For now, remember that an `int` is copied entirely while an array is copied by reference.

## Default values

An uninitialized class field gets a default value: `0` for numbers, `false` for `bool`, `'\0'` for `char` and `null` for reference types. You can get it with `default`:

```csharp
Console.WriteLine(default(int));    // 0
Console.WriteLine(default(bool));   // False
```

## Nullable types

A value type cannot be `null`. To allow "no value", add `?` to the type:

```csharp
int? mark = null;   // no grade yet
mark = 5;

if (mark.HasValue)
{
    Console.WriteLine(mark.Value);
}
```

For reference types, `?` (for example `string?`) tells the compiler that `null` is expected here, so it stops issuing a warning.

## Check yourself

> [!QUIZ]
> Which type should you use for money?
> ---
> `decimal`: it stores decimal fractions exactly, without the rounding errors of `double`.

> [!QUIZ]
> How do value types differ from reference types?
> ---
> Value types (`int`, `double`, `struct`) hold the value itself and are copied whole. Reference types (`string`, arrays, classes) hold a reference to an object.
