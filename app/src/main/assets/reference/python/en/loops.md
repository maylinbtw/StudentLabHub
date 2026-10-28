# Loops

Python has two loops: `for` walks through the items of a collection, `while` repeats code while a condition holds.

## for and range

A Python `for` always iterates over something. To count, use `range`:

```python
for i in range(5):
    print(i, end=" ")      # 0 1 2 3 4
print()

for i in range(1, 6):
    print(i, end=" ")      # 1 2 3 4 5
print()

for i in range(10, 0, -3):
    print(i, end=" ")      # 10 7 4 1
```

| Call | Numbers |
|---|---|
| `range(5)` | 0, 1, 2, 3, 4 |
| `range(2, 6)` | 2, 3, 4, 5 — the end is excluded |
| `range(0, 10, 2)` | 0, 2, 4, 6, 8 |
| `range(5, 0, -1)` | 5, 4, 3, 2, 1 |

## Iterating collections

```python
marks = [8, 6, 9, 7]
for m in marks:
    print(m)

for ch in "Lab5":
    print(ch)

grades = {"Lab1": 9, "Lab2": 7}
for lab, mark in grades.items():
    print(f"{lab}: {mark}")
```

## enumerate and zip

```python
labs = ["Lab1", "Lab2", "Lab3"]
for number, lab in enumerate(labs, start=1):
    print(number, lab)

names = ["Anna", "Ivan"]
scores = [9, 7]
for name, score in zip(names, scores):
    print(f"{name} — {score}")
```

```output
1 Lab1
2 Lab2
3 Lab3
Anna — 9
Ivan — 7
```

## while

```python
n, digits = 1234, 0
while n > 0:
    n //= 10
    digits += 1
print(digits)    # 4
```

Python has no `do…while`. An infinite loop with `break` replaces it:

```python
while True:
    answer = input("Continue? (yes/no) ")
    if answer in ("yes", "no"):
        break
```

## break, continue and else

```python
for i in range(1, 21):
    if i % 3 != 0:
        continue
    if i > 12:
        break
    print(i, end=" ")    # 3 6 9 12
```

Python loops have an unusual `else` branch: it runs when the loop ends **without** `break`:

```python
for lab in labs:
    if lab == "Lab5":
        print("Found")
        break
else:
    print("Lab5 is not in the list")
```

## List comprehensions

The common "build a list from another one" loop fits on one line:

```python
squares = [x ** 2 for x in range(1, 6)]          # [1, 4, 9, 16, 25]
good = [m for m in marks if m >= 8]              # [8, 9]
names_upper = [n.upper() for n in names]
```

## Handy functions

```python
sum(marks)                 # 30
min(marks), max(marks)     # (6, 9)
sorted(marks)              # [6, 7, 8, 9]
reversed(marks)            # iterate in reverse order
any(m < 4 for m in marks)  # any retakes? — False
all(m >= 4 for m in marks) # all passed? — True
```

> [!WARNING]
> Don't modify a list while iterating over it with `for`: removing items makes the loop skip elements. Iterate over a copy `for x in items[:]` or build a new list.
