# Переменные и типы данных

Java — язык со строгой статической типизацией. Типы делятся на **примитивные** (числа, символы, логический тип) и **ссылочные** (строки, массивы, объекты классов).

## Примитивные типы

| Тип | Размер | Диапазон / пример |
|---|---|---|
| `byte` | 1 байт | −128 … 127 |
| `short` | 2 байта | −32 768 … 32 767 |
| `int` | 4 байта | около ±2,1 млрд |
| `long` | 8 байт | около ±9,2·10¹⁸, литерал `10L` |
| `float` | 4 байта | ~7 цифр, литерал `3.5f` |
| `double` | 8 байт | ~15 цифр, `3.5` |
| `char` | 2 байта | символ Unicode: `'Я'` |
| `boolean` | — | `true` или `false` |

В отличие от C++, размеры типов одинаковы на всех платформах, а беззнаковых типов нет.

```java
int labs = 12;
long fileSize = 3_000_000_000L;   // без L — ошибка: число не помещается в int
double avg = 7.5;
char grade = 'A';
boolean passed = true;
```

## Строки

`String` — ссылочный тип, но со строками можно работать почти как с примитивами:

```java
String name = "Анна";
String greet = "Привет, " + name + "!";
int len = name.length();             // 4
char first = name.charAt(0);         // 'А'
String upper = name.toUpperCase();   // "АННА"
```

> [!WARNING]
> Строки сравнивают методом `equals`, а не `==`. Оператор `==` проверяет, один ли это объект, и может вернуть `false` для одинакового текста.

```java
String input = scanner.nextLine();
if (input.equals("exit")) { ... }
if (input.equalsIgnoreCase("EXIT")) { ... }
```

## var

С Java 10 тип локальной переменной можно вывести автоматически:

```java
var count = 10;           // int
var title = "Отчёт";      // String
var list = new ArrayList<String>();
```

## Константы

```java
final int MAX_LABS = 20;          // значение нельзя изменить
static final double PI = 3.14159; // константа класса
```

## Значения по умолчанию

Поля классов получают значения по умолчанию: `0`, `0.0`, `false`, `'\u0000'` и `null` для ссылок. Локальные переменные — нет: чтение неинициализированной локальной переменной не скомпилируется.

## Преобразование типов

```java
int i = 100;
long l = i;                  // расширение — автоматически
double d = i;                // 100.0

double price = 9.99;
int whole = (int) price;     // 9 — сужение только явно

int n = Integer.parseInt("42");          // строка → число
double x = Double.parseDouble("3.5");
String s = String.valueOf(42);           // число → строка
String t = "" + 42;                      // тоже работает
```

## Классы-обёртки

Для каждого примитива есть класс: `Integer`, `Double`, `Character`, `Boolean`… Они нужны, например, в коллекциях — `ArrayList<int>` написать нельзя, только `ArrayList<Integer>`. Преобразование между ними происходит автоматически (автоупаковка).

```java
Integer boxed = 5;          // int → Integer
int raw = boxed;            // Integer → int
int max = Integer.MAX_VALUE;
```
