# Constructors

A **constructor** is a special method called when an object is created with `new`. Its job is to put the object into a valid initial state.

## Declaration

The constructor's name matches the class name, and no return type is written:

```csharp
class Student
{
    public string Name { get; }
    public int Course { get; }

    public Student(string name, int course)
    {
        Name = name;
        Course = course;
    }
}

var s = new Student("Anna", 2);
Console.WriteLine($"{s.Name}, year {s.Course}");
```

```output
Anna, year 2
```

## Default constructor

If a class has no constructors, the compiler adds an empty parameterless one. As soon as you declare your own, the empty one disappears:

```csharp
var s = new Student(); // CS7036: no argument for parameter name
```

## Several constructors

Constructors can be overloaded. To avoid duplicating code, one calls another with `this(...)`:

```csharp
class Lab
{
    public string Title { get; }
    public int Number { get; }

    public Lab(string title, int number)
    {
        Title = title;
        Number = number;
    }

    public Lab(string title) : this(title, 1) { }
}

var a = new Lab("Arrays", 3);
var b = new Lab("Introduction");   // Number = 1
```

## Primary constructor

In C# 12 constructor parameters can be written right after the class name:

```csharp
class Teacher(string name, string subject)
{
    public string Name => name;
    public void Introduce() => Console.WriteLine($"{name}, {subject}");
}

new Teacher("Ivanova", "Math").Introduce();
```

Primary constructor parameters are visible in the whole class body.

## record

For simple data classes, `record` is handy. The constructor, properties, value equality and `ToString` are generated automatically:

```csharp
record Mark(string Subject, int Value);

var m1 = new Mark("Physics", 8);
var m2 = new Mark("Physics", 8);

Console.WriteLine(m1);          // Mark { Subject = Physics, Value = 8 }
Console.WriteLine(m1 == m2);    // True — compared by value

var m3 = m1 with { Value = 9 }; // a copy with one field changed
```

> [!NOTE]
> For a regular class, `==` compares references: two different objects with the same fields are not equal. For a `record`, property values are compared.

## Initialization order

1. Fields get the values from their declarations (`int count = 5;`).
2. The constructor runs.
3. The object initializer `{ Name = ... }` is applied, if any.

## Check yourself

> [!QUIZ]
> What happens to the empty constructor once you declare one with parameters?
> ---
> The compiler stops generating it: `new Student()` gives error CS7036.

> [!QUIZ]
> How does `==` compare two `record`s with equal fields?
> ---
> By value — the result is `true`. For regular classes `==` compares references.
