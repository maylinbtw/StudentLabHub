# Словари

**Словарь** (`dict`) хранит пары **ключ → значение**. Значение находится по ключу мгновенно. Ключи уникальны и должны быть неизменяемыми (строки, числа, кортежи).

## Создание и доступ

```python
marks = {"Математика": 9, "Физика": 7}
empty = {}
info = dict(name="Анна", group="П-21")

print(marks["Физика"])        # 7
marks["Химия"] = 8            # добавить
marks["Физика"] = 8           # изменить
print(len(marks))             # 3
```

> [!WARNING]
> Обращение к несуществующему ключу `marks["Биология"]` вызывает `KeyError`. Используйте `get` или проверку `in`.

```python
print(marks.get("Биология"))        # None
print(marks.get("Биология", 0))     # 0 — значение по умолчанию
print("Химия" in marks)             # True — проверяются ключи
```

## Удаление

```python
del marks["Химия"]
value = marks.pop("Физика")          # удалить и вернуть: 8
value = marks.pop("Биология", None)  # без ошибки, если ключа нет
marks.clear()                        # очистить
```

## Перебор

С Python 3.7 словарь помнит порядок добавления.

```python
marks = {"Математика": 9, "Физика": 7, "Химия": 8}

for subject in marks:                    # ключи
    print(subject)

for subject, mark in marks.items():      # пары
    print(f"{subject}: {mark}")

print(list(marks.values()))              # [9, 7, 8]
print(sum(marks.values()) / len(marks))  # 8.0
```

## Подсчёт частоты

```python
text = "to be or not to be"
counts = {}
for word in text.split():
    counts[word] = counts.get(word, 0) + 1
print(counts)   # {'to': 2, 'be': 2, 'or': 1, 'not': 1}
```

То же через `Counter`:

```python
from collections import Counter

counts = Counter(text.split())
print(counts.most_common(2))   # [('to', 2), ('be', 2)]
```

## Группировка: defaultdict

```python
from collections import defaultdict

students = [("Анна", "П-21"), ("Олег", "П-22"), ("Ира", "П-21")]
by_group = defaultdict(list)
for name, group in students:
    by_group[group].append(name)

print(dict(by_group))   # {'П-21': ['Анна', 'Ира'], 'П-22': ['Олег']}
```

`defaultdict(list)` сам создаёт пустой список для нового ключа.

## Генераторы словарей

```python
squares = {x: x * x for x in range(1, 5)}       # {1: 1, 2: 4, 3: 9, 4: 16}
passed = {s: m for s, m in marks.items() if m >= 8}
```

## Сортировка по значению

```python
marks = {"Математика": 9, "Физика": 7, "Химия": 8}
for subject, mark in sorted(marks.items(), key=lambda p: p[1], reverse=True):
    print(subject, mark)
```

```output
Математика 9
Химия 8
Физика 7
```

## Вложенные словари

Так обычно выглядят данные из JSON:

```python
student = {
    "name": "Анна",
    "marks": {"Математика": 9, "Физика": 7},
    "labs": [1, 2, 3]
}
print(student["marks"]["Физика"])   # 7
```

## Объединение

```python
a = {"x": 1, "y": 2}
b = {"y": 20, "z": 3}
merged = a | b           # Python 3.9+: {'x': 1, 'y': 20, 'z': 3}
a.update(b)              # изменить a на месте
```

## Проверь себя

> [!QUIZ]
> Как получить значение по ключу без ошибки, если ключа нет?
> ---
> `d.get(key)` или `d.get(key, значение_по_умолчанию)`.

> [!QUIZ]
> Что проверяет `"x" in d` для словаря?
> ---
> Есть ли такой ключ. Значения проверяет `"x" in d.values()`.
