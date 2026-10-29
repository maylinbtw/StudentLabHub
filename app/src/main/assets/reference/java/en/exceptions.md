# Exceptions

An **exception** is a run-time error: division by zero, a malformed number, a missing file. An unhandled exception stops the program and prints the **stack trace**.

## try and catch

```java
import java.util.Scanner;

Scanner in = new Scanner(System.in);
try {
    System.out.print("Number: ");
    int n = Integer.parseInt(in.nextLine());
    System.out.println(100 / n);
} catch (NumberFormatException e) {
    System.out.println("Enter a whole number");
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero");
}
```

Several types can be handled in one block:

```java
catch (NumberFormatException | ArithmeticException e) {
    System.out.println("Error: " + e.getMessage());
}
```

## finally

`finally` always runs — with or without an error:

```java
try {
    System.out.println("Working");
} finally {
    System.out.println("Done");
}
```

## try-with-resources

For files, scanners and connections — the resource closes itself:

```java
import java.io.*;

try (BufferedReader reader = new BufferedReader(new FileReader("marks.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.out.println("Could not read the file: " + e.getMessage());
}
```

## Checked and unchecked

| Kind | Examples | What the compiler requires |
|---|---|---|
| checked | `IOException`, `FileNotFoundException` | handle in `catch` or declare `throws` |
| unchecked | `NullPointerException`, `ArithmeticException`, `IllegalArgumentException` | nothing |

Unchecked exceptions extend `RuntimeException` and usually mean a bug in the code.

```java
static String readFirstLine(String path) throws IOException {
    try (var reader = new BufferedReader(new FileReader(path))) {
        return reader.readLine();
    }
}
```

`throws IOException` passes handling to the calling code.

## Common exceptions

| Exception | When |
|---|---|
| `NullPointerException` | calling a method on `null` |
| `ArrayIndexOutOfBoundsException` | index outside the array |
| `NumberFormatException` | `Integer.parseInt("abc")` |
| `ArithmeticException` | integer division by 0 |
| `ClassCastException` | invalid type cast |
| `InputMismatchException` | `Scanner.nextInt()` got something that isn't a number |

## throw

```java
static double average(int[] marks) {
    if (marks.length == 0) {
        throw new IllegalArgumentException("The marks array is empty");
    }
    int sum = 0;
    for (int m : marks) sum += m;
    return (double) sum / marks.length;
}
```

## Custom exception

```java
class DeadlineMissedException extends Exception {
    public DeadlineMissedException(String message) {
        super(message);
    }
}
```

> [!WARNING]
> Don't leave an empty `catch (Exception e) {}` — the error vanishes without a trace and becomes very hard to find. At least call `e.printStackTrace()`.
