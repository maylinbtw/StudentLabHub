# Input and output

Console output uses the `print` function, input uses `input`.

## print

`print` prints values separated by spaces and ends with a line break:

```python
done, total = 7, 10
print("Done", done, "of", total)
print("No line break", end="")
print(" — continued")
print("a", "b", "c", sep=" | ")
```

```output
Done 7 of 10
No line break — continued
a | b | c
```

- `sep` is the separator between values (a space by default);
- `end` is what to print at the end (`"\n"` by default).

## f-strings

The most convenient way to put values into text is an **f-string**: the letter `f` before the quotes and expressions in curly braces:

```python
name = "Anna"
avg = 7.4567

print(f"{name}: average {avg:.2f}")
print(f"Labs remaining: {total - done}")
print(f"{done=}")                 # debugging: prints done=7
```

```output
Anna: average 7.46
Labs remaining: 3
done=7
```

## Number formats

| Format | Value | Result |
|---|---|---|
| `{x:.2f}` | 3.14159 | 3.14 |
| `{x:,}` | 1234567 | 1,234,567 |
| `{x:.1%}` | 0.256 | 25.6% |
| `{x:5}` | 42 | `   42` |
| `{x:<10}` | "Lab1" | `Lab1      ` |
| `{x:03}` | 7 | 007 |

## input

`input` shows a prompt, waits for input and **always returns a string**:

```python
name = input("What is your name? ")
print(f"Hello, {name}!")
```

Numbers must be converted explicitly:

```python
age = int(input("Age: "))
height = float(input("Height in meters: "))
print(f"Next year you will be {age + 1}")
```

> [!WARNING]
> If the input is not a number, `int(...)` raises `ValueError` and the program stops. Wrap the conversion in `try` for robust input.

```python
while True:
    try:
        count = int(input("Number of labs: "))
        break
    except ValueError:
        print("An integer, please")
```

## Several values on one line

```python
a, b = map(int, input("Two numbers separated by a space: ").split())
print(a + b)

marks = list(map(int, input("Marks: ").split()))
print(sum(marks) / len(marks))
```

```output
Two numbers separated by a space: 3 4
7
Marks: 8 6 9 7
7.5
```

`split()` cuts the string at spaces, `map(int, ...)` turns each piece into a number.

## Check yourself

> [!QUIZ]
> How do you print `x` with two decimal places?
> ---
> With an f-string: `print(f"{x:.2f}")`.

> [!QUIZ]
> How do you read two numbers from one input line?
> ---
> `a, b = map(int, input().split())`.
