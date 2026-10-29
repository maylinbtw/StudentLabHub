# Arrays

An **array** stores a fixed number of elements of one type. Indexes start at zero.

## Creating

```java
int[] marks = new int[5];              // 5 zeros
int[] scores = {7, 9, 4};              // with values
String[] days = new String[]{"Mon", "Tue", "Wed"};
```

Elements of a new array get default values: `0` for numbers, `false` for `boolean`, `null` for objects.

## Access

```java
System.out.println(scores[0]);        // 7
scores[2] = 10;
System.out.println(scores.length);    // 3 — a field, no parentheses
System.out.println(scores[scores.length - 1]); // the last one
```

> [!WARNING]
> An index outside the array throws `ArrayIndexOutOfBoundsException`. The last valid index is `length - 1`.

## Iterating

```java
for (int i = 0; i < scores.length; i++) {
    System.out.println("Lab " + (i + 1) + ": " + scores[i]);
}

int sum = 0;
for (int s : scores) {
    sum += s;
}
```

## Printing an array

`System.out.println(scores)` prints something like `[I@1b6d3586` — an address, not the elements. Use `Arrays.toString`:

```java
import java.util.Arrays;

int[] a = {5, 2, 9};
System.out.println(Arrays.toString(a));   // [5, 2, 9]
```

## The Arrays class

```java
int[] a = {5, 2, 9, 1};

Arrays.sort(a);                            // [1, 2, 5, 9]
int idx = Arrays.binarySearch(a, 5);       // 2 — sorted arrays only
int[] copy = Arrays.copyOf(a, a.length);   // an independent copy
int[] part = Arrays.copyOfRange(a, 1, 3);  // [2, 5]
Arrays.fill(a, 0);                         // [0, 0, 0, 0]
boolean same = Arrays.equals(copy, part);  // false
int max = Arrays.stream(copy).max().getAsInt(); // 9
```

## Arrays are reference types

```java
int[] a = {1, 2, 3};
int[] b = a;          // the reference is copied
b[0] = 100;
System.out.println(a[0]); // 100
```

`a == b` checks whether it is the same array. Contents are compared with `Arrays.equals`.

## Two-dimensional arrays

```java
int[][] table = {
    {1, 2, 3},
    {4, 5, 6}
};

for (int[] row : table) {
    for (int x : row) {
        System.out.print(x + " ");
    }
    System.out.println();
}

System.out.println(table.length);     // 2 — rows
System.out.println(table[0].length);  // 3 — columns
```

Rows can have different lengths:

```java
int[][] jagged = new int[3][];
jagged[0] = new int[]{1};
jagged[1] = new int[]{1, 2};
jagged[2] = new int[]{1, 2, 3};
```

> [!NOTE]
> An array's size can't change. If elements are added as the program runs, use `ArrayList` — see the "OOP and collections" chapter.

## Check yourself

> [!QUIZ]
> Why doesn't `System.out.println(arr)` print the elements?
> ---
> It prints the type and hash. `Arrays.toString(arr)` prints the elements.

> [!QUIZ]
> How do you get an array's length?
> ---
> With the `length` field, no parentheses: `arr.length`.
