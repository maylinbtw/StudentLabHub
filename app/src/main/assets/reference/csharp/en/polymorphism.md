# Polymorphism: virtual, override, abstract

**Polymorphism** means the same call behaves differently depending on the actual type of the object. In C# the base class declares a method `virtual`, and the derived class overrides it with `override`.

## virtual and override

```csharp
class Shape
{
    public virtual double Area() => 0;
}

class Circle : Shape
{
    public double R { get; init; }
    public override double Area() => Math.PI * R * R;
}

class Square : Shape
{
    public double Side { get; init; }
    public override double Area() => Side * Side;
}

Shape[] shapes = { new Circle { R = 1 }, new Square { Side = 2 } };
foreach (Shape s in shapes)
    Console.WriteLine($"{s.GetType().Name}: {s.Area():F2}");
```

```output
Circle: 3.14
Square: 4.00
```

The variable has type `Shape`, but the version from the object's actual class is called.

## base — calling the parent version

```csharp
class Person
{
    public virtual string Info() => "Person";
}

class Student : Person
{
    public override string Info() => base.Info() + ", student";
}

Console.WriteLine(new Student().Info()); // Person, student
```

## Abstract classes

If a base method has no meaningful implementation, the class and the method are declared `abstract`. You can't create an object of an abstract class, and a derived class must implement all abstract methods:

```csharp
abstract class Shape
{
    public abstract double Area();

    public void Print() => Console.WriteLine($"Area: {Area():F2}");
}

class Triangle : Shape
{
    public double A { get; init; }
    public double H { get; init; }
    public override double Area() => A * H / 2;
}

// new Shape(); — error CS0144
new Triangle { A = 4, H = 3 }.Print(); // Area: 6.00
```

## ToString

The `object` class declares a virtual `ToString()`. Override it so the object prints nicely:

```csharp
class Student
{
    public string Name { get; init; } = "";
    public override string ToString() => $"Student {Name}";
}

Console.WriteLine(new Student { Name = "Anna" }); // Student Anna
```

## override or new

| Keyword | What happens |
|---|---|
| `override` | the method is replaced everywhere, even when called through a base-type variable |
| `new` | the method only hides the base one; a base-type variable still calls the old version |

> [!WARNING]
> If a derived class declares a method with the same name without `override`, the compiler gives warning CS0114 and the method hides the base one instead of overriding it. You almost always want `override`.
