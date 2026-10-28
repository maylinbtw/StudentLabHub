# Conditions: if and match

## if, elif, else

```python
mark = int(input("Mark: "))

if mark >= 9:
    print("Excellent")
elif mark >= 4:
    print("Passed")
else:
    print("Retake")
```

- the condition ends with a colon `:`;
- the branch body is indented;
- `elif` is used instead of `else if`;
- no parentheses are needed around the condition.

> [!WARNING]
> An empty branch is not allowed. If the code is not written yet, put `pass` — a statement that does nothing.

```python
if mark < 4:
    pass   # retake handling comes later
```

## Nested conditions

```python
if done:
    if defended:
        print("Work closed")
    else:
        print("Needs defending")
else:
    print("Needs doing")
```

## Conditional expressions

The equivalent of the ternary operator is written in words:

```python
status = "passed" if mark >= 4 else "failed"
print(f"Max: {a if a > b else b}")
```

## Membership checks

`in` checks whether a value is in a string, list, dictionary or set:

```python
day = input("Day: ").lower()

if day in ("saturday", "sunday"):
    print("Weekend")

if "Lab" in title:
    print("It is a lab")
```

## match (Python 3.10+)

`match` compares a value against **patterns** — a more powerful relative of `switch`:

```python
command = input("> ").strip().lower()

match command:
    case "add":
        print("Adding a lab")
    case "list" | "ls":
        print("List of labs")
    case "exit" | "quit":
        print("Bye")
    case _:
        print("Unknown command")
```

- `|` combines several options;
- `_` means "anything else", like `default`;
- there is no fall-through between branches, so no `break` is needed.

## Guards and unpacking

```python
match mark:
    case 10:
        print("Perfect")
    case m if m >= 7:
        print(f"Good: {m}")
    case _:
        print("Could be better")
```

`match` can take lists and tuples apart:

```python
parts = input("> ").split()

match parts:
    case ["add", name]:
        print(f"Add {name}")
    case ["rename", old, new]:
        print(f"{old} → {new}")
    case []:
        print("Empty command")
    case _:
        print("Didn't get that")
```
