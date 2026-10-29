# Алгоритмы STL

Заголовок `<algorithm>` содержит готовые функции для сортировки, поиска и обработки контейнеров. Они работают с диапазоном, заданным двумя итераторами: `begin()` и `end()`.

## Сортировка

```cpp
#include <algorithm>
#include <vector>

vector<int> v = {7, 2, 9, 4};

sort(v.begin(), v.end());                     // 2 4 7 9
sort(v.begin(), v.end(), greater<int>());     // 9 7 4 2
reverse(v.begin(), v.end());                  // 2 4 7 9
```

Сортировка объектов по своему правилу — через лямбду:

```cpp
struct Student { string name; double avg; };

vector<Student> group = {{"Анна", 8.4}, {"Олег", 6.1}, {"Ира", 9.2}};

sort(group.begin(), group.end(),
     [](const Student& a, const Student& b) { return a.avg > b.avg; });

for (const auto& s : group)
    cout << s.name << ' ' << s.avg << '\n';
```

```output
Ира 9.2
Анна 8.4
Олег 6.1
```

## Лямбда-выражения

`[захват](параметры) { тело }` — безымянная функция:

```cpp
auto square = [](int x) { return x * x; };
cout << square(5);   // 25

int limit = 6;
auto passed = [limit](int m) { return m >= limit; };  // захват по значению
auto addTo = [&limit](int d) { limit += d; };         // захват по ссылке
```

## Поиск и подсчёт

```cpp
vector<int> v = {7, 2, 9, 4, 9};

auto it = find(v.begin(), v.end(), 9);
if (it != v.end())
    cout << "Индекс: " << (it - v.begin()) << '\n';   // 2

int nines = count(v.begin(), v.end(), 9);             // 2
int big = count_if(v.begin(), v.end(), [](int x) { return x > 5; }); // 3

auto mx = max_element(v.begin(), v.end());
cout << *mx;                                           // 9
```

`find` и `max_element` возвращают итератор; если ничего не найдено — `end()`.

## Проверки

```cpp
bool allPositive = all_of(v.begin(), v.end(), [](int x) { return x > 0; }); // true
bool anyBig = any_of(v.begin(), v.end(), [](int x) { return x > 8; });      // true
```

## Сумма

`accumulate` находится в `<numeric>`:

```cpp
#include <numeric>

int sum = accumulate(v.begin(), v.end(), 0);   // 31
double avg = static_cast<double>(sum) / v.size();
```

> [!WARNING]
> Тип результата `accumulate` берётся из начального значения. `accumulate(d.begin(), d.end(), 0)` для вектора `double` отбросит дробные части — пишите `0.0`.

## Преобразование и удаление

```cpp
vector<int> v = {1, 2, 3, 4, 5, 6};

transform(v.begin(), v.end(), v.begin(), [](int x) { return x * 10; });
// 10 20 30 40 50 60

v.erase(remove_if(v.begin(), v.end(), [](int x) { return x > 30; }), v.end());
// 10 20 30
```

`remove_if` только сдвигает подходящие элементы в конец и возвращает новую границу — удаляет их `erase`. В C++20 есть короче: `erase_if(v, условие)`.

## Уникальные значения

```cpp
vector<int> v = {3, 1, 3, 2, 1};
sort(v.begin(), v.end());                        // 1 1 2 3 3
v.erase(unique(v.begin(), v.end()), v.end());    // 1 2 3
```

`unique` убирает только **соседние** повторы, поэтому сначала сортируют.

## Бинарный поиск

В **отсортированном** векторе поиск через `binary_search` и `lower_bound` работает за логарифмическое время:

```cpp
vector<int> v = {2, 4, 7, 9};
bool has = binary_search(v.begin(), v.end(), 7);   // true
auto pos = lower_bound(v.begin(), v.end(), 5);     // на 7 — первый ≥ 5
```

## Проверь себя

> [!QUIZ]
> Почему перед `unique` вектор сортируют?
> ---
> `unique` убирает только соседние повторы.

> [!QUIZ]
> Удаляет ли `remove_if` элементы из вектора?
> ---
> Нет, только сдвигает оставшиеся вперёд и возвращает новую границу. Удаляет их `erase`.
