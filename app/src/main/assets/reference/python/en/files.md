# Working with files

## Reading and writing

A file is opened with `open` inside `with` — then it closes automatically:

```python
with open("note.txt", "w", encoding="utf-8") as f:
    f.write("Submit lab 3 by Friday\n")
    f.write("Bring a flash drive\n")

with open("note.txt", encoding="utf-8") as f:
    text = f.read()
print(text)
```

> [!WARNING]
> Always pass `encoding="utf-8"`. Without it Windows uses the system encoding, and non-Latin text turns into garbage or raises `UnicodeDecodeError`.

## Open modes

| Mode | Meaning |
|---|---|
| `"r"` | read (default), the file must exist |
| `"w"` | write, the file is created or **emptied** |
| `"a"` | append to the end |
| `"x"` | create a new one, error if it exists |
| `"b"` | binary mode: `"rb"`, `"wb"` |

## Reading line by line

```python
with open("marks.txt", encoding="utf-8") as f:
    for line in f:
        print(line.strip())      # strip removes the trailing \n

with open("marks.txt", encoding="utf-8") as f:
    lines = f.read().splitlines()   # a list of lines without \n
```

Iterating `for line in f` reads one line at a time — fine even for very large files.

## Writing a list

```python
marks = [8, 9, 7]
with open("marks.txt", "w", encoding="utf-8") as f:
    for m in marks:
        f.write(f"{m}\n")

# or like this
with open("marks.txt", "w", encoding="utf-8") as f:
    print(*marks, sep="\n", file=f)
```

## CSV

```python
import csv

rows = [["Name", "Group", "Average"], ["Anna", "P-21", 8.4], ["Oleg", "P-22", 7.5]]

with open("students.csv", "w", newline="", encoding="utf-8") as f:
    csv.writer(f, delimiter=";").writerows(rows)

with open("students.csv", newline="", encoding="utf-8") as f:
    for row in csv.DictReader(f, delimiter=";"):
        print(row["Name"], row["Average"])
```

## JSON

```python
import json

data = {"name": "Anna", "marks": [8, 9, 7]}

with open("data.json", "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

with open("data.json", encoding="utf-8") as f:
    loaded = json.load(f)
print(loaded["marks"])   # [8, 9, 7]
```

`ensure_ascii=False` keeps non-Latin text readable, `indent=2` adds indentation.

## Paths: pathlib

```python
from pathlib import Path

folder = Path("data") / "2026"
folder.mkdir(parents=True, exist_ok=True)

file = folder / "note.txt"
file.write_text("Hello", encoding="utf-8")
print(file.read_text(encoding="utf-8"))

print(file.name)      # note.txt
print(file.suffix)    # .txt
print(file.exists())  # True

for p in Path("data").rglob("*.txt"):   # all .txt in nested folders
    print(p)
```

## Errors

| Exception | Cause |
|---|---|
| `FileNotFoundError` | the file doesn't exist |
| `PermissionError` | no permission |
| `IsADirectoryError` | a folder was given instead of a file |
| `UnicodeDecodeError` | wrong encoding |

```python
try:
    with open("config.txt", encoding="utf-8") as f:
        config = f.read()
except FileNotFoundError:
    config = ""
    print("Settings file not found")
```

## Check yourself

> [!QUIZ]
> Why open files with `with open(...)`?
> ---
> The file closes automatically even if an error happens inside the block.

> [!QUIZ]
> What does mode `"w"` do to an existing file?
> ---
> It empties it. To append, use mode `"a"`.
