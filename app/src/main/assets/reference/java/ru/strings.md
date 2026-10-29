# Строки String

`String` — класс для текста. Строки в Java **неизменяемы**: любой метод, «меняющий» строку, возвращает новую.

## Основные методы

```java
String s = "  Лабораторная работа  ";

s.length();             // 23
s.trim();               // "Лабораторная работа"
s.strip();              // то же, учитывает Unicode-пробелы (Java 11+)
s.toUpperCase();        // "  ЛАБОРАТОРНАЯ РАБОТА  "
s.isBlank();            // false (Java 11+)
"".isEmpty();           // true
```

```java
String path = "labs/lab3/report.docx";

path.charAt(0);                    // 'l'
path.indexOf('/');                 // 4
path.lastIndexOf('/');             // 9
path.substring(10);                // "report.docx"
path.substring(0, 4);              // "labs"
path.contains("lab3");             // true
path.startsWith("labs");           // true
path.endsWith(".docx");            // true
path.replace("docx", "pdf");       // "labs/lab3/report.pdf"
```

`substring(начало, конец)` — конец не включается.

## Сравнение строк

> [!WARNING]
> Строки сравнивают методом `equals`, а не `==`. Оператор `==` проверяет, один ли это объект в памяти, и может вернуть `false` для одинакового текста.

```java
String a = "java";
String b = new String("java");

System.out.println(a == b);              // false
System.out.println(a.equals(b));         // true
System.out.println(a.equalsIgnoreCase("JAVA")); // true
System.out.println("apple".compareTo("banana")); // отрицательное — раньше по алфавиту
```

## Разбиение и склеивание

```java
String line = "Анна;П-21;8.4";
String[] parts = line.split(";");
System.out.println(parts[1]);          // П-21

String joined = String.join(", ", "C#", "Java", "Python");
System.out.println(joined);            // C#, Java, Python
```

> [!NOTE]
> `split` принимает регулярное выражение. Чтобы разделить по точке, пишите `split("\\.")`, а не `split(".")`.

## Форматирование

```java
String name = "Анна";
double avg = 8.456;

String s1 = String.format("%s: %.2f", name, avg);  // Анна: 8,46 или 8.46 — по локали
String s2 = "%s — %d курс".formatted(name, 2);     // Java 15+
```

| Спецификатор | Тип |
|---|---|
| `%s` | строка |
| `%d` | целое |
| `%.2f` | дробное, 2 знака |
| `%5d`, `%-10s` | ширина поля, `-` — влево |
| `%n` | перевод строки |

## Преобразования

```java
int n = Integer.parseInt("42");
double d = Double.parseDouble("3.14");
String s = String.valueOf(2026);
String t = "" + 15;          // тоже строка
char[] chars = "abc".toCharArray();
```

Если строка не число, `parseInt` выбросит `NumberFormatException`.

## StringBuilder

Склеивание строк через `+` в цикле каждый раз создаёт новый объект. Для сборки строки в цикле используйте `StringBuilder`:

```java
StringBuilder sb = new StringBuilder();
for (int i = 1; i <= 3; i++) {
    sb.append("Лаб ").append(i).append('\n');
}
sb.insert(0, "Список:\n");
String result = sb.toString();
```

## Текстовые блоки

С Java 15 многострочный текст пишут в тройных кавычках:

```java
String json = """
    {
      "name": "Анна",
      "group": "П-21"
    }
    """;
```
