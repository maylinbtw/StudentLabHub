# vector

`std::vector` из `<vector>` — динамический массив: элементы лежат подряд, размер меняется по мере добавления. Это основной контейнер в C++.

## Создание

```cpp
#include <vector>

vector<int> a;                      // пустой
vector<int> b = {7, 9, 4};          // со значениями
vector<int> c(5);                   // 5 нулей
vector<string> d(3, "—");           // 3 строки "—"
```

## Добавление и удаление

```cpp
vector<int> marks = {7, 9};

marks.push_back(4);                 // в конец: 7 9 4
marks.insert(marks.begin(), 10);    // в начало: 10 7 9 4
marks.pop_back();                   // убрать последний: 10 7 9
marks.erase(marks.begin() + 1);     // убрать второй: 10 9
marks.clear();                      // пустой
```

`begin()` — **итератор** на первый элемент, `begin() + i` — на элемент с индексом `i`.

## Доступ

```cpp
vector<int> v = {7, 9, 4};

v[0];          // 7 — без проверки границ
v.at(1);       // 9 — с проверкой, иначе исключение out_of_range
v.front();     // 7
v.back();      // 4
v.size();      // 3
v.empty();     // false
```

> [!WARNING]
> `v.size()` возвращает беззнаковый `size_t`. Цикл `for (int i = 0; i < v.size() - 1; i++)` на пустом векторе уйдёт в огромное число итераций: `0 - 1` для беззнакового — это максимум.

## Перебор

```cpp
for (size_t i = 0; i < v.size(); i++)
    cout << v[i] << ' ';

for (int x : v)           // копия каждого элемента
    cout << x << ' ';

for (int& x : v)          // ссылка — можно менять
    x *= 2;

for (const auto& s : names)   // для строк и объектов — без копий
    cout << s << '\n';
```

## Вектор объектов

```cpp
struct Lab
{
    string title;
    int mark;
};

vector<Lab> labs;
labs.push_back({"Массивы", 8});
labs.emplace_back(Lab{"Строки", 9});

for (const auto& lab : labs)
    cout << lab.title << ": " << lab.mark << '\n';
```

## Двумерный вектор

```cpp
int rows = 3, cols = 4;
vector<vector<int>> grid(rows, vector<int>(cols, 0));

grid[1][2] = 5;
cout << grid.size() << "x" << grid[0].size(); // 3x4
```

## size и capacity

| Метод | Значение |
|---|---|
| `size()` | сколько элементов сейчас |
| `capacity()` | под сколько выделена память |
| `reserve(n)` | заранее выделить память под n элементов |
| `resize(n)` | изменить количество элементов |

Если заранее известно, сколько будет элементов, `reserve` избавит от лишних перевыделений памяти.

> [!NOTE]
> После `push_back` вектор может переехать в другую область памяти. Указатели, ссылки и итераторы на старые элементы при этом становятся недействительными.
