# Console input and output

Output goes through `System.out`, input through the `Scanner` class from the `java.util` package.

## Output

```java
System.out.print("No line break. ");
System.out.println("With a line break.");
System.out.println();                    // empty line

int done = 7, total = 10;
System.out.println("Done " + done + " of " + total);
```

```output
No line break. With a line break.

Done 7 of 10
```

> Careful with `+`: `"Total: " + 2 + 3` gives `"Total: 23"` because addition goes left to right and produces a string first. You need `"Total: " + (2 + 3)`.

## Formatted output

`printf` and `String.format` fill values into a template:

```java
double avg = 7.4567;
String name = "Anna";

System.out.printf("%s: average %.2f%n", name, avg);
String line = String.format("%-10s|%5d|", "Lab1", 9);
```

```output
Anna: average 7.46
```

| Pattern | Prints |
|---|---|
| `%d` | an integer |
| `%f`, `%.2f` | a fraction, 2 decimal places |
| `%s` | a string (or any object) |
| `%c` | a character |
| `%b` | a boolean |
| `%n` | a line break |
| `%5d`, `%-10s` | width 5 right-aligned, width 10 left-aligned |

> The decimal separator in `printf` depends on the system language. To force a dot, pass a locale: `String.format(Locale.US, "%.2f", avg)`.

## Input: Scanner

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Age: ");
        int age = scanner.nextInt();

        System.out.println("Hi, " + name + "! Next year you will be " + (age + 1));
    }
}
```

Main methods:

| Method | Reads |
|---|---|
| `nextLine()` | a whole line up to Enter |
| `next()` | one word up to a space |
| `nextInt()`, `nextLong()` | an integer |
| `nextDouble()` | a fraction |
| `hasNextInt()` | check: is the next token an integer? |

> [!WARNING]
> After `nextInt()` a newline stays in the buffer, and the next `nextLine()` returns an empty string. Add an extra `scanner.nextLine();` after reading a number, or read everything with `nextLine()` and `Integer.parseInt`.

## Validating input

```java
System.out.print("Number of labs: ");
while (!scanner.hasNextInt()) {
    System.out.print("An integer, please: ");
    scanner.next();              // discard the bad input
}
int count = scanner.nextInt();
```

## Errors and stderr

```java
System.err.println("File not found");   // error stream, shown in red in the IDE
```
