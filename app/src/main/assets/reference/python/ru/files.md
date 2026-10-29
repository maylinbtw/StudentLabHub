# Работа с файлами

## Чтение и запись

Файл открывают функцией `open` внутри `with` — тогда он закроется автоматически:

```python
with open("note.txt", "w", encoding="utf-8") as f:
    f.write("Сдать лабу 3 до пятницы\n")
    f.write("Взять флешку\n")

with open("note.txt", encoding="utf-8") as f:
    text = f.read()
print(text)
```

> [!WARNING]
> Всегда указывайте `encoding="utf-8"`. Без него в Windows используется кодировка системы, и русский текст превратится в «кракозябры» или вызовет `UnicodeDecodeError`.

## Режимы открытия

| Режим | Значение |
|---|---|
| `"r"` | чтение (по умолчанию), файл должен существовать |
| `"w"` | запись, файл создаётся или **очищается** |
| `"a"` | дописать в конец |
| `"x"` | создать новый, ошибка если уже есть |
| `"b"` | двоичный режим: `"rb"`, `"wb"` |

## Построчное чтение

```python
with open("marks.txt", encoding="utf-8") as f:
    for line in f:
        print(line.strip())      # strip убирает \n в конце

with open("marks.txt", encoding="utf-8") as f:
    lines = f.read().splitlines()   # список строк без \n
```

Перебор `for line in f` читает файл по одной строке — подходит и для очень больших файлов.

## Запись списка

```python
marks = [8, 9, 7]
with open("marks.txt", "w", encoding="utf-8") as f:
    for m in marks:
        f.write(f"{m}\n")

# или так
with open("marks.txt", "w", encoding="utf-8") as f:
    print(*marks, sep="\n", file=f)
```

## CSV

```python
import csv

rows = [["Имя", "Группа", "Балл"], ["Анна", "П-21", 8.4], ["Олег", "П-22", 7.5]]

with open("students.csv", "w", newline="", encoding="utf-8") as f:
    csv.writer(f, delimiter=";").writerows(rows)

with open("students.csv", newline="", encoding="utf-8") as f:
    for row in csv.DictReader(f, delimiter=";"):
        print(row["Имя"], row["Балл"])
```

## JSON

```python
import json

data = {"name": "Анна", "marks": [8, 9, 7]}

with open("data.json", "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

with open("data.json", encoding="utf-8") as f:
    loaded = json.load(f)
print(loaded["marks"])   # [8, 9, 7]
```

`ensure_ascii=False` оставляет кириллицу читаемой, `indent=2` добавляет отступы.

## Пути: pathlib

```python
from pathlib import Path

folder = Path("data") / "2026"
folder.mkdir(parents=True, exist_ok=True)

file = folder / "note.txt"
file.write_text("Привет", encoding="utf-8")
print(file.read_text(encoding="utf-8"))

print(file.name)      # note.txt
print(file.suffix)    # .txt
print(file.exists())  # True

for p in Path("data").rglob("*.txt"):   # все .txt во вложенных папках
    print(p)
```

## Ошибки

| Исключение | Причина |
|---|---|
| `FileNotFoundError` | файла нет |
| `PermissionError` | нет прав |
| `IsADirectoryError` | указана папка, а не файл |
| `UnicodeDecodeError` | неверная кодировка |

```python
try:
    with open("config.txt", encoding="utf-8") as f:
        config = f.read()
except FileNotFoundError:
    config = ""
    print("Файл настроек не найден")
```
