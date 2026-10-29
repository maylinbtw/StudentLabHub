# Classes and objects

A **class** describes a new type: data (**attributes**) and behavior (**methods**). An **object** is an instance of a class.

## Declaring a class

```python
class Student:
    def __init__(self, name, course):
        self.name = name
        self.course = course

    def introduce(self):
        print(f"I'm {self.name}, year {self.course}")

    def next_course(self):
        self.course += 1


anna = Student("Anna", 2)
anna.introduce()
anna.next_course()
anna.introduce()
```

```output
I'm Anna, year 2
I'm Anna, year 3
```

- `__init__` is the constructor, called when an object is created;
- `self` refers to the current object, the first parameter of every method;
- attributes are created by assigning `self.name = ...`.

## Class attributes

An attribute declared in the class body is shared by all objects:

```python
class Student:
    count = 0

    def __init__(self, name):
        self.name = name
        Student.count += 1

Student("Anna")
Student("Oleg")
print(Student.count)   # 2
```

## String representation

```python
class Student:
    def __init__(self, name, avg):
        self.name = name
        self.avg = avg

    def __str__(self):
        return f"{self.name} ({self.avg})"

print(Student("Ira", 9.2))   # Ira (9.2)
```

Without `__str__` you get something like `<__main__.Student object at 0x...>`.

## Properties and "private" attributes

Python has no `private`. By convention an attribute starting with `_` is internal. To validate values, use `@property`:

```python
class Lab:
    def __init__(self, title):
        self.title = title
        self._mark = 0

    @property
    def mark(self):
        return self._mark

    @mark.setter
    def mark(self, value):
        if not 0 <= value <= 10:
            raise ValueError("Mark must be 0 to 10")
        self._mark = value

lab = Lab("Arrays")
lab.mark = 9        # the setter is called
print(lab.mark)     # 9
```

## Inheritance

```python
class Person:
    def __init__(self, name):
        self.name = name

    def info(self):
        return f"Person {self.name}"


class Student(Person):
    def __init__(self, name, group):
        super().__init__(name)
        self.group = group

    def info(self):
        return super().info() + f", group {self.group}"


people = [Person("Oleg"), Student("Anna", "P-21")]
for p in people:
    print(p.info())
```

```output
Person Oleg
Person Anna, group P-21
```

`isinstance(obj, Person)` checks the type, taking inheritance into account.

## dataclass

For data classes, the constructor, `__repr__` and comparison are generated automatically:

```python
from dataclasses import dataclass

@dataclass
class Mark:
    subject: str
    value: int = 0

m1 = Mark("Physics", 8)
m2 = Mark("Physics", 8)
print(m1)          # Mark(subject='Physics', value=8)
print(m1 == m2)    # True
```

## Special methods

| Method | When it is called |
|---|---|
| `__init__` | object creation |
| `__str__` | `print(obj)`, `str(obj)` |
| `__repr__` | display in the console and in lists |
| `__eq__` | `a == b` |
| `__lt__` | `a < b`, needed for `sorted` |
| `__len__` | `len(obj)` |

> [!WARNING]
> A forgotten `self` is a common mistake: `def introduce():` raises `TypeError: introduce() takes 0 positional arguments but 1 was given`.

## Check yourself

> [!QUIZ]
> What is `self`?
> ---
> A reference to the current object — the first parameter of every method.

> [!QUIZ]
> Which method defines how an object looks in `print`?
> ---
> `__str__`.
