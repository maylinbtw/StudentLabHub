# Strings

A string (`str`) is an immutable sequence of characters. It can be written in single, double or triple quotes.

```python
a = 'Hello'
b = "Hello"
c = """Multi-line
text"""
path = r"C:\labs\new"      # r — a "raw" string, \ doesn't escape
```

## Indexes and slices

They work just like with lists:

```python
s = "Python"
s[0]       # 'P'
s[-1]      # 'n'
s[0:3]     # 'Pyt'
s[::-1]    # 'nohtyP'
len(s)     # 6
```

You can't change a character: `s[0] = "J"` raises `TypeError`. Create a new string: `"J" + s[1:]`.

## Basic methods

```python
s = "  Laboratory work  "

s.strip()                # 'Laboratory work' — trim spaces at the edges
s.lower(), s.upper()     # case
s.strip().title()        # 'Laboratory Work'
s.replace("work", "#3")
s.find("work")           # index or -1
s.count("o")             # how many times it occurs
s.startswith("  Lab")    # True
"42".isdigit()           # True
"abc".isalpha()          # True
```

Methods don't change the string but return a new one — save the result: `s = s.strip()`.

## Splitting and joining

```python
line = "Anna;P-21;8.4"
name, group, avg = line.split(";")
print(group)                         # P-21

words = "to be or not".split()       # by whitespace: ['to', 'be', 'or', 'not']

print(", ".join(["C#", "Java", "Python"]))   # C#, Java, Python
print("-".join("abc"))                        # a-b-c
```

> [!WARNING]
> `join` accepts only strings. Convert numbers first: `" ".join(map(str, [1, 2, 3]))`.

## f-strings

```python
name = "Anna"
avg = 8.456

print(f"{name}: {avg:.2f}")       # Anna: 8.46
print(f"{name:>10}|")             # right-aligned to 10
print(f"{name:<10}|")             # left
print(f"{42:05d}")                # 00042
print(f"{0.256:.1%}")             # 25.6%
print(f"{1234567:,}")             # 1,234,567
print(f"{avg=}")                  # avg=8.456 — for debugging
```

## Membership

```python
"lab" in "laboratory"          # True
"Lab" in "laboratory"          # False — case matters
```

## Iterating characters

```python
vowels = 0
for ch in "programming":
    if ch in "aeiou":
        vowels += 1
print(vowels)   # 3
```

## Multi-line text and escape sequences

| Notation | Meaning |
|---|---|
| `\n` | newline |
| `\t` | tab |
| `\\` | backslash |
| `\"` | a quote inside a string |

```python
print("Line 1\nLine 2")
print("Name\tMark")
```

## Character codes

```python
ord("A")      # 65
chr(66)       # 'B'
```
