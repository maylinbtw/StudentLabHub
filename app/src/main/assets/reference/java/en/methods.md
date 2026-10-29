# Methods

A **method** is a named block of code inside a class. Java has no functions outside classes: everything is a method.

## Declaring and calling

```java
public class Main {

    static double average(int a, int b, int c) {
        return (a + b + c) / 3.0;
    }

    static void printLine(int width) {
        System.out.println("-".repeat(width));
    }

    public static void main(String[] args) {
        printLine(20);
        System.out.println("Average: " + average(8, 9, 7));
        printLine(20);
    }
}
```

```output
--------------------
Average: 8.0
--------------------
```

| Part | Meaning |
|---|---|
| `static` | a class method, called without creating an object |
| `double` / `void` | result type; `void` returns nothing |
| `average` | name, written in camelCase by convention |
| `(int a, ...)` | parameters |

Method order in a class doesn't matter: `main` can be above or below the methods it calls.

## return

`return` returns a value and ends the method immediately. Every path must end with `return`:

```java
static String grade(int mark) {
    if (mark >= 9) return "excellent";
    if (mark >= 6) return "good";
    if (mark >= 4) return "pass";
    return "fail";
}
```

Without the last line you get the compile error `missing return statement`.

## Passing arguments

In Java arguments are always passed **by value**. For primitives the number itself is copied — the method won't change the original variable:

```java
static void addOne(int n) {
    n++;
}

int x = 5;
addOne(x);
System.out.println(x); // 5
```

For objects and arrays the **reference** is copied. A method can change the object's contents but can't replace the caller's object:

```java
static void fill(int[] arr) {
    arr[0] = 100;          // visible outside too
    arr = new int[3];      // nothing changes outside
}
```

## Overloading

Methods with the same name and different parameters:

```java
static int max(int a, int b) { return a > b ? a : b; }
static double max(double a, double b) { return a > b ? a : b; }
static int max(int a, int b, int c) { return max(max(a, b), c); }
```

The return type doesn't take part in choosing an overload.

## Variable number of arguments

```java
static int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) total += n;
    return total;
}

sum();           // 0
sum(1, 2, 3);    // 6
```

Inside the method `numbers` is a regular array. There can be only one such parameter, and it must be last.

## Recursion

```java
static long factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

System.out.println(factorial(10)); // 3628800
```

> [!WARNING]
> Without a base case, recursion ends with `StackOverflowError`.

## Check yourself

> [!QUIZ]
> Does the array change outside if a method does `arr[0] = 100`?
> ---
> Yes: the reference is copied, so the method changes the same array.

> [!QUIZ]
> What does `int... numbers` mean in parameters?
> ---
> A variable number of arguments: inside the method it's a regular `int[]`.
