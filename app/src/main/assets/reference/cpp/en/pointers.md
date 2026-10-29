# Pointers and dynamic memory

A **pointer** is a variable that stores the **address** of another variable in memory.

## Address and dereferencing

```cpp
int mark = 8;
int* p = &mark;      // & — take the address

cout << p << '\n';   // an address, e.g. 0x7ffd5c3a
cout << *p << '\n';  // 8 — * reads the value at the address

*p = 10;             // change mark through the pointer
cout << mark;        // 10
```

| Notation | Meaning |
|---|---|
| `int* p` | declares a pointer to `int` |
| `&x` | the address of variable `x` |
| `*p` | the value at address `p` |
| `p->field` | a field of an object through a pointer, same as `(*p).field` |

## nullptr

A pointer that points nowhere equals `nullptr`:

```cpp
int* p = nullptr;
if (p != nullptr)
    cout << *p;
```

> [!WARNING]
> Dereferencing `nullptr` or an uninitialized pointer is undefined behavior, usually a crash. Initialize pointers right away.

## Pointers and arrays

An array name turns into a pointer to its first element. You can add a number to a pointer:

```cpp
int a[] = {10, 20, 30};
int* p = a;

cout << *p << '\n';        // 10
cout << *(p + 2) << '\n';  // 30 — same as a[2]
```

## Pointer or reference

| | Pointer `int*` | Reference `int&` |
|---|---|---|
| can be empty | yes, `nullptr` | no |
| can be re-pointed | yes | no, bound forever |
| access syntax | `*p`, `p->x` | like a regular variable |

If you just need to change a function argument, use a reference. Use a pointer when a value may be missing or you need dynamic memory.

## new and delete

Memory allocated with `new` lives until it is explicitly freed with `delete`:

```cpp
int* n = new int(5);
cout << *n << '\n';
delete n;

int size;
cin >> size;
int* arr = new int[size];   // an array sized from input
for (int i = 0; i < size; i++) arr[i] = i * i;
delete[] arr;               // for arrays — delete[]
```

> [!WARNING]
> Forgot `delete` — a **memory leak**. Called `delete` twice or used memory after `delete` — undefined behavior. That's why modern C++ almost never uses bare `new`/`delete`.

## Smart pointers

From `<memory>` — pointers that free memory themselves:

```cpp
#include <memory>

unique_ptr<int> a = make_unique<int>(42);
cout << *a << '\n';           // 42
// no delete needed — memory is freed when the scope ends

shared_ptr<string> s1 = make_shared<string>("shared");
shared_ptr<string> s2 = s1;   // both own it, count = 2
```

- `unique_ptr` — a single owner, can't be copied (only `move`d);
- `shared_ptr` — several owners, memory is freed when the last one goes away.

Instead of a dynamic array, `vector` is almost always better.
