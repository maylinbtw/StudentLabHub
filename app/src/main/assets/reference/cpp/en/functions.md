# Functions

A **function** is a named block of code with parameters and a result. It is declared once and called as often as needed.

## Declaring and calling

```cpp
#include <iostream>
using namespace std;

double average(int a, int b, int c)
{
    return (a + b + c) / 3.0;
}

void printLine(int width)
{
    for (int i = 0; i < width; i++) cout << '-';
    cout << '\n';
}

int main()
{
    printLine(20);
    cout << "Average: " << average(8, 9, 7) << '\n';
    printLine(20);
}
```

```output
--------------------
Average: 8
--------------------
```

First the result type (`void` returns nothing), then the name and parameters.

## Prototype

A function must be declared **before** it is called. If you prefer to write the body after `main`, put a **prototype** — a declaration without a body — at the top:

```cpp
int square(int x);          // prototype

int main()
{
    cout << square(5);      // 25
}

int square(int x)
{
    return x * x;
}
```

In larger projects prototypes go into a `.h` header file and the implementation into a `.cpp` file.

## Pass by value and by reference

By default a function gets a **copy** of the argument. An `&` on a parameter makes it a **reference** — the function works with the original variable:

```cpp
void addOne(int n)   { n++; }
void addOneRef(int& n) { n++; }

int x = 5;
addOne(x);      // x = 5
addOneRef(x);   // x = 6
```

Swapping values:

```cpp
void swapValues(int& a, int& b)
{
    int t = a;
    a = b;
    b = t;
}
```

Large objects (strings, vectors) are passed by **const reference** — no copy and no right to change:

```cpp
void printName(const string& name)
{
    cout << name << '\n';
}
```

## Default parameters

```cpp
void greet(const string& name, const string& text = "Hello")
{
    cout << text << ", " << name << "!\n";
}

greet("Anna");                   // Hello, Anna!
greet("Oleg", "Good morning");   // Good morning, Oleg!
```

Default parameters go at the end of the list and are specified only in the prototype (if there is one).

## Overloading

You can declare several functions with the same name and different parameters:

```cpp
int maxOf(int a, int b) { return a > b ? a : b; }
double maxOf(double a, double b) { return a > b ? a : b; }
int maxOf(int a, int b, int c) { return maxOf(maxOf(a, b), c); }
```

## Recursion

A function can call itself. A base case is required:

```cpp
long long factorial(int n)
{
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

cout << factorial(10); // 3628800
```

> [!WARNING]
> If a function with a result type never reaches `return`, the behavior is undefined: the program may return garbage. The compiler usually warns about this — don't ignore warnings.
