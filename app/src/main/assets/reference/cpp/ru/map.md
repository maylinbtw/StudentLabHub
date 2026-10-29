# map, set и другие контейнеры

## map — словарь

`std::map` из `<map>` хранит пары **ключ → значение**, отсортированные по ключу. Ключи уникальны.

```cpp
#include <map>

map<string, int> marks;
marks["Математика"] = 9;
marks["Физика"] = 7;
marks.insert({"Химия", 8});

cout << marks["Физика"] << '\n';   // 7
cout << marks.size() << '\n';      // 3
```

> [!WARNING]
> Обращение `marks["Биология"]` к несуществующему ключу **создаёт** его со значением по умолчанию (0). Чтобы только проверить наличие, используйте `count`, `find` или `contains` (C++20).

```cpp
if (marks.count("Биология") == 0)
    cout << "Нет оценки\n";

auto it = marks.find("Химия");
if (it != marks.end())
    cout << it->first << ": " << it->second << '\n';

marks.erase("Физика");
```

## Перебор map

Элементы идут по возрастанию ключа. Каждый элемент — пара с полями `first` (ключ) и `second` (значение):

```cpp
for (const auto& [subject, mark] : marks)   // C++17
    cout << subject << " — " << mark << '\n';
```

## Подсчёт частоты

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

## set — множество

`std::set` из `<set>` хранит только уникальные значения в отсортированном порядке:

```cpp
#include <set>

set<int> s = {5, 1, 5, 3, 1};
s.insert(4);

for (int x : s) cout << x << ' ';   // 1 3 4 5
cout << '\n' << s.count(3);         // 1 — есть
```

## unordered_map и unordered_set

Из `<unordered_map>` и `<unordered_set>`: то же самое, но без сортировки. Поиск в среднем быстрее, а порядок перебора не определён.

## Какой контейнер выбрать

| Задача | Контейнер |
|---|---|
| список элементов, доступ по индексу | `vector` |
| ключ → значение, нужен порядок по ключу | `map` |
| ключ → значение, порядок не важен | `unordered_map` |
| уникальные значения | `set` / `unordered_set` |
| очередь FIFO | `queue` из `<queue>` |
| стек LIFO | `stack` из `<stack>` |
| вставка с обоих концов | `deque` из `<deque>` |

```cpp
#include <queue>
#include <stack>

queue<string> q;
q.push("Анна"); q.push("Олег");
cout << q.front() << '\n';   // Анна
q.pop();

stack<int> st;
st.push(1); st.push(2);
cout << st.top() << '\n';    // 2
```

## Проверь себя

> [!QUIZ]
> Что делает `m["key"]`, если ключа в `map` нет?
> ---
> Создаёт его со значением по умолчанию. Для проверки используйте `count`, `find` или `contains`.

> [!QUIZ]
> В каком порядке перебирается `std::map`?
> ---
> По возрастанию ключа. У `unordered_map` порядок не определён.
