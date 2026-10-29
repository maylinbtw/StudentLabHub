# Modules and packages

A **module** is any `.py` file. Its code can be used in another file with `import`.

## Importing

```python
import math
print(math.sqrt(16))          # 4.0

from math import pi, floor
print(floor(pi))              # 3

import datetime as dt         # an alias
print(dt.date.today())

from random import randint
print(randint(1, 10))         # a random number from 1 to 10
```

> [!WARNING]
> Avoid `from module import *`: it's unclear where names came from, and they may overwrite your variables.

## Your own module

File `grades.py`:

```python
PASS_MARK = 4

def average(marks):
    return sum(marks) / len(marks)

def is_passed(mark):
    return mark >= PASS_MARK
```

File `main.py` in the same folder:

```python
import grades

marks = [8, 3, 9]
print(grades.average(marks))                       # 6.666...
print([m for m in marks if grades.is_passed(m)])   # [8, 9]
```

> [!WARNING]
> Don't name your files like standard modules: `random.py`, `math.py`, `json.py`. Then `import random` loads your file instead of the library.

## if __name__ == "__main__"

A module's code runs when it is imported. To make part of the code run only when the file is run directly:

```python
def average(marks):
    return sum(marks) / len(marks)

if __name__ == "__main__":
    print(average([8, 9, 7]))   # won't run on import
```

## Packages

A **package** is a folder with modules:

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

The `__init__.py` file can be empty.

## Useful standard modules

| Module | What for |
|---|---|
| `math` | math: `sqrt`, `ceil`, `pi` |
| `random` | random numbers: `randint`, `choice`, `shuffle` |
| `datetime` | dates and times |
| `os`, `pathlib` | files, folders, paths |
| `json`, `csv` | data formats |
| `collections` | `Counter`, `defaultdict`, `deque` |
| `itertools` | combinations, permutations, grouping |
| `statistics` | `mean`, `median`, `mode` |
| `time` | `sleep` pauses, timing |

```python
from datetime import date, timedelta

deadline = date(2026, 11, 15)
left = deadline - date.today()
print(f"Days left: {left.days}")
print(deadline + timedelta(days=7))   # 2026-11-22
```

## Third-party packages: pip

Libraries from the internet are installed with `pip`:

```bash
pip install requests
pip list
pip freeze > requirements.txt
pip install -r requirements.txt
```

## Virtual environment

To keep packages of different projects apart, create an environment for each project:

```bash
python -m venv .venv
.venv\Scripts\activate        # Windows
source .venv/bin/activate     # Linux, macOS
pip install requests
deactivate
```

The `.venv` folder isn't added to git — `requirements.txt` is kept instead.
