# Your first C++ program

**C++** is a compiled language: the source code is first translated by a compiler into an executable file, and only then run. C++ is used for system software, game engines, drivers and anything where speed matters.

## The code

Create a file named `main.cpp`:

```cpp
#include <iostream>

int main()
{
    std::cout << "Hello, World!" << std::endl;
    return 0;
}
```

Line by line:

- `#include <iostream>` brings in the input/output library. Lines starting with `#` are handled by the **preprocessor** before compilation;
- `int main()` is the main function, the **entry point**. Execution starts here;
- curly braces `{ }` enclose the function body;
- `std::cout` is the standard output stream (the console), and `<<` sends data into it;
- `std::endl` is a line break;
- `return 0;` means the program finished successfully. A non-zero value signals an error.

## Compiling and running

On Linux or in an MSYS2 terminal on Windows:

```bash
g++ main.cpp -o hello
./hello
```

```output
Hello, World!
```

Useful compiler options:

```bash
g++ -std=c++20 -Wall -Wextra main.cpp -o hello   # C++20 standard and all warnings
g++ -g main.cpp -o hello                          # with debug info (for gdb)
g++ -O2 main.cpp -o hello                         # with optimization
```

> In Visual Studio, create a "Console App" project and press **Ctrl+F5**. In Code::Blocks and CLion, the "Build and run" button is enough.

## How a build works

1. The **preprocessor** handles `#include` and other directives.
2. The **compiler** turns each `.cpp` into an object file with machine code.
3. The **linker** combines object files and libraries into an executable.

That is why there are two kinds of errors: compile errors (syntax, types) and link errors (`undefined reference` — a function is declared but its definition is not found).

## using namespace std

All standard names live in the `std` namespace. To avoid typing `std::` every time, teaching programs often add:

```cpp
#include <iostream>
using namespace std;

int main()
{
    cout << "Hello!" << endl;
    return 0;
}
```

> Large projects and header files avoid `using namespace std;` because it can cause name clashes. For lab assignments it is fine.

## Comments

```cpp
// single-line comment

/* multi-line
   comment */
```

## Common beginner mistakes

- a missing `;` at the end of a statement — the compiler points at the **next** line;
- `Main` with a capital letter — the linker cannot find the entry point;
- garbled non-Latin text in the Windows console — call `setlocale(LC_ALL, "");` at the start of `main` and include `<clocale>`.

## Check yourself

> [!QUIZ]
> Which g++ command compiles `main.cpp` into a program called `app`?
> ---
> `g++ main.cpp -o app`, then run it with `./app`.

> [!QUIZ]
> What does `#include <iostream>` do?
> ---
> It includes the standard library header with the `cin` and `cout` I/O streams.
