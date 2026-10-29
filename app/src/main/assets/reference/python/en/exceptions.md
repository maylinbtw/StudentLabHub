# Exceptions

An **exception** is a run-time error. If it isn't handled, the program stops and prints a **traceback** — the chain of calls that led to the error.

```python
print(10 / 0)
```

```output
Traceback (most recent call last):
  File "main.py", line 1, in <module>
    print(10 / 0)
ZeroDivisionError: division by zero
```

Read a traceback from the bottom up: the last line is the error type and message, above it is where it happened.

## try and except

```python
try:
    n = int(input("Number: "))
    print(100 / n)
except ValueError:
    print("Enter a whole number")
except ZeroDivisionError:
    print("Cannot divide by zero")
```

Several types in one block, plus the error text:

```python
except (ValueError, ZeroDivisionError) as e:
    print("Error:", e)
```

## else and finally

```python
try:
    f = open("marks.txt", encoding="utf-8")
except FileNotFoundError:
    print("No file")
else:
    print(f.read())     # runs if there was no error
    f.close()
finally:
    print("Done")       # always runs
```

## Common exceptions

| Exception | When |
|---|---|
| `ValueError` | `int("abc")` — an invalid value |
| `TypeError` | `"5" + 5` — an unsuitable type |
| `ZeroDivisionError` | division by 0 |
| `IndexError` | index outside the list |
| `KeyError` | no such key in the dict |
| `AttributeError` | the object has no such attribute or method |
| `NameError` | the variable isn't defined (often a typo) |
| `FileNotFoundError` | the file wasn't found |

## raise

Your own check that raises an exception:

```python
def average(marks):
    if not marks:
        raise ValueError("The marks list is empty")
    return sum(marks) / len(marks)
```

## Custom exception

```python
class DeadlineError(Exception):
    pass

def submit(day):
    if day > 15:
        raise DeadlineError(f"Deadline passed: {day} > 15")

try:
    submit(20)
except DeadlineError as e:
    print(e)    # Deadline passed: 20 > 15
```

## Repeating input until it's valid

```python
while True:
    try:
        mark = int(input("Mark (0–10): "))
        if 0 <= mark <= 10:
            break
        print("Out of range")
    except ValueError:
        print("Not a number")
```

> [!WARNING]
> Don't write a bare `except:` or `except Exception: pass` — it swallows every error, including typos in your code, and makes problems very hard to find. Catch specific types.

## assert

A condition check for debugging — raises `AssertionError` if the condition is false:

```python
assert len(marks) > 0, "No marks"
```

## Check yourself

> [!QUIZ]
> When does the `else` block after `try` run?
> ---
> Only if no exception occurred in `try`.

> [!QUIZ]
> Which exception does `int("abc")` raise?
> ---
> `ValueError`.
