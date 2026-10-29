# Classes and objects

A **class** describes a new type: what data it stores (**fields**) and what it can do (**methods**). An **object** is a concrete instance created from that description.

## Declaring a class

```csharp
class Student
{
    public string Name = "";
    public int Course;

    public void Introduce()
    {
        Console.WriteLine($"I'm {Name}, year {Course}");
    }
}
```

A class usually lives in its own file with the same name: `Student.cs`.

> [!NOTE]
> In a program with top-level statements, classes are declared after the main code or in separate files. In this chapter's examples a class and the code using it are shown together for brevity.

## Creating an object

An object is created with `new`. Fields and methods are accessed with a dot:

```csharp
Student anna = new Student();
anna.Name = "Anna";
anna.Course = 2;
anna.Introduce();

var oleg = new Student { Name = "Oleg", Course = 3 }; // object initializer
oleg.Introduce();
```

```output
I'm Anna, year 2
I'm Oleg, year 3
```

The short form `Student s = new();` also works — the type is taken from the left side.

## Access modifiers

| Modifier | Who can see it |
|---|---|
| `public` | all code |
| `private` | only the class itself (default for class members) |
| `protected` | the class and its subclasses |
| `internal` | code in the same project (default for a class) |

Fields are usually `private`, and methods and properties are exposed instead — the object controls its own data. This is called **encapsulation**.

```csharp
class Counter
{
    private int value;

    public void Increment() => value++;
    public int Get() => value;
}
```

## this

Inside a method, `this` refers to the current object. It is needed when a parameter has the same name as a field:

```csharp
class Lab
{
    private string title = "";

    public void SetTitle(string title)
    {
        this.title = title;
    }
}
```

## Classes are reference types

A variable stores a reference to the object. Assignment copies the reference, and there is still one object:

```csharp
var a = new Student { Name = "Anna" };
var b = a;
b.Name = "Ira";
Console.WriteLine(a.Name); // Ira
```

> [!WARNING]
> If a variable is `null`, accessing its members throws `NullReferenceException`: `Student s = null; s.Introduce();`.

## Static members

Members marked `static` belong to the class as a whole, not to a single object:

```csharp
class Student
{
    public static int Count;
    public string Name = "";

    public Student() => Count++;
}

new Student();
new Student();
Console.WriteLine(Student.Count); // 2
```

This is how `Math.Sqrt`, `Console.WriteLine` and `int.Parse` work — they are called through the class name.
