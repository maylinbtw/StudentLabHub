# Functions

A **function** is a named block of code that can be called many times. It is declared with `def`.

## Declaring and calling

```python
def average(a, b, c):
    return (a + b + c) / 3

def print_line(width):
    print("-" * width)

print_line(20)
print("Average:", average(8, 9, 7))
print_line(20)
```

```output
--------------------
Average: 8.0
--------------------
```

- the function body is indented;
- `return` returns a value and ends the function;
- a function without `return` returns `None`;
- a function must be declared before it is called.

## Several results

A function can return several values — it's actually a tuple, which is easy to unpack:

```python
def min_max(items):
    return min(items), max(items)

lo, hi = min_max([4, 9, 2, 7])
print(lo, hi)   # 2 9
```

## Default parameters and keyword arguments

```python
def greet(name, greeting="Hello"):
    print(f"{greeting}, {name}!")

greet("Anna")                      # Hello, Anna!
greet("Oleg", "Good morning")      # Good morning, Oleg!
greet(greeting="Hi", name="Ira")   # by name — order doesn't matter
```

> [!WARNING]
> Don't use mutable default values: `def add(x, items=[])`. The list is created **once** when the function is declared and keeps values between calls. The right way: `items=None`, and inside `if items is None: items = []`.

## *args and **kwargs

```python
def total(*numbers):          # any number of positional arguments → a tuple
    return sum(numbers)

print(total(1, 2, 3))         # 6

def show(**info):             # any number of keyword arguments → a dict
    for key, value in info.items():
        print(f"{key}: {value}")

show(name="Anna", group="P-21")
```

## Type hints

Types can be specified for readability and editor hints. They don't affect execution:

```python
def grade(mark: int) -> str:
    if mark >= 9:
        return "excellent"
    if mark >= 4:
        return "pass"
    return "fail"
```

## Scope

Variables created inside a function are **local**. To change a global variable you need `global`:

```python
counter = 0

def increment():
    global counter
    counter += 1

increment()
print(counter)  # 1
```

Better not to overuse `global`: pass values as parameters and return the result.

## lambda

A short unnamed function with a single expression:

```python
square = lambda x: x * x
print(square(5))   # 25

students = [("Anna", 8.4), ("Oleg", 6.1), ("Ira", 9.2)]
students.sort(key=lambda s: s[1], reverse=True)
print(students[0][0])   # Ira
```

## Docstring

The first line in the function body in triple quotes is a **docstring**. `help()` and editor hints show it:

```python
def average(marks):
    """Returns the average of a list of marks."""
    return sum(marks) / len(marks)
```

## Recursion

```python
def factorial(n):
    if n <= 1:
        return 1
    return n * factorial(n - 1)

print(factorial(10))   # 3628800
```

> [!NOTE]
> By default recursion depth is limited to about 1000 calls — beyond that you get `RecursionError`. For deep tasks, a loop is better.
