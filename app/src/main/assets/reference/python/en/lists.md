# Lists

A **list** (`list`) is an ordered mutable collection. It can hold values of different types, but usually holds data of one kind.

## Creating and accessing

```python
marks = [7, 9, 4, 8]
empty = []
letters = list("abc")        # ['a', 'b', 'c']
zeros = [0] * 5              # [0, 0, 0, 0, 0]

print(marks[0])              # 7
print(marks[-1])             # 8 — the last one
print(len(marks))            # 4
marks[2] = 10
```

> [!WARNING]
> An index outside the list raises `IndexError`. Valid indexes: from `0` to `len - 1` and from `-len` to `-1`.

## Slices

`list[start:end:step]` — the end is not included:

```python
nums = [0, 1, 2, 3, 4, 5, 6]

nums[1:4]     # [1, 2, 3]
nums[:3]      # [0, 1, 2]
nums[4:]      # [4, 5, 6]
nums[::2]     # [0, 2, 4, 6]
nums[::-1]    # [6, 5, 4, 3, 2, 1, 0] — reversed
nums[:]       # a copy of the list
```

## Modifying

```python
subjects = ["Math", "Physics"]

subjects.append("Chemistry")        # to the end
subjects.insert(0, "History")       # at an index
subjects.extend(["Programming", "DB"])  # several at once

subjects.remove("Physics")          # by value (first occurrence)
last = subjects.pop()               # remove and return the last: "DB"
first = subjects.pop(0)             # by index: "History"
del subjects[0]                     # delete by index

print(subjects)                     # ['Chemistry', 'Programming']
```

## Search and checks

```python
marks = [7, 9, 4, 9]

9 in marks             # True
marks.index(9)         # 1 — first occurrence
marks.count(9)         # 2
min(marks), max(marks) # (4, 9)
sum(marks) / len(marks)# 7.25
```

## Sorting

```python
marks = [7, 9, 4]

marks.sort()                 # changes the list itself: [4, 7, 9]
marks.sort(reverse=True)     # [9, 7, 4]
new = sorted(marks)          # returns a new one, the original is unchanged

names = ["oleg", "Anna", "ira"]
names.sort(key=str.lower)    # case-insensitive
```

## List comprehensions

A short way to build a list from another:

```python
squares = [x * x for x in range(1, 6)]          # [1, 4, 9, 16, 25]
good = [m for m in marks if m >= 8]             # with a condition
upper = [s.upper() for s in ["a", "b"]]         # ['A', 'B']
```

## Copying

```python
a = [1, 2, 3]
b = a            # the same reference
b.append(4)
print(a)         # [1, 2, 3, 4] — a changed too

c = a.copy()     # or a[:] or list(a)
c.append(5)
print(a)         # [1, 2, 3, 4] — a is unchanged
```

## Nested lists

```python
table = [
    [1, 2, 3],
    [4, 5, 6]
]
print(table[1][2])   # 6

for row in table:
    print(*row)
```

> [!WARNING]
> `[[0] * 3] * 2` creates **two references to the same row**: changing one row changes both. The right way: `[[0] * 3 for _ in range(2)]`.
