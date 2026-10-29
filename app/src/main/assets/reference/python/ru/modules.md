# Модули и пакеты

**Модуль** — любой файл `.py`. Код из него можно подключить в другой файл через `import`.

## Импорт

```python
import math
print(math.sqrt(16))          # 4.0

from math import pi, floor
print(floor(pi))              # 3

import datetime as dt         # псевдоним
print(dt.date.today())

from random import randint
print(randint(1, 10))         # случайное число от 1 до 10
```

> [!WARNING]
> Избегайте `from module import *`: непонятно, откуда взялись имена, и они могут перезаписать ваши переменные.

## Свой модуль

Файл `grades.py`:

```python
PASS_MARK = 4

def average(marks):
    return sum(marks) / len(marks)

def is_passed(mark):
    return mark >= PASS_MARK
```

Файл `main.py` в той же папке:

```python
import grades

marks = [8, 3, 9]
print(grades.average(marks))                       # 6.666...
print([m for m in marks if grades.is_passed(m)])   # [8, 9]
```

> [!WARNING]
> Не называйте свои файлы как стандартные модули: `random.py`, `math.py`, `json.py`. Тогда `import random` загрузит ваш файл, а не библиотеку.

## if __name__ == "__main__"

Код модуля выполняется при импорте. Чтобы часть кода работала только при прямом запуске файла:

```python
def average(marks):
    return sum(marks) / len(marks)

if __name__ == "__main__":
    print(average([8, 9, 7]))   # не выполнится при import
```

## Пакеты

**Пакет** — папка с модулями:

```text
project/
├── main.py
└── school/
    ├── __init__.py
    ├── grades.py
    └── schedule.py
```

```python
from school import grades
from school.schedule import today_lessons
```

Файл `__init__.py` может быть пустым.

## Полезные стандартные модули

| Модуль | Для чего |
|---|---|
| `math` | математика: `sqrt`, `ceil`, `pi` |
| `random` | случайные числа: `randint`, `choice`, `shuffle` |
| `datetime` | даты и время |
| `os`, `pathlib` | файлы, папки, пути |
| `json`, `csv` | форматы данных |
| `collections` | `Counter`, `defaultdict`, `deque` |
| `itertools` | комбинации, перестановки, группировки |
| `statistics` | `mean`, `median`, `mode` |
| `time` | паузы `sleep`, замер времени |

```python
from datetime import date, timedelta

deadline = date(2026, 11, 15)
left = deadline - date.today()
print(f"Осталось дней: {left.days}")
print(deadline + timedelta(days=7))   # 2026-11-22
```

## Сторонние пакеты: pip

Библиотеки из интернета ставятся менеджером `pip`:

```bash
pip install requests
pip list
pip freeze > requirements.txt
pip install -r requirements.txt
```

## Виртуальное окружение

Чтобы пакеты разных проектов не мешали друг другу, для проекта создают окружение:

```bash
python -m venv .venv
.venv\Scripts\activate        # Windows
source .venv/bin/activate     # Linux, macOS
pip install requests
deactivate
```

Папку `.venv` не добавляют в git — вместо неё хранят `requirements.txt`.

## Проверь себя

> [!QUIZ]
> Почему нельзя называть свой файл `random.py`?
> ---
> `import random` загрузит ваш файл вместо стандартного модуля.

> [!QUIZ]
> Зачем проекту виртуальное окружение?
> ---
> Чтобы пакеты и их версии разных проектов не конфликтовали друг с другом.
