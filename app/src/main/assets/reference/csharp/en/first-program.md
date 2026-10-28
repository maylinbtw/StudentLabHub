# Your first program

Let's create a console application that prints a greeting. There are two ways: from the terminal and in Visual Studio.

## Creating a project in the terminal

`dotnet new console` creates a project from the console application template. The `-o` option sets the folder:

```bash
dotnet new console -o HelloApp
cd HelloApp
dotnet run
```

```output
Hello, World!
```

The folder will contain:

- `HelloApp.csproj` — the project file: .NET version, build settings, package references;
- `Program.cs` — the program source code;
- `bin` and `obj` — build output folders.

## Creating a project in Visual Studio

1. "Create a new project" → the **Console App** (C#) template.
2. Enter a name and a folder, choose the .NET version.
3. Press **F5** (run with debugging) or **Ctrl+F5** (without debugging, the console window stays open).

## The code

Open `Program.cs`. The template contains a single line:

```csharp
Console.WriteLine("Hello, World!");
```

- `Console` is the class for working with the console;
- `WriteLine` is a method that prints text followed by a line break;
- the text in double quotes is a string literal;
- the semicolon ends the statement.

Let's change the program so it asks for a name:

```csharp
Console.Write("What is your name? ");
string? name = Console.ReadLine();
Console.WriteLine($"Hello, {name}!");
```

```output
What is your name? Anna
Hello, Anna!
```

`Write` prints text without a line break, `ReadLine` waits for input and returns a string, and the `$` before the quotes lets you insert values inside curly braces.

## The Main method

The code above uses **top-level statements**: the compiler wraps it into a `Main` method for you. Older projects and textbooks look like this:

```csharp
using System;

namespace HelloApp
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Hello, World!");
        }
    }
}
```

Both versions do the same thing. `Main` is the **entry point** where execution starts. The `args` parameter holds the command-line arguments.

> [!WARNING]
> Top-level statements appeared in C# 9 (.NET 5). If your college environment creates a project with `Main`, write your code inside the curly braces of `Main`.
