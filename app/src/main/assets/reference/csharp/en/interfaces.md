# Interfaces

An **interface** is a contract: a list of methods and properties a class promises to implement. The interface itself (as a rule) has no implementation and stores no data.

## Declaration and implementation

Interface names start with the letter `I` by convention:

```csharp
interface IPrintable
{
    void Print();
}

class Lab : IPrintable
{
    public string Title { get; init; } = "";
    public void Print() => Console.WriteLine($"Lab: {Title}");
}

class Report : IPrintable
{
    public void Print() => Console.WriteLine("Report");
}
```

Interface methods in the class must be `public`.

## Working through an interface

A variable of an interface type can hold any object that implements it:

```csharp
IPrintable[] items = { new Lab { Title = "Arrays" }, new Report() };
foreach (IPrintable item in items)
    item.Print();
```

```output
Lab: Arrays
Report
```

The code calling `Print()` doesn't care which class it is — only that it can print.

## Several interfaces

There can be only one base class, but any number of interfaces:

```csharp
interface IGradable
{
    int Mark { get; }
}

class Lab : IPrintable, IGradable
{
    public int Mark { get; set; }
    public void Print() => Console.WriteLine($"Mark: {Mark}");
}
```

If there is also a base class, it comes first: `class Student : Person, IComparable<Student>`.

## Standard interfaces

| Interface | What it gives |
|---|---|
| `IComparable<T>` | sorting with `Array.Sort`, `List.Sort` |
| `IEquatable<T>` | custom equality |
| `IEnumerable<T>` | iteration with `foreach` and LINQ |
| `IDisposable` | releasing resources with `using` |

A sorting example:

```csharp
class Student : IComparable<Student>
{
    public string Name { get; init; } = "";
    public double Average { get; init; }

    public int CompareTo(Student? other) =>
        other is null ? 1 : other.Average.CompareTo(Average); // by average, descending
}

var list = new List<Student>
{
    new() { Name = "Anna", Average = 8.2 },
    new() { Name = "Oleg", Average = 9.1 }
};
list.Sort();
Console.WriteLine(list[0].Name); // Oleg
```

## Interface or abstract class

| | Interface | Abstract class |
|---|---|---|
| how many can be inherited | many | one |
| data fields | no | yes |
| constructor | no | yes |
| when to choose | a shared **ability** of different classes | a shared **base** of related classes |

> [!NOTE]
> Since C# 8 an interface can contain a default method implementation. But usually an interface is kept a pure contract.
