# Arrays

An **array** is several values of the same type stored contiguously in memory. Indexes start at zero.

## Plain array

```cpp
int marks[5] = {8, 6, 9, 7, 10};
double prices[] = {9.5, 12.0, 3.2};   // size 3 — from the number of values
int zeros[10] = {};                   // all elements are 0

cout << marks[0] << '\n';             // 8
marks[1] = 7;
```

The size of a plain array must be known at compile time and never changes.

Number of elements:

```cpp
int count = sizeof(marks) / sizeof(marks[0]);   // 5
// or in C++17:
#include <iterator>
size_t n = std::size(marks);                     // 5
```

> [!WARNING]
> C++ doesn't check array bounds. `marks[5]` or `marks[-1]` won't fail right away — the program reads someone else's memory. This is **undefined behavior**: garbage, a crash, or "everything works" for a while.

## Iterating

```cpp
int sum = 0;
for (int i = 0; i < 5; i++)
    sum += marks[i];

for (int m : marks)          // range-based loop
    cout << m << ' ';
```

## std::array

From `<array>` — a fixed-size array that knows its length and can check indexes:

```cpp
#include <array>

array<int, 4> a = {3, 1, 4, 1};
cout << a.size() << '\n';     // 4
cout << a.at(2) << '\n';      // 4
// a.at(10) — out_of_range exception
```

## std::vector

If the size isn't known in advance, use `vector` — an array that grows:

```cpp
#include <vector>

vector<int> marks;
int m;
while (cin >> m && m > 0)
    marks.push_back(m);

cout << "Marks entered: " << marks.size() << '\n';
```

More about `vector` is in the "Classes and STL" chapter.

## Two-dimensional array

```cpp
int table[2][3] = {
    {1, 2, 3},
    {4, 5, 6}
};

for (int r = 0; r < 2; r++)
{
    for (int c = 0; c < 3; c++)
        cout << table[r][c] << ' ';
    cout << '\n';
}
```

```output
1 2 3 
4 5 6 
```

## Passing an array to a function

A plain array is passed as a pointer to its first element — the size is lost and is passed separately:

```cpp
double average(const int arr[], int size)
{
    int sum = 0;
    for (int i = 0; i < size; i++) sum += arr[i];
    return static_cast<double>(sum) / size;
}

int marks[] = {8, 6, 9};
cout << average(marks, 3);   // 7.66667
```

With `vector` it's simpler — it carries its size:

```cpp
double average(const vector<int>& v)
{
    int sum = 0;
    for (int x : v) sum += x;
    return static_cast<double>(sum) / v.size();
}
```

## Check yourself

> [!QUIZ]
> What happens when you access `a[10]` in a 5-element array?
> ---
> Undefined behavior: C++ doesn't check bounds. `at()` on `std::array` and `vector` does check.

> [!QUIZ]
> Why do functions taking a plain array also take its size?
> ---
> The array is passed as a pointer to its first element, so the length is lost.
