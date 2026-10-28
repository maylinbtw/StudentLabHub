# Variables and data types

A variable in C++ is a named piece of memory of a specific type. The type is set at declaration and never changes.

## Declaration and initialization

```cpp
int count;              // declaration: the value is undefined!
int labs = 12;          // initialization with =
double avg(7.5);        // with parentheses
int marks{9};           // uniform initialization (C++11)
int a = 1, b = 2;       // several variables
```

> [!WARNING]
> An uninitialized local variable holds "garbage" — whatever value was in memory. The compiler does not forbid it. Always set an initial value.

Braces are stricter: `int x{3.7};` is an error, while `int x = 3.7;` silently drops the fraction.

## Basic types

| Type | Size (typical) | Holds |
|---|---|---|
| `bool` | 1 byte | `true` or `false` |
| `char` | 1 byte | a character or a small number |
| `short` | 2 bytes | integer ±32,767 |
| `int` | 4 bytes | integer ±2.1 billion |
| `long long` | 8 bytes | integer ±9.2·10¹⁸ |
| `float` | 4 bytes | fraction, ~7 digits |
| `double` | 8 bytes | fraction, ~15 digits |

The `unsigned` modifier makes a type non-negative and doubles the upper limit: `unsigned int` goes from 0 to ~4.3 billion. `sizeof(int)` shows the exact size on your system.

## Strings and characters

```cpp
#include <string>

char grade = 'A';                  // one character — single quotes
std::string name = "Anna";         // a string — double quotes
std::string full = name + " K.";   // concatenation
int len = name.length();           // length in bytes
```

> `std::string` stores bytes, not letters. A non-Latin letter in UTF-8 takes 2 or more bytes, so `length()` may be larger than the number of letters.

## auto

`auto` infers the type from the value:

```cpp
auto x = 10;        // int
auto pi = 3.14;     // double
auto c = 'z';       // char
auto s = std::string("text");
```

## Constants

```cpp
const int MaxLabs = 20;           // cannot change after initialization
constexpr double Pi = 3.14159;    // computed at compile time
```

## Scope

```cpp
int global = 1;          // global — visible in the whole file

int main()
{
    int local = 2;       // local — until the end of main
    {
        int inner = 3;   // visible only in this block
    }
    // inner is not accessible here
    return 0;
}
```

Global variables are zero-initialized automatically, local ones are not.

## Type casts

```cpp
double d = 7.9;
int i = static_cast<int>(d);             // 7 — explicit cast
double r = static_cast<double>(7) / 2;   // 3.5
int old = (int)d;                         // C-style cast, better avoided
```
