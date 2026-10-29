# Your first Python program

**Python** is an interpreted language with a simple syntax. Code does not need compiling: the interpreter reads and runs it line by line. Python is used for scripts, web servers, data analysis, machine learning and automation.

## Installing and checking

Download Python 3 from python.org (on Windows, tick **Add Python to PATH** during installation). On Linux it is usually preinstalled.

```bash
python3 --version      # on Windows: python --version or py --version
```

```output
Python 3.12.3
```

## Interactive mode

Running `python3` with no arguments opens the **REPL**, where each line runs immediately. Handy for quick checks:

```text
>>> 2 + 2
4
>>> "lab" * 3
'lablablab'
>>> exit()
```

## A program in a file

Create a file named `hello.py`:

```python
print("Hello, World!")
```

And run it:

```bash
python3 hello.py
```

```output
Hello, World!
```

No classes, no `main`, no semicolons — the program starts running from the first line of the file.

## Indentation instead of braces

Python marks code blocks with **indentation** rather than curly braces. A line ending with `:` opens a block, and every line of the block is shifted by the same number of spaces (4 by convention):

```python
mark = 8

if mark >= 4:
    print("Passed")
    print("You can go home")
print("This line is outside the condition")
```

> [!WARNING]
> Mixing tabs and spaces or uneven indentation causes an `IndentationError`. Configure your editor so that Tab inserts 4 spaces.

## Comments

```python
# single-line comment

"""
A triple-quoted string is often used
as a multi-line comment and description (docstring).
"""
```

## The main block

In multi-file programs the start-up code is written like this:

```python
def main():
    print("Program started")


if __name__ == "__main__":
    main()
```

The block under `if __name__ == "__main__":` runs only when the file is executed directly, not when it is imported.

## Where to write code

- **IDLE** — a simple editor bundled with Python;
- **PyCharm** — a full IDE;
- **VS Code** with the Python extension;
- **Jupyter Notebook** — for data analysis and experiments.

## Check yourself

> [!QUIZ]
> What marks the body of an `if`, loop or function in Python?
> ---
> Indentation — usually 4 spaces. There are no braces.

> [!QUIZ]
> What is `if __name__ == "__main__":` for?
> ---
> The code inside runs only when the file is run directly, not when it's imported.
