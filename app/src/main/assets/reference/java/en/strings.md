# String

`String` is the class for text. Strings in Java are **immutable**: any method that "changes" a string returns a new one.

## Basic methods

```java
String s = "  Laboratory work  ";

s.length();             // 19
s.trim();               // "Laboratory work"
s.strip();              // same, aware of Unicode spaces (Java 11+)
s.toUpperCase();        // "  LABORATORY WORK  "
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

In `substring(start, end)` the end is not included.

## Comparing strings

> [!WARNING]
> Compare strings with `equals`, not `==`. The `==` operator checks whether it is the same object in memory and may return `false` for identical text.

```java
String a = "java";
String b = new String("java");

System.out.println(a == b);              // false
System.out.println(a.equals(b));         // true
System.out.println(a.equalsIgnoreCase("JAVA")); // true
System.out.println("apple".compareTo("banana")); // negative — comes first alphabetically
```

## Splitting and joining

```java
String line = "Anna;P-21;8.4";
String[] parts = line.split(";");
System.out.println(parts[1]);          // P-21

String joined = String.join(", ", "C#", "Java", "Python");
System.out.println(joined);            // C#, Java, Python
```

> [!NOTE]
> `split` takes a regular expression. To split by a dot, write `split("\\.")`, not `split(".")`.

## Formatting

```java
String name = "Anna";
double avg = 8.456;

String s1 = String.format("%s: %.2f", name, avg);  // Anna: 8.46 (or 8,46 depending on locale)
String s2 = "%s — year %d".formatted(name, 2);     // Java 15+
```

| Specifier | Type |
|---|---|
| `%s` | string |
| `%d` | integer |
| `%.2f` | decimal, 2 digits |
| `%5d`, `%-10s` | field width, `-` aligns left |
| `%n` | newline |

## Conversions

```java
int n = Integer.parseInt("42");
double d = Double.parseDouble("3.14");
String s = String.valueOf(2026);
String t = "" + 15;          // also a string
char[] chars = "abc".toCharArray();
```

If the string isn't a number, `parseInt` throws `NumberFormatException`.

## StringBuilder

Concatenating with `+` in a loop creates a new object every time. To build a string in a loop, use `StringBuilder`:

```java
StringBuilder sb = new StringBuilder();
for (int i = 1; i <= 3; i++) {
    sb.append("Lab ").append(i).append('\n');
}
sb.insert(0, "List:\n");
String result = sb.toString();
```

## Text blocks

Since Java 15, multi-line text is written in triple quotes:

```java
String json = """
    {
      "name": "Anna",
      "group": "P-21"
    }
    """;
```

## Check yourself

> [!QUIZ]
> Why compare strings with `equals` rather than `==`?
> ---
> `==` checks whether it's the same object in memory, `equals` compares the text.

> [!QUIZ]
> How do you split a string by a dot?
> ---
> `s.split("\\.")`: `split` takes a regex, where a dot means any character.
