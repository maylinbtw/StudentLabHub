# STL algorithms

The `<algorithm>` header contains ready-made functions for sorting, searching and processing containers. They work on a range given by two iterators: `begin()` and `end()`.

## Sorting

```cpp
#include <algorithm>
#include <vector>

vector<int> v = {7, 2, 9, 4};

sort(v.begin(), v.end());                     // 2 4 7 9
sort(v.begin(), v.end(), greater<int>());     // 9 7 4 2
reverse(v.begin(), v.end());                  // 2 4 7 9
```

Sorting objects by a custom rule — with a lambda:

```cpp
struct Student { string name; double avg; };

vector<Student> group = {{"Anna", 8.4}, {"Oleg", 6.1}, {"Ira", 9.2}};

sort(group.begin(), group.end(),
     [](const Student& a, const Student& b) { return a.avg > b.avg; });

for (const auto& s : group)
    cout << s.name << ' ' << s.avg << '\n';
```

```output
Ira 9.2
Anna 8.4
Oleg 6.1
```

## Lambda expressions

`[capture](parameters) { body }` is an unnamed function:

```cpp
auto square = [](int x) { return x * x; };
cout << square(5);   // 25

int limit = 6;
auto passed = [limit](int m) { return m >= limit; };  // capture by value
auto addTo = [&limit](int d) { limit += d; };         // capture by reference
```

## Search and count

```cpp
vector<int> v = {7, 2, 9, 4, 9};

auto it = find(v.begin(), v.end(), 9);
if (it != v.end())
    cout << "Index: " << (it - v.begin()) << '\n';    // 2

int nines = count(v.begin(), v.end(), 9);             // 2
int big = count_if(v.begin(), v.end(), [](int x) { return x > 5; }); // 3

auto mx = max_element(v.begin(), v.end());
cout << *mx;                                           // 9
```

`find` and `max_element` return an iterator; if nothing is found — `end()`.

## Checks

```cpp
bool allPositive = all_of(v.begin(), v.end(), [](int x) { return x > 0; }); // true
bool anyBig = any_of(v.begin(), v.end(), [](int x) { return x > 8; });      // true
```

## Sum

`accumulate` lives in `<numeric>`:

```cpp
#include <numeric>

int sum = accumulate(v.begin(), v.end(), 0);   // 31
double avg = static_cast<double>(sum) / v.size();
```

> [!WARNING]
> The result type of `accumulate` comes from the initial value. `accumulate(d.begin(), d.end(), 0)` on a `double` vector drops the fractions — write `0.0`.

## Transforming and removing

```cpp
vector<int> v = {1, 2, 3, 4, 5, 6};

transform(v.begin(), v.end(), v.begin(), [](int x) { return x * 10; });
// 10 20 30 40 50 60

v.erase(remove_if(v.begin(), v.end(), [](int x) { return x > 30; }), v.end());
// 10 20 30
```

`remove_if` only moves matching elements to the end and returns the new boundary — `erase` actually removes them. C++20 has a shorter form: `erase_if(v, condition)`.

## Unique values

```cpp
vector<int> v = {3, 1, 3, 2, 1};
sort(v.begin(), v.end());                        // 1 1 2 3 3
v.erase(unique(v.begin(), v.end()), v.end());    // 1 2 3
```

`unique` removes only **adjacent** duplicates, so sort first.

## Binary search

In a **sorted** vector, `binary_search` and `lower_bound` work in logarithmic time:

```cpp
vector<int> v = {2, 4, 7, 9};
bool has = binary_search(v.begin(), v.end(), 7);   // true
auto pos = lower_bound(v.begin(), v.end(), 5);     // at 7 — the first ≥ 5
```
