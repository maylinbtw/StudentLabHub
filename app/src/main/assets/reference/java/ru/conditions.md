# Условия: if и switch

## if, else if, else

```java
int mark = scanner.nextInt();

if (mark >= 9) {
    System.out.println("Отлично");
} else if (mark >= 4) {
    System.out.println("Зачтено");
} else {
    System.out.println("Пересдача");
}
```

В Java открывающую скобку принято ставить на той же строке, что и условие.

> Условие должно иметь тип `boolean`. Запись `if (count)` для целого `count` не скомпилируется — пишите `if (count > 0)`.

## Классический switch

```java
int day = 3;

switch (day) {
    case 1:
        System.out.println("Понедельник");
        break;
    case 6:
    case 7:
        System.out.println("Выходной");
        break;
    default:
        System.out.println("Будний день");
}
```

В `switch` можно проверять `int`, `char`, `String` и перечисления `enum`.

> [!WARNING]
> Без `break` выполнение проваливается в следующий `case` — это частая ошибка в лабораторных.

## switch со стрелками (Java 14+)

Новая форма не проваливается и умеет возвращать значение:

```java
String name = switch (day) {
    case 1 -> "Пн";
    case 2 -> "Вт";
    case 3 -> "Ср";
    case 4 -> "Чт";
    case 5 -> "Пт";
    case 6, 7 -> "Выходной";
    default -> "Ошибка";
};
```

Если в ветке несколько инструкций, используйте блок и `yield`:

```java
int points = switch (grade) {
    case "A" -> 10;
    case "B" -> 8;
    default -> {
        System.out.println("Неизвестная оценка: " + grade);
        yield 0;
    }
};
```

## switch со строками

```java
String command = scanner.nextLine().trim().toLowerCase();

switch (command) {
    case "add" -> addLab();
    case "list" -> printLabs();
    case "exit" -> System.exit(0);
    default -> System.out.println("Неизвестная команда");
}
```

## Сопоставление с образцом (Java 21)

`switch` может проверять тип объекта и сразу приводить его:

```java
Object value = 42;

String text = switch (value) {
    case Integer i when i > 10 -> "большое число " + i;
    case Integer i -> "число " + i;
    case String s -> "строка " + s;
    default -> "что-то другое";
};
```

То же для `if`:

```java
if (value instanceof String s && !s.isEmpty()) {
    System.out.println(s.length());
}
```
