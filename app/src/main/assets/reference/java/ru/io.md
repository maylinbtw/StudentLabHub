# Ввод и вывод в консоли

Для вывода используется `System.out`, для ввода — класс `Scanner` из пакета `java.util`.

## Вывод

```java
System.out.print("Без перевода строки. ");
System.out.println("С переводом строки.");
System.out.println();                    // пустая строка

int done = 7, total = 10;
System.out.println("Сделано " + done + " из " + total);
```

```output
Без перевода строки. С переводом строки.

Сделано 7 из 10
```

> Осторожно с `+`: `"Итого: " + 2 + 3` даёт `"Итого: 23"`, потому что сложение идёт слева направо и сначала получается строка. Нужно `"Итого: " + (2 + 3)`.

## Форматированный вывод

`printf` и `String.format` подставляют значения по шаблону:

```java
double avg = 7.4567;
String name = "Анна";

System.out.printf("%s: средний балл %.2f%n", name, avg);
String line = String.format("%-10s|%5d|", "ЛР1", 9);
```

```output
Анна: средний балл 7,46
```

| Шаблон | Что выводит |
|---|---|
| `%d` | целое число |
| `%f`, `%.2f` | дробное, 2 знака после запятой |
| `%s` | строку (или любой объект) |
| `%c` | символ |
| `%b` | boolean |
| `%n` | перевод строки |
| `%5d`, `%-10s` | ширина 5 справа, ширина 10 слева |

> Разделитель дробной части в `printf` зависит от языка системы. Для точки укажите локаль: `String.format(Locale.US, "%.2f", avg)`.

## Ввод: Scanner

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Имя: ");
        String name = scanner.nextLine();

        System.out.print("Возраст: ");
        int age = scanner.nextInt();

        System.out.println("Привет, " + name + "! Через год тебе " + (age + 1));
    }
}
```

Основные методы:

| Метод | Читает |
|---|---|
| `nextLine()` | строку целиком до Enter |
| `next()` | одно слово до пробела |
| `nextInt()`, `nextLong()` | целое число |
| `nextDouble()` | дробное число |
| `hasNextInt()` | проверка: следующее — целое? |

> [!WARNING]
> После `nextInt()` в буфере остаётся перевод строки, и следующий `nextLine()` вернёт пустую строку. Добавьте лишний `scanner.nextLine();` после чтения числа или читайте всё через `nextLine()` и `Integer.parseInt`.

## Проверка ввода

```java
System.out.print("Количество работ: ");
while (!scanner.hasNextInt()) {
    System.out.print("Нужно целое число: ");
    scanner.next();              // выбросить неверный ввод
}
int count = scanner.nextInt();
```

## Ошибки и вывод в stderr

```java
System.err.println("Файл не найден");   // поток ошибок, в IDE выделяется красным
```

## Проверь себя

> [!QUIZ]
> Каким классом читают ввод с клавиатуры?
> ---
> `Scanner`: `new Scanner(System.in)` и методы `nextLine()`, `nextInt()`.

> [!QUIZ]
> Что делает `%n` в `printf`?
> ---
> Переводит строку с правильным для ОС символом.
