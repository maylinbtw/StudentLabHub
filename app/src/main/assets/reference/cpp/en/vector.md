# vector

`std::vector` from `<vector>` is a dynamic array: elements are stored contiguously, and the size changes as you add them. It is the main container in C++.

## Creating

```cpp
#include <vector>

vector<int> a;                      // empty
vector<int> b = {7, 9, 4};          // with values
vector<int> c(5);                   // 5 zeros
vector<string> d(3, "—");           // 3 strings "—"
```

## Adding and removing

```cpp
vector<int> marks = {7, 9};

marks.push_back(4);                 // to the end: 7 9 4
marks.insert(marks.begin(), 10);    // to the front: 10 7 9 4
marks.pop_back();                   // remove the last: 10 7 9
marks.erase(marks.begin() + 1);     // remove the second: 10 9
marks.clear();                      // empty
```

`begin()` is an **iterator** to the first element, `begin() + i` points to the element at index `i`.

## Access

```cpp
vector<int> v = {7, 9, 4};

v[0];          // 7 — no bounds check
v.at(1);       // 9 — checked, otherwise out_of_range exception
v.front();     // 7
v.back();      // 4
v.size();      // 3
v.empty();     // false
```

> [!WARNING]
> `v.size()` returns an unsigned `size_t`. The loop `for (int i = 0; i < v.size() - 1; i++)` on an empty vector runs a huge number of times: `0 - 1` for an unsigned type is the maximum value.

## Iterating

```cpp
for (size_t i = 0; i < v.size(); i++)
    cout << v[i] << ' ';

for (int x : v)           // a copy of each element
    cout << x << ' ';

for (int& x : v)          // a reference — can modify
    x *= 2;

for (const auto& s : names)   // for strings and objects — no copies
    cout << s << '\n';
```

## Vector of objects

```cpp
struct Lab
{
    string title;
    int mark;
};

vector<Lab> labs;
labs.push_back({"Arrays", 8});
labs.emplace_back(Lab{"Strings", 9});

for (const auto& lab : labs)
    cout << lab.title << ": " << lab.mark << '\n';
```

## Two-dimensional vector

```cpp
int rows = 3, cols = 4;
vector<vector<int>> grid(rows, vector<int>(cols, 0));

grid[1][2] = 5;
cout << grid.size() << "x" << grid[0].size(); // 3x4
```

## size and capacity

| Method | Meaning |
|---|---|
| `size()` | how many elements there are now |
| `capacity()` | how many fit in the allocated memory |
| `reserve(n)` | allocate memory for n elements in advance |
| `resize(n)` | change the number of elements |

If you know in advance how many elements there will be, `reserve` avoids extra reallocations.

> [!NOTE]
> After `push_back` a vector may move to another memory area. Pointers, references and iterators to old elements then become invalid.
