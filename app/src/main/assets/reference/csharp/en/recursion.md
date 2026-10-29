# Recursion

**Recursion** is when a method calls itself. A task is reduced to the same but smaller task until it reaches a simple case.

## Base case and step

A recursive method always has two parts:

- **base case** — a condition where the method returns an answer without another call;
- **recursive step** — a call to itself with an argument closer to the base case.

## Factorial

`n! = n × (n − 1)!`, and `0! = 1`:

```csharp
static long Factorial(int n)
{
    if (n <= 1) return 1;          // base case
    return n * Factorial(n - 1);   // recursive step
}

Console.WriteLine(Factorial(5));
```

```output
120
```

How it is computed:

```text
Factorial(5) = 5 * Factorial(4)
             = 5 * 4 * Factorial(3)
             = 5 * 4 * 3 * Factorial(2)
             = 5 * 4 * 3 * 2 * Factorial(1)
             = 5 * 4 * 3 * 2 * 1
             = 120
```

## Sum of digits

```csharp
static int DigitSum(int n)
{
    if (n < 10) return n;
    return n % 10 + DigitSum(n / 10);
}

Console.WriteLine(DigitSum(4096)); // 19
```

## Call stack

Each method call takes space on the **call stack**: its parameters and local variables live there. The space is freed only when the call finishes.

> [!WARNING]
> If there is no base case or it is unreachable, calls go on forever, the stack overflows and the program crashes with `StackOverflowException`. This exception cannot be caught with `try/catch`.

```csharp
static int Broken(int n) => Broken(n - 1); // no base case
```

## Fibonacci numbers

Each number is the sum of the two before it: 0, 1, 1, 2, 3, 5, 8…

```csharp
static long Fib(int n)
{
    if (n < 2) return n;
    return Fib(n - 1) + Fib(n - 2);
}
```

The code is short but slow: `Fib(n - 2)` is computed again inside `Fib(n - 1)`, and the number of calls grows exponentially. `Fib(45)` takes several seconds.

The same result with a loop is instant:

```csharp
static long FibLoop(int n)
{
    long a = 0, b = 1;
    for (int i = 0; i < n; i++)
        (a, b) = (b, a + b);
    return a;
}
```

## When to use recursion

| Good fit | Better a loop |
|---|---|
| walking trees and nested folders | a simple counter, iterating an array |
| divide and conquer: quicksort, binary search | deep call chains (thousands or more) |
| tasks naturally defined in terms of themselves | repeating the same computations |

An example where recursion is handy — counting files in all nested folders:

```csharp
static int CountFiles(string path)
{
    int count = Directory.GetFiles(path).Length;
    foreach (string dir in Directory.GetDirectories(path))
        count += CountFiles(dir);
    return count;
}
```

## Check yourself

> [!QUIZ]
> What happens to a recursive method with no base case?
> ---
> Calls go on forever, the stack overflows and the program crashes with `StackOverflowException`.

> [!QUIZ]
> Why is recursive `Fib(n)` slow?
> ---
> The same values are computed many times, and the number of calls grows exponentially. A loop takes linear time.
