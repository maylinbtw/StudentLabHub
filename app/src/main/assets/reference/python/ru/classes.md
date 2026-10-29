# Классы и объекты

**Класс** описывает новый тип: данные (**атрибуты**) и поведение (**методы**). **Объект** — экземпляр класса.

## Объявление класса

```python
class Student:
    def __init__(self, name, course):
        self.name = name
        self.course = course

    def introduce(self):
        print(f"Я {self.name}, {self.course} курс")

    def next_course(self):
        self.course += 1


anna = Student("Анна", 2)
anna.introduce()
anna.next_course()
anna.introduce()
```

```output
Я Анна, 2 курс
Я Анна, 3 курс
```

- `__init__` — конструктор, вызывается при создании объекта;
- `self` — ссылка на текущий объект, первый параметр каждого метода;
- атрибуты создаются присваиванием `self.имя = ...`.

## Атрибуты класса

Атрибут, объявленный в теле класса, общий для всех объектов:

```python
class Student:
    count = 0

    def __init__(self, name):
        self.name = name
        Student.count += 1

Student("Анна")
Student("Олег")
print(Student.count)   # 2
```

## Строковое представление

```python
class Student:
    def __init__(self, name, avg):
        self.name = name
        self.avg = avg

    def __str__(self):
        return f"{self.name} ({self.avg})"

print(Student("Ира", 9.2))   # Ира (9.2)
```

Без `__str__` выведется что-то вроде `<__main__.Student object at 0x...>`.

## Свойства и «закрытые» атрибуты

В Python нет `private`. По соглашению атрибут с `_` в начале — внутренний. Для проверки значений используют `@property`:

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
            raise ValueError("Оценка от 0 до 10")
        self._mark = value

lab = Lab("Массивы")
lab.mark = 9        # вызывается setter
print(lab.mark)     # 9
```

## Наследование

```python
class Person:
    def __init__(self, name):
        self.name = name

    def info(self):
        return f"Человек {self.name}"


class Student(Person):
    def __init__(self, name, group):
        super().__init__(name)
        self.group = group

    def info(self):
        return super().info() + f", группа {self.group}"


people = [Person("Олег"), Student("Анна", "П-21")]
for p in people:
    print(p.info())
```

```output
Человек Олег
Человек Анна, группа П-21
```

`isinstance(obj, Person)` проверяет тип с учётом наследования.

## dataclass

Для классов-данных конструктор, `__repr__` и сравнение создаются автоматически:

```python
from dataclasses import dataclass

@dataclass
class Mark:
    subject: str
    value: int = 0

m1 = Mark("Физика", 8)
m2 = Mark("Физика", 8)
print(m1)          # Mark(subject='Физика', value=8)
print(m1 == m2)    # True
```

## Специальные методы

| Метод | Когда вызывается |
|---|---|
| `__init__` | создание объекта |
| `__str__` | `print(obj)`, `str(obj)` |
| `__repr__` | отображение в консоли и списках |
| `__eq__` | `a == b` |
| `__lt__` | `a < b`, нужен для `sorted` |
| `__len__` | `len(obj)` |

> [!WARNING]
> Забытый `self` — частая ошибка: `def introduce():` вызовет `TypeError: introduce() takes 0 positional arguments but 1 was given`.
