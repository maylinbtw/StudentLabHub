# The C# language and .NET

**C#** (pronounced "C sharp") is a general-purpose object-oriented language from Microsoft. It is used for desktop and web applications, servers, mobile apps, Unity games and command-line tools.

## What .NET is

A C# program cannot run on its own: it needs the **.NET** platform. .NET consists of:

- a **compiler** that turns `.cs` source code into the intermediate language IL;
- the **CLR runtime** that loads IL and converts it to machine code while the program runs (JIT compilation);
- a **class library** with ready-made types for strings, files, collections, networking and much more;
- the **SDK and the `dotnet` tool** for creating, building and running projects.

Thanks to the intermediate language, the same assembly runs on Windows, Linux and macOS wherever the .NET runtime is installed.

## From source code to result

1. You write code in `.cs` files.
2. `dotnet build` compiles them into a `.dll` assembly containing IL.
3. `dotnet run` (or launching the built file) hands the assembly to the CLR.
4. The CLR compiles the methods it needs into machine code and executes them.

> The garbage collector (GC) in the CLR frees memory that is no longer used. You never call `delete` by hand as in C++.

## Versions

The language and the platform evolve together: each new .NET release ships a new C# version. The examples in this reference target modern .NET (8 and later) and programs without an explicit `Main` method. If your college uses an old Visual Studio, some syntax may not be supported — the articles point this out.

## What you need

| Tool | Purpose |
|---|---|
| .NET SDK | The compiler and the `dotnet` tool |
| Visual Studio | Full IDE for Windows |
| VS Code + C# Dev Kit | Lightweight editor for any OS |
| JetBrains Rider | IDE for Windows, Linux and macOS |

To check that the SDK is installed, run in a terminal:

```bash
dotnet --version
```

```output
8.0.404
```

Your version number may differ — what matters is that the command does not fail.
