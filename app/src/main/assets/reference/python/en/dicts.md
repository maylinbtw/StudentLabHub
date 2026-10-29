# Dictionaries

A **dictionary** (`dict`) stores **key → value** pairs. A value is found by its key instantly. Keys are unique and must be immutable (strings, numbers, tuples).

## Creating and accessing

```python
marks = {"Math": 9, "Physics": 7}
empty = {}
info = dict(name="Anna", group="P-21")

print(marks["Physics"])       # 7
marks["Chemistry"] = 8        # add
marks["Physics"] = 8          # change
print(len(marks))             # 3
```

> [!WARNING]
> Accessing a missing key `marks["Biology"]` raises `KeyError`. Use `get` or an `in` check.

```python
print(marks.get("Biology"))         # None
print(marks.get("Biology", 0))      # 0 — a default value
print("Chemistry" in marks)         # True — keys are checked
```

## Removing

```python
del marks["Chemistry"]
value = marks.pop("Physics")         # remove and return: 8
value = marks.pop("Biology", None)   # no error if the key is missing
marks.clear()                        # empty it
```

## Iterating

Since Python 3.7 a dict remembers insertion order.

```python
marks = {"Math": 9, "Physics": 7, "Chemistry": 8}

for subject in marks:                    # keys
    print(subject)

for subject, mark in marks.items():      # pairs
    print(f"{subject}: {mark}")

print(list(marks.values()))              # [9, 7, 8]
print(sum(marks.values()) / len(marks))  # 8.0
```

## Counting words

```python
text = "to be or not to be"
counts = {}
for word in text.split():
    counts[word] = counts.get(word, 0) + 1
print(counts)   # {'to': 2, 'be': 2, 'or': 1, 'not': 1}
```

The same with `Counter`:

```python
from collections import Counter

counts = Counter(text.split())
print(counts.most_common(2))   # [('to', 2), ('be', 2)]
```

## Grouping: defaultdict

```python
from collections import defaultdict

students = [("Anna", "P-21"), ("Oleg", "P-22"), ("Ira", "P-21")]
by_group = defaultdict(list)
for name, group in students:
    by_group[group].append(name)

print(dict(by_group))   # {'P-21': ['Anna', 'Ira'], 'P-22': ['Oleg']}
```

`defaultdict(list)` creates an empty list for a new key by itself.

## Dict comprehensions

```python
squares = {x: x * x for x in range(1, 5)}       # {1: 1, 2: 4, 3: 9, 4: 16}
passed = {s: m for s, m in marks.items() if m >= 8}
```

## Sorting by value

```python
marks = {"Math": 9, "Physics": 7, "Chemistry": 8}
for subject, mark in sorted(marks.items(), key=lambda p: p[1], reverse=True):
    print(subject, mark)
```

```output
Math 9
Chemistry 8
Physics 7
```

## Nested dictionaries

This is what JSON data usually looks like:

```python
student = {
    "name": "Anna",
    "marks": {"Math": 9, "Physics": 7},
    "labs": [1, 2, 3]
}
print(student["marks"]["Physics"])   # 7
```

## Merging

```python
a = {"x": 1, "y": 2}
b = {"y": 20, "z": 3}
merged = a | b           # Python 3.9+: {'x': 1, 'y': 20, 'z': 3}
a.update(b)              # change a in place
```

## Check yourself

> [!QUIZ]
> How do you get a value by key without an error if it's missing?
> ---
> `d.get(key)` or `d.get(key, default)`.

> [!QUIZ]
> What does `"x" in d` check for a dict?
> ---
> Whether the key exists. `"x" in d.values()` checks the values.
