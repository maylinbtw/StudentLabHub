# Properties

A **property** looks like a field from outside, but inside it consists of `get` and `set` accessors. This lets you validate values and hide how the class works.

## Auto-properties

The most common form — the compiler creates a hidden field for you:

```csharp
class Student
{
    public string Name { get; set; } = "";
    public int Course { get; set; }
}

var s = new Student { Name = "Anna", Course = 2 };
Console.WriteLine(s.Name);
```

In C#, public data is normally exposed as properties, not fields.

## Property with validation

To validate a value, write the property with a field by hand. In `set` the new value is available as `value`:

```csharp
class Lab
{
    private int mark;

    public int Mark
    {
        get => mark;
        set
        {
            if (value < 0 || value > 10)
                throw new ArgumentOutOfRangeException(nameof(value), "Mark must be 0 to 10");
            mark = value;
        }
    }
}

var lab = new Lab();
lab.Mark = 9;     // ok
lab.Mark = 15;    // exception
```

## Read-only

| Form | Who can change it |
|---|---|
| `{ get; set; }` | everyone |
| `{ get; private set; }` | only the class itself |
| `{ get; init; }` | only when the object is created |
| `{ get; }` | only the constructor |

```csharp
class Subject
{
    public string Title { get; init; } = "";
    public int Hours { get; private set; }

    public void AddHours(int h) => Hours += h;
}

var math = new Subject { Title = "Math" };
// math.Title = "Physics"; — error: init-only property
math.AddHours(2);
```

## Computed properties

A property may not store a value but compute it on every access:

```csharp
class Rectangle
{
    public double Width { get; set; }
    public double Height { get; set; }

    public double Area => Width * Height;
}

var r = new Rectangle { Width = 3, Height = 4 };
Console.WriteLine(r.Area); // 12
```

## required

The `required` keyword (C# 11) forces the property to be set when the object is created:

```csharp
class Group
{
    public required string Name { get; init; }
}

var g = new Group { Name = "P-21" };
// var e = new Group(); — error CS9035: Name is not set
```

> [!NOTE]
> A get-only property and a parameterless method look similar. Rule of thumb: if it is a characteristic of the object and is quick to compute — a property; if it is an action or a slow operation — a method.

## Check yourself

> [!QUIZ]
> What does `{ get; init; }` mean?
> ---
> The property can be set only when the object is created (constructor or initializer), then only read.

> [!QUIZ]
> How do you get the assigned value inside `set`?
> ---
> With the `value` keyword.
