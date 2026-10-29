# Методы

**Метод** — именованный блок кода внутри класса. В Java нет функций вне классов: всё оформляется методами.

## Объявление и вызов

```java
public class Main {

    static double average(int a, int b, int c) {
        return (a + b + c) / 3.0;
    }

    static void printLine(int width) {
        System.out.println("-".repeat(width));
    }

    public static void main(String[] args) {
        printLine(20);
        System.out.println("Средний балл: " + average(8, 9, 7));
        printLine(20);
    }
}
```

```output
--------------------
Средний балл: 8.0
--------------------
```

| Часть | Значение |
|---|---|
| `static` | метод класса, вызывается без создания объекта |
| `double` / `void` | тип результата; `void` — ничего не возвращает |
| `average` | имя, принято писать в camelCase |
| `(int a, ...)` | параметры |

Порядок методов в классе не важен: `main` может стоять и выше, и ниже вызываемых методов.

## return

`return` возвращает значение и сразу завершает метод. Каждый путь должен заканчиваться `return`:

```java
static String grade(int mark) {
    if (mark >= 9) return "отлично";
    if (mark >= 6) return "хорошо";
    if (mark >= 4) return "удовлетворительно";
    return "не сдано";
}
```

Без последней строки будет ошибка компиляции `missing return statement`.

## Передача аргументов

В Java аргументы всегда передаются **по значению**. Для примитивов копируется само число — метод не изменит исходную переменную:

```java
static void addOne(int n) {
    n++;
}

int x = 5;
addOne(x);
System.out.println(x); // 5
```

Для объектов и массивов копируется **ссылка**. Метод может изменить содержимое объекта, но не может подменить сам объект у вызывающего кода:

```java
static void fill(int[] arr) {
    arr[0] = 100;          // изменится и снаружи
    arr = new int[3];      // снаружи ничего не изменится
}
```

## Перегрузка

Методы с одним именем и разными параметрами:

```java
static int max(int a, int b) { return a > b ? a : b; }
static double max(double a, double b) { return a > b ? a : b; }
static int max(int a, int b, int c) { return max(max(a, b), c); }
```

Тип результата в выборе перегрузки не участвует.

## Переменное число аргументов

```java
static int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) total += n;
    return total;
}

sum();           // 0
sum(1, 2, 3);    // 6
```

Внутри метода `numbers` — обычный массив. Такой параметр может быть только один и последним.

## Рекурсия

```java
static long factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

System.out.println(factorial(10)); // 3628800
```

> [!WARNING]
> Без базового случая рекурсия заканчивается ошибкой `StackOverflowError`.

## Проверь себя

> [!QUIZ]
> Изменится ли массив снаружи, если метод сделает `arr[0] = 100`?
> ---
> Да: копируется ссылка, и метод меняет тот же массив.

> [!QUIZ]
> Что значит `int... numbers` в параметрах?
> ---
> Переменное число аргументов: внутри метода это обычный массив `int[]`.
