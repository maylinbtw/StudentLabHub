# Исключения

**Исключение** — ошибка во время выполнения: деление на ноль, неверный формат числа, отсутствующий файл. Необработанное исключение завершает программу и печатает **стек вызовов**.

## try и catch

```java
import java.util.Scanner;

Scanner in = new Scanner(System.in);
try {
    System.out.print("Число: ");
    int n = Integer.parseInt(in.nextLine());
    System.out.println(100 / n);
} catch (NumberFormatException e) {
    System.out.println("Нужно целое число");
} catch (ArithmeticException e) {
    System.out.println("На ноль делить нельзя");
}
```

Несколько типов можно обработать одним блоком:

```java
catch (NumberFormatException | ArithmeticException e) {
    System.out.println("Ошибка: " + e.getMessage());
}
```

## finally

`finally` выполняется всегда — была ошибка или нет:

```java
try {
    System.out.println("Работаем");
} finally {
    System.out.println("Готово");
}
```

## try-with-resources

Для файлов, сканеров и соединений — ресурс закроется сам:

```java
import java.io.*;

try (BufferedReader reader = new BufferedReader(new FileReader("marks.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.out.println("Не удалось прочитать файл: " + e.getMessage());
}
```

## Проверяемые и непроверяемые

| Вид | Примеры | Что требует компилятор |
|---|---|---|
| проверяемые (checked) | `IOException`, `FileNotFoundException` | обработать в `catch` или объявить `throws` |
| непроверяемые (unchecked) | `NullPointerException`, `ArithmeticException`, `IllegalArgumentException` | ничего |

Непроверяемые наследуют `RuntimeException` и обычно означают ошибку в коде.

```java
static String readFirstLine(String path) throws IOException {
    try (var reader = new BufferedReader(new FileReader(path))) {
        return reader.readLine();
    }
}
```

`throws IOException` перекладывает обработку на вызывающий код.

## Частые исключения

| Исключение | Когда |
|---|---|
| `NullPointerException` | вызов метода у `null` |
| `ArrayIndexOutOfBoundsException` | индекс за границами массива |
| `NumberFormatException` | `Integer.parseInt("abc")` |
| `ArithmeticException` | целочисленное деление на 0 |
| `ClassCastException` | неверное приведение типа |
| `InputMismatchException` | `Scanner.nextInt()` получил не число |

## throw

```java
static double average(int[] marks) {
    if (marks.length == 0) {
        throw new IllegalArgumentException("Массив оценок пуст");
    }
    int sum = 0;
    for (int m : marks) sum += m;
    return (double) sum / marks.length;
}
```

## Своё исключение

```java
class DeadlineMissedException extends Exception {
    public DeadlineMissedException(String message) {
        super(message);
    }
}
```

> [!WARNING]
> Не оставляйте пустой `catch (Exception e) {}` — ошибка пропадёт бесследно, и найти её будет очень сложно. Хотя бы выведите `e.printStackTrace()`.

## Проверь себя

> [!QUIZ]
> Чем проверяемые исключения отличаются от непроверяемых?
> ---
> Проверяемые (`IOException`) компилятор требует обработать или объявить в `throws`. Непроверяемые (`RuntimeException`) — нет.

> [!QUIZ]
> Что даёт конструкция try-with-resources?
> ---
> Ресурс из скобок `try (...)` закрывается автоматически, даже при ошибке.
