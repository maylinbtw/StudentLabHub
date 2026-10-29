# Tuples and sets

## Tuple

A **tuple** is like a list but **immutable**: after creation its elements can't be replaced, added or removed.

```python
point = (3, 4)
student = ("Anna", "P-21", 8.4)
single = (5,)          # a one-element tuple — the comma is required
empty = ()

print(student[0])      # Anna
print(len(student))    # 3
# student[0] = "Ira"   — TypeError: tuples are immutable
```

## Unpacking

```python
name, group, avg = student
print(name, avg)       # Anna 8.4

a, b = 1, 2
a, b = b, a            # swap values
print(a, b)            # 2 1

first, *rest = [1, 2, 3, 4]
print(first, rest)     # 1 [2, 3, 4]
```

## When to use a tuple

- a fixed set of related values: coordinates, a "name — mark" record;
- returning several values from a function;
- a dictionary key — a list can't be a key, but a tuple can:

```python
schedule = {("Mon", 1): "Math", ("Mon", 2): "Physics"}
print(schedule[("Mon", 2)])   # Physics
```

## Named tuple

```python
from collections import namedtuple

Student = namedtuple("Student", ["name", "group", "avg"])
s = Student("Oleg", "P-22", 7.5)
print(s.name, s.avg)   # Oleg 7.5
```

## Set

A **set** stores only unique values with no order and checks membership very quickly.

```python
groups = {"P-21", "P-22", "P-21"}
print(groups)            # {'P-21', 'P-22'} — the duplicate is gone
print(len(groups))       # 2

groups.add("P-23")
groups.discard("P-99")   # remove if present (no error)
print("P-22" in groups)  # True

empty = set()            # {} is an empty dict, not a set!
```

Removing duplicates from a list:

```python
marks = [7, 9, 7, 4, 9]
unique = set(marks)          # {9, 4, 7} — order not guaranteed
print(sorted(unique))        # [4, 7, 9]
```

## Set operations

```python
a = {1, 2, 3}
b = {2, 3, 4}

a | b      # {1, 2, 3, 4} — union
a & b      # {2, 3} — intersection
a - b      # {1} — difference
a ^ b      # {1, 4} — symmetric difference
a <= b     # False — is a a subset of b
```

Example: subjects both groups have:

```python
p21 = {"Math", "Programming", "Physics"}
p22 = {"Math", "DB", "Physics"}
print(p21 & p22)   # {'Math', 'Physics'}
```

> [!WARNING]
> Set elements must be immutable: numbers, strings, tuples. You can't put a list into a set — `TypeError: unhashable type: 'list'`.

## Comparing collections

| | List | Tuple | Set | Dict |
|---|---|---|---|---|
| syntax | `[1, 2]` | `(1, 2)` | `{1, 2}` | `{"a": 1}` |
| ordered | yes | yes | no | insertion order |
| mutable | yes | no | yes | yes |
| duplicates | yes | yes | no | keys are unique |
| access | by index | by index | `in` check | by key |

## Check yourself

> [!QUIZ]
> How do you create a one-element tuple?
> ---
> With a comma: `(5,)`. `(5)` is just a number in parentheses.

> [!QUIZ]
> Does `{}` create an empty set or an empty dict?
> ---
> An empty dict. An empty set is `set()`.
