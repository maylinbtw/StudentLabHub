# map, set and other containers

## map — a dictionary

`std::map` from `<map>` stores **key → value** pairs sorted by key. Keys are unique.

```cpp
#include <map>

map<string, int> marks;
marks["Math"] = 9;
marks["Physics"] = 7;
marks.insert({"Chemistry", 8});

cout << marks["Physics"] << '\n';  // 7
cout << marks.size() << '\n';      // 3
```

> [!WARNING]
> Accessing `marks["Biology"]` with a missing key **creates** it with a default value (0). To only check presence, use `count`, `find` or `contains` (C++20).

```cpp
if (marks.count("Biology") == 0)
    cout << "No mark\n";

auto it = marks.find("Chemistry");
if (it != marks.end())
    cout << it->first << ": " << it->second << '\n';

marks.erase("Physics");
```

## Iterating a map

Elements come in ascending key order. Each element is a pair with `first` (key) and `second` (value):

```cpp
for (const auto& [subject, mark] : marks)   // C++17
    cout << subject << " — " << mark << '\n';
```

## Counting words

```cpp
string text = "to be or not to be";
stringstream ss(text);
map<string, int> counts;
string word;

while (ss >> word)
    counts[word]++;

for (const auto& [w, n] : counts)
    cout << w << ": " << n << '\n';
```

```output
be: 2
not: 1
or: 1
to: 2
```

## set — a set

`std::set` from `<set>` stores only unique values in sorted order:

```cpp
#include <set>

set<int> s = {5, 1, 5, 3, 1};
s.insert(4);

for (int x : s) cout << x << ' ';   // 1 3 4 5
cout << '\n' << s.count(3);         // 1 — present
```

## unordered_map and unordered_set

From `<unordered_map>` and `<unordered_set>`: the same, but unsorted. Lookup is faster on average, and iteration order is unspecified.

## Which container to choose

| Task | Container |
|---|---|
| a list of elements, access by index | `vector` |
| key → value, order by key needed | `map` |
| key → value, order doesn't matter | `unordered_map` |
| unique values | `set` / `unordered_set` |
| FIFO queue | `queue` from `<queue>` |
| LIFO stack | `stack` from `<stack>` |
| insert at both ends | `deque` from `<deque>` |

```cpp
#include <queue>
#include <stack>

queue<string> q;
q.push("Anna"); q.push("Oleg");
cout << q.front() << '\n';   // Anna
q.pop();

stack<int> st;
st.push(1); st.push(2);
cout << st.top() << '\n';    // 2
```

## Check yourself

> [!QUIZ]
> What does `m["key"]` do if the key isn't in the `map`?
> ---
> It creates it with a default value. To check, use `count`, `find` or `contains`.

> [!QUIZ]
> In what order is a `std::map` iterated?
> ---
> In ascending key order. `unordered_map` has no defined order.
