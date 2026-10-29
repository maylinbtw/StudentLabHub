# Inheritance

**Inheritance** lets you create a new class based on an existing one. The new class (the **derived** class) gets the fields, properties and methods of the base class and can add its own.

## Syntax

The base class is given after a colon:

```csharp
class Person
{
    public string Name { get; set; } = "";

    public void SayName() => Console.WriteLine($"My name is {Name}");
}

class Student : Person
{
    public string Group { get; set; } = "";
}

var s = new Student { Name = "Anna", Group = "P-21" };
s.SayName();               // method from Person
Console.WriteLine(s.Group);
```

```output
My name is Anna
P-21
```

In C# a class can have only **one** base class. All classes implicitly inherit from `object`.

## Base class constructor

Constructors are not inherited. A derived class calls the base constructor with `base(...)`:

```csharp
class Person
{
    public string Name { get; }
    public Person(string name) => Name = name;
}

class Student : Person
{
    public string Group { get; }

    public Student(string name, string group) : base(name)
    {
        Group = group;
    }
}
```

The `Person` constructor runs first, then `Student`.

## protected

`protected` members are visible in the class itself and in all derived classes, but not from outside:

```csharp
class Account
{
    protected decimal balance;
}

class SavingsAccount : Account
{
    public void AddInterest() => balance *= 1.05m; // allowed
}

// new Account().balance — error: inaccessible
```

## Type casting

A derived object can be stored in a variable of the base type:

```csharp
Person p = new Student("Oleg", "P-22");
// p.Group — not available: the variable is of type Person

if (p is Student st)
    Console.WriteLine(st.Group); // P-22
```

`is` checks the type and creates a variable of that type at once. `as` returns `null` if the cast is not possible:

```csharp
Student? maybe = p as Student;
```

## sealed

A class marked `sealed` cannot be inherited:

```csharp
sealed class Config { }
// class MyConfig : Config { } — error CS0509
```

> [!NOTE]
> Inheritance is an "is a" relationship: a student **is a** person. For a "has a" relationship (a student **has** a record book), use a field instead of a subclass.

## Check yourself

> [!QUIZ]
> How many base classes can a C# class have?
> ---
> One. Any number of interfaces, though.

> [!QUIZ]
> What does `p as Student` return if `p` isn't a student?
> ---
> `null`. The `as` operator doesn't throw.
