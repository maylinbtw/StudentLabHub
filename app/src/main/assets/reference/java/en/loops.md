# Loops

Java has four kinds of loops: `for`, the enhanced `for` (for-each), `while` and `do…while`.

## for

```java
for (int i = 1; i <= 5; i++) {
    System.out.print(i + " ");
}
System.out.println();
```

```output
1 2 3 4 5 
```

## for-each

Walks through arrays and collections without an index:

```java
int[] marks = {8, 6, 9, 7};
int sum = 0;
for (int m : marks) {
    sum += m;
}
System.out.printf("Average: %.2f%n", (double) sum / marks.length);
```

```output
Average: 7.50
```

```java
List<String> labs = List.of("Lab1", "Lab2", "Lab3");
for (String lab : labs) {
    System.out.println(lab);
}
```

> You cannot remove elements from a collection inside for-each — you get a `ConcurrentModificationException`. Use `removeIf` or an iterator.

## while and do…while

```java
int n = 1234, digits = 0;
while (n > 0) {
    n /= 10;
    digits++;
}
```

```java
int choice;
do {
    System.out.print("1 — add, 0 — exit: ");
    choice = scanner.nextInt();
} while (choice != 0);
```

## break and continue

```java
for (int i = 1; i <= 20; i++) {
    if (i % 3 != 0) continue;
    if (i > 12) break;
    System.out.print(i + " ");   // 3 6 9 12
}
```

## Labels for nested loops

Java can leave several loops at once using a **label**:

```java
outer:
for (int row = 0; row < 5; row++) {
    for (int col = 0; col < 5; col++) {
        if (grid[row][col] == target) {
            System.out.println("Found: " + row + ", " + col);
            break outer;
        }
    }
}
```

`continue outer;` jumps to the next step of the outer loop.

## Index-based and reverse iteration

```java
String[] days = {"Mon", "Tue", "Wed"};

for (int i = 0; i < days.length; i++) {
    System.out.println((i + 1) + ". " + days[i]);
}

for (int i = days.length - 1; i >= 0; i--) {
    System.out.print(days[i] + " ");   // Wed Tue Mon
}
```

## Streams instead of loops

Many loops are shorter with the Stream API:

```java
int[] marks = {8, 6, 9, 7};
double avg = Arrays.stream(marks).average().orElse(0);
long good = Arrays.stream(marks).filter(m -> m >= 8).count();   // 2
```

## Check yourself

> [!QUIZ]
> How do you exit two nested loops at once?
> ---
> Put a label before the outer loop and write `break label;`.

> [!QUIZ]
> When is a for-each loop handy?
> ---
> When you need every element of an array or collection and don't need the index.
