# Variables and data types

In Python a variable's type is not declared: it is determined by the value stored in it. The same variable can hold a number first and a string later — this is **dynamic typing**.

## Variables

```python
labs = 12               # int
avg = 7.5               # float
name = "Anna"           # str
passed = True           # bool
nothing = None          # "no value"

a, b = 1, 2             # several variables at once
a, b = b, a             # swap without a temporary variable
x = y = 0               # one value for two variables
```

Names use `snake_case`: `lab_count`, `average_mark`. Constants are uppercase: `MAX_LABS = 20` (a convention — Python does not stop you from changing them).

## Main types

| Type | Example | Description |
|---|---|---|
| `int` | `42`, `-7`, `1_000_000` | integers of unlimited size |
| `float` | `3.14`, `1e-3` | fractions (double) |
| `str` | `"text"`, `'text'` | immutable strings |
| `bool` | `True`, `False` | boolean (capitalized) |
| `NoneType` | `None` | absence of a value |
| `list` | `[1, 2, 3]` | mutable list |
| `tuple` | `(1, 2)` | immutable tuple |
| `dict` | `{"Lab1": 9}` | "key → value" dictionary |
| `set` | `{1, 2, 3}` | set without duplicates |

```python
print(type(avg))               # <class 'float'>
print(isinstance(labs, int))   # True
```

> Python integers never overflow: `2 ** 100` simply evaluates to a huge exact number.

## Strings

```python
name = "Anna"
print(len(name))           # 4 — length in characters
print(name[0])             # A
print(name[-1])            # a — the last character
print(name.upper())        # ANNA
print(name * 2)            # AnnaAnna
print("An" in name)        # True
```

## Type conversion

```python
n = int("42")              # string → integer
x = float("3.5")           # string → float
s = str(42)                # number → string
i = int(7.9)               # 7 — the fraction is dropped
b = bool(0)                # False; empty strings and lists are False too
```

> [!WARNING]
> `int("3.5")` and `int("abc")` raise `ValueError`. `"5" + 5` raises `TypeError`: Python never converts strings to numbers on its own.

## References to objects

A Python variable is a name bound to an object. Assignment copies the reference:

```python
a = [1, 2, 3]
b = a
b.append(4)
print(a)                   # [1, 2, 3, 4] — it is the same list

c = a.copy()               # an independent copy
```

With immutable types (`int`, `str`, `tuple`) you never notice: any "change" creates a new object.
