# Arrays

An **array** stores several values of the same type under one name. Elements are accessed by **index** — a number that starts at zero.

## Creating an array

```csharp
int[] marks = new int[5];                 // 5 elements, all equal to 0
int[] scores = new int[] { 7, 9, 4 };     // with values right away
string[] days = { "Mon", "Tue", "Wed" };  // short form
int[] empty = [];                          // C# 12: collection expression
```

The size is set at creation and never changes. If you need a list that grows, use `List<T>`.

## Accessing elements

```csharp
string[] days = { "Mon", "Tue", "Wed" };

Console.WriteLine(days[0]);            // Mon — the first element
days[2] = "Wednesday";                 // change an element
Console.WriteLine(days.Length);        // 3 — number of elements
Console.WriteLine(days[^1]);           // Wednesday — the last element
```

> [!WARNING]
> The last index is `Length - 1`. Accessing `days[3]` in a three-element array throws `IndexOutOfRangeException`.

## Iterating: for and foreach

`for` gives you the element index:

```csharp
int[] marks = { 8, 6, 9, 7 };

for (int i = 0; i < marks.Length; i++)
{
    Console.WriteLine($"Lab {i + 1}: {marks[i]}");
}
```

`foreach` walks through all elements without an index — shorter and with no risk of going out of bounds:

```csharp
int sum = 0;
foreach (int mark in marks)
{
    sum += mark;
}
Console.WriteLine($"Average: {(double)sum / marks.Length:F2}");
```

```output
Average: 7.50
```

> Inside `foreach` you cannot assign to the loop variable (`mark = 10` is an error). Use `for` to change elements.

## Useful methods

```csharp
int[] data = { 5, 2, 9, 1 };

Array.Sort(data);                          // 1 2 5 9
Array.Reverse(data);                       // 9 5 2 1
int index = Array.IndexOf(data, 5);        // 1, or -1 if missing
bool has = data.Contains(2);               // True (System.Linq)

Console.WriteLine(string.Join(", ", data));
Console.WriteLine(data.Max());             // 9 (System.Linq)
Console.WriteLine(data.Sum());             // 17 (System.Linq)
```

`string.Join` is a quick way to print an array: `Console.WriteLine(data)` prints only the type name `System.Int32[]`.

## Arrays are reference types

Assignment copies the reference, not the elements:

```csharp
int[] a = { 1, 2, 3 };
int[] b = a;
b[0] = 100;

Console.WriteLine(a[0]);   // 100 — a and b point to the same array
```

To get an independent copy, use `(int[])a.Clone()` or `a.ToArray()`.

## Two-dimensional arrays

A rectangular table uses a comma inside the square brackets:

```csharp
int[,] table =
{
    { 1, 2, 3 },
    { 4, 5, 6 }
};

int rows = table.GetLength(0);   // 2
int cols = table.GetLength(1);   // 3

for (int r = 0; r < rows; r++)
{
    for (int c = 0; c < cols; c++)
        Console.Write($"{table[r, c]} ");
    Console.WriteLine();
}
```

```output
1 2 3 
4 5 6 
```

## Jagged arrays

A **jagged** array is an array of arrays whose rows can have different lengths:

```csharp
int[][] jagged = new int[3][];
jagged[0] = new[] { 1 };
jagged[1] = new[] { 1, 2 };
jagged[2] = new[] { 1, 2, 3 };

foreach (int[] row in jagged)
    Console.WriteLine(string.Join(" ", row));
```

```output
1
1 2
1 2 3
```
