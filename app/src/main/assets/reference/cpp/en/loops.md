# Loops

C++ has `for`, `while`, `do…while` and the range-based `for (x : collection)` loop.

## for

```cpp
for (int i = 0; i < 5; i++)
{
    cout << i << ' ';
}
cout << '\n';
```

```output
0 1 2 3 4 
```

The header has three parts separated by `;`: initializer, continuation condition, step. Any part can be omitted: `for (;;)` is an infinite loop.

## while and do…while

```cpp
int n = 1234, digits = 0;
while (n > 0)
{
    n /= 10;
    digits++;
}
cout << "Digits: " << digits << '\n';     // 4
```

`do…while` runs the body at least once — handy for menus and input validation:

```cpp
int choice;
do
{
    cout << "1 — new lab, 2 — list, 0 — exit: ";
    cin >> choice;
} while (choice != 0);
```

## break and continue

```cpp
for (int i = 1; i <= 20; i++)
{
    if (i % 3 != 0) continue;   // skip non-multiples of three
    if (i > 12) break;          // leave the loop
    cout << i << ' ';           // 3 6 9 12
}
```

## Range-based for (C++11)

Walks through every element of an array, `vector`, string and other containers:

```cpp
#include <vector>

vector<int> marks = {8, 6, 9, 7};
int sum = 0;
for (int m : marks)
{
    sum += m;
}
cout << "Average: " << static_cast<double>(sum) / marks.size() << '\n';
```

```output
Average: 7.5
```

To modify elements, use a reference `&`:

```cpp
for (int& m : marks)
    m += 1;                 // add 1 to every mark

for (const auto& name : names)
    cout << name << '\n';   // const auto& — no string copies
```

## Nested loops

```cpp
for (int row = 1; row <= 3; row++)
{
    for (int col = 1; col <= row; col++)
        cout << '*';
    cout << '\n';
}
```

```output
*
**
***
```

`break` leaves only the inner loop. To leave both, use a flag or move the loops into a function and `return`.

## Common mistakes

- `for (int i = 0; i <= n; i++)` over an array of `n` elements — out of bounds on the last step;
- a `;` right after the header: `for (...);` — the loop body is empty and the block below runs once;
- comparing an `int` with `size()` triggers a signedness warning: use `size_t i` or a range-based loop.
