# Program structure

A C# program consists of **statements** grouped into **blocks**, methods and classes.

## Statements

A statement is a single action: declaring a variable, calling a method, assigning a value. Every simple statement ends with a semicolon:

```csharp
int a = 5;
a = a + 1;
Console.WriteLine(a);
```

You can put several statements on one line, but the code becomes harder to read:

```csharp
int a = 5; a = a + 1; Console.WriteLine(a);
```

## Code blocks

Curly braces `{ }` group statements into a block. Blocks are used by methods, classes, conditions and loops:

```csharp
if (a > 5)
{
    Console.WriteLine("Greater than five");
    Console.WriteLine("A block of two statements");
}
```

A variable declared inside a block is visible only in that block.

## Case sensitivity

C# distinguishes uppercase and lowercase letters. `Console`, `console` and `CONSOLE` are three different names, and only the first one is correct:

```csharp
console.WriteLine("Error"); // CS0103: the name console does not exist
```

## Comments

The compiler ignores comments. They explain the code:

```csharp
// Single-line comment — up to the end of the line

/*
   Multi-line comment.
   Handy for temporarily disabling a piece of code.
*/

/// <summary>
/// XML comment: a method description for IDE hints.
/// </summary>
```

## Namespaces and using

.NET classes are organized into **namespaces**. For example, `Console` lives in `System`, and `List<T>` in `System.Collections.Generic`. The `using` directive lets you skip the full name:

```csharp
using System.Text;

var builder = new StringBuilder(); // instead of System.Text.StringBuilder
```

> New projects have **implicit usings** enabled: the most common namespaces (`System`, `System.IO`, `System.Linq` and others) are imported automatically. That is why the template has no `using System;` line.

## Naming conventions

| What | Style | Example |
|---|---|---|
| Classes, methods, properties | PascalCase | `StudentList`, `GetAverage` |
| Local variables, parameters | camelCase | `studentName`, `count` |
| Constants | PascalCase | `MaxScore` |
| Private fields | _camelCase | `_items` |

The compiler does not enforce these rules, but .NET itself and most projects follow them.

## Check yourself

> [!QUIZ]
> What is the `Main` method?
> ---
> The program's entry point: execution starts there. With top-level statements the compiler generates it for you.

> [!QUIZ]
> What is the `using System;` directive for?
> ---
> It imports a namespace so you can write `Console` instead of `System.Console`.
