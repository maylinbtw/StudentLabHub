# Исключения

**Исключение** — ошибка во время выполнения. Если его не обработать, программа остановится и напечатает **traceback** — цепочку вызовов, которая привела к ошибке.

```python
print(10 / 0)
```

```output
Traceback (most recent call last):
  File "main.py", line 1, in <module>
    print(10 / 0)
ZeroDivisionError: division by zero
```

Читайте traceback снизу вверх: последняя строка — тип и описание ошибки, выше — где она произошла.

## try и except

```python
try:
    n = int(input("Число: "))
    print(100 / n)
except ValueError:
    print("Нужно целое число")
except ZeroDivisionError:
    print("На ноль делить нельзя")
```

Несколько типов в одном блоке и текст ошибки:

```python
except (ValueError, ZeroDivisionError) as e:
    print("Ошибка:", e)
```

## else и finally

```python
try:
    f = open("marks.txt", encoding="utf-8")
except FileNotFoundError:
    print("Файла нет")
else:
    print(f.read())     # выполняется, если ошибки не было
    f.close()
finally:
    print("Готово")     # выполняется всегда
```

## Частые исключения

| Исключение | Когда |
|---|---|
| `ValueError` | `int("abc")` — неверное значение |
| `TypeError` | `"5" + 5` — неподходящий тип |
| `ZeroDivisionError` | деление на 0 |
| `IndexError` | индекс за пределами списка |
| `KeyError` | нет такого ключа в словаре |
| `AttributeError` | у объекта нет такого атрибута или метода |
| `NameError` | переменная не объявлена (часто — опечатка) |
| `FileNotFoundError` | файл не найден |

## raise

Своя проверка с выбросом исключения:

```python
def average(marks):
    if not marks:
        raise ValueError("Список оценок пуст")
    return sum(marks) / len(marks)
```

## Своё исключение

```python
class DeadlineError(Exception):
    pass

def submit(day):
    if day > 15:
        raise DeadlineError(f"Срок прошёл: {day} > 15")

try:
    submit(20)
except DeadlineError as e:
    print(e)    # Срок прошёл: 20 > 15
```

## Повтор ввода до правильного значения

```python
while True:
    try:
        mark = int(input("Оценка (0–10): "))
        if 0 <= mark <= 10:
            break
        print("Вне диапазона")
    except ValueError:
        print("Это не число")
```

> [!WARNING]
> Не пишите голый `except:` или `except Exception: pass` — так проглатываются все ошибки, включая опечатки в коде, и найти проблему становится очень трудно. Перехватывайте конкретные типы.

## assert

Проверка условия для отладки — при ложном условии выбрасывает `AssertionError`:

```python
assert len(marks) > 0, "Нет оценок"
```
