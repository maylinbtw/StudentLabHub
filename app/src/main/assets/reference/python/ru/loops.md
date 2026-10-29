# Циклы

В Python два цикла: `for` перебирает элементы коллекции, `while` повторяет код, пока условие истинно.

## for и range

`for` в Python всегда перебирает элементы чего-либо. Для счёта используют `range`:

```python
for i in range(5):
    print(i, end=" ")      # 0 1 2 3 4
print()

for i in range(1, 6):
    print(i, end=" ")      # 1 2 3 4 5
print()

for i in range(10, 0, -3):
    print(i, end=" ")      # 10 7 4 1
```

| Вызов | Числа |
|---|---|
| `range(5)` | 0, 1, 2, 3, 4 |
| `range(2, 6)` | 2, 3, 4, 5 — конец не включается |
| `range(0, 10, 2)` | 0, 2, 4, 6, 8 |
| `range(5, 0, -1)` | 5, 4, 3, 2, 1 |

## Перебор коллекций

```python
marks = [8, 6, 9, 7]
for m in marks:
    print(m)

for ch in "ЛР№5":
    print(ch)

grades = {"ЛР1": 9, "ЛР2": 7}
for lab, mark in grades.items():
    print(f"{lab}: {mark}")
```

## enumerate и zip

```python
labs = ["ЛР1", "ЛР2", "ЛР3"]
for number, lab in enumerate(labs, start=1):
    print(number, lab)

names = ["Анна", "Иван"]
scores = [9, 7]
for name, score in zip(names, scores):
    print(f"{name} — {score}")
```

```output
1 ЛР1
2 ЛР2
3 ЛР3
Анна — 9
Иван — 7
```

## while

```python
n, digits = 1234, 0
while n > 0:
    n //= 10
    digits += 1
print(digits)    # 4
```

Цикла `do…while` в Python нет. Его заменяют бесконечным циклом с `break`:

```python
while True:
    answer = input("Продолжить? (да/нет) ")
    if answer in ("да", "нет"):
        break
```

## break, continue и else

```python
for i in range(1, 21):
    if i % 3 != 0:
        continue
    if i > 12:
        break
    print(i, end=" ")    # 3 6 9 12
```

У циклов Python есть необычная ветка `else`: она выполняется, если цикл завершился **без** `break`:

```python
for lab in labs:
    if lab == "ЛР5":
        print("Нашли")
        break
else:
    print("ЛР5 в списке нет")
```

## Генераторы списков

Частый цикл «создать список из другого» записывается в одну строку:

```python
squares = [x ** 2 for x in range(1, 6)]          # [1, 4, 9, 16, 25]
good = [m for m in marks if m >= 8]              # [8, 9]
names_upper = [n.upper() for n in names]
```

## Полезные функции

```python
sum(marks)                 # 30
min(marks), max(marks)     # (6, 9)
sorted(marks)              # [6, 7, 8, 9]
reversed(marks)            # перебор в обратном порядке
any(m < 4 for m in marks)  # есть ли пересдачи — False
all(m >= 4 for m in marks) # все ли зачтены — True
```

> [!WARNING]
> Не изменяйте список, пока перебираете его в `for`: удаление элементов приведёт к пропускам. Перебирайте копию `for x in items[:]` или создайте новый список.

## Проверь себя

> [!QUIZ]
> Какие числа даст `range(2, 10, 3)`?
> ---
> 2, 5, 8 — конец не включается.

> [!QUIZ]
> Как перебирать список вместе с индексами?
> ---
> `for i, item in enumerate(items):`.
