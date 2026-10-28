# Your first Java program

**Java** is an object-oriented language whose programs run on the **Java Virtual Machine (JVM)**. The compiler turns code into bytecode, and the JVM runs it on any OS. Java is used for servers, enterprise systems and Android apps.

## What to install

- the **JDK** (Java Development Kit) — the `javac` compiler, the JVM and the standard library. Any current LTS version will do, for example 21;
- an IDE: IntelliJ IDEA, Eclipse or VS Code with the Java extension.

```bash
java -version
javac -version
```

## The code

Create a file named `Main.java`:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

- all Java code lives inside **classes**. This class is named `Main`;
- `public static void main(String[] args)` is the **entry point** where execution starts;
- `System.out.println` prints a line and moves to the next one;
- every statement ends with `;`.

> [!WARNING]
> The name of a public class must match the file name, including case: class `Main` goes into `Main.java`. Otherwise the compiler reports an error.

## Compiling and running

```bash
javac Main.java      # produces bytecode Main.class
java Main            # runs class Main (without .class)
```

```output
Hello, World!
```

Since Java 11, a single-file program can be run without a separate compile step:

```bash
java Main.java
```

## Packages

Real projects organize classes into **packages** — folders with special names:

```java
package com.slh.labs;

public class Lab1 {
    public static void main(String[] args) {
        System.out.println("Lab 1");
    }
}
```

The file must be located at `com/slh/labs/Lab1.java`. IntelliJ IDEA creates the folders for you.

## Comments

```java
// single-line

/* multi-line */

/**
 * Javadoc — documentation for classes and methods.
 */
```

## Naming conventions

| What | Style | Example |
|---|---|---|
| Classes | PascalCase | `StudentList` |
| Methods and variables | camelCase | `getAverage`, `labCount` |
| Constants | UPPER_SNAKE_CASE | `MAX_LABS` |
| Packages | lowercase | `com.slh.labs` |
