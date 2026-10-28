# Conditions: if and switch

## if, else if, else

```java
int mark = scanner.nextInt();

if (mark >= 9) {
    System.out.println("Excellent");
} else if (mark >= 4) {
    System.out.println("Passed");
} else {
    System.out.println("Retake");
}
```

In Java, the opening brace is conventionally placed on the same line as the condition.

> The condition must be a `boolean`. `if (count)` for an integer `count` does not compile — write `if (count > 0)`.

## The classic switch

```java
int day = 3;

switch (day) {
    case 1:
        System.out.println("Monday");
        break;
    case 6:
    case 7:
        System.out.println("Weekend");
        break;
    default:
        System.out.println("Weekday");
}
```

`switch` works with `int`, `char`, `String` and `enum` values.

> [!WARNING]
> Without `break`, execution falls through into the next `case` — a frequent bug in lab assignments.

## Arrow switch (Java 14+)

The new form never falls through and can return a value:

```java
String name = switch (day) {
    case 1 -> "Mon";
    case 2 -> "Tue";
    case 3 -> "Wed";
    case 4 -> "Thu";
    case 5 -> "Fri";
    case 6, 7 -> "Weekend";
    default -> "Error";
};
```

When a branch needs several statements, use a block and `yield`:

```java
int points = switch (grade) {
    case "A" -> 10;
    case "B" -> 8;
    default -> {
        System.out.println("Unknown grade: " + grade);
        yield 0;
    }
};
```

## switch on strings

```java
String command = scanner.nextLine().trim().toLowerCase();

switch (command) {
    case "add" -> addLab();
    case "list" -> printLabs();
    case "exit" -> System.exit(0);
    default -> System.out.println("Unknown command");
}
```

## Pattern matching (Java 21)

`switch` can check an object's type and cast it right away:

```java
Object value = 42;

String text = switch (value) {
    case Integer i when i > 10 -> "large number " + i;
    case Integer i -> "number " + i;
    case String s -> "string " + s;
    default -> "something else";
};
```

The same works with `if`:

```java
if (value instanceof String s && !s.isEmpty()) {
    System.out.println(s.length());
}
```
