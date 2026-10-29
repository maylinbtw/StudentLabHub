# Полиморфизм: virtual, override, abstract

**Полиморфизм** — один и тот же вызов работает по-разному в зависимости от реального типа объекта. В C# для этого базовый класс объявляет метод `virtual`, а наследник переопределяет его через `override`.

## virtual и override

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
Circle: 3,14
Square: 4,00
```

Переменная имеет тип `Shape`, но вызывается версия метода из реального класса объекта.

## base — вызов версии родителя

```csharp
class Person
{
    public virtual string Info() => "Человек";
}

class Student : Person
{
    public override string Info() => base.Info() + ", студент";
}

Console.WriteLine(new Student().Info()); // Человек, студент
```

## Абстрактные классы

Если у базового метода нет осмысленной реализации, класс и метод объявляют `abstract`. Создать объект абстрактного класса нельзя, а наследник обязан реализовать все абстрактные методы:

```csharp
abstract class Shape
{
    public abstract double Area();

    public void Print() => Console.WriteLine($"Площадь: {Area():F2}");
}

class Triangle : Shape
{
    public double A { get; init; }
    public double H { get; init; }
    public override double Area() => A * H / 2;
}

// new Shape(); — ошибка CS0144
new Triangle { A = 4, H = 3 }.Print(); // Площадь: 6,00
```

## ToString

Класс `object` объявляет виртуальный `ToString()`. Его переопределяют, чтобы объект нормально выводился:

```csharp
class Student
{
    public string Name { get; init; } = "";
    public override string ToString() => $"Студент {Name}";
}

Console.WriteLine(new Student { Name = "Анна" }); // Студент Анна
```

## override или new

| Слово | Что происходит |
|---|---|
| `override` | метод заменяется везде, даже при вызове через переменную базового типа |
| `new` | метод только скрывает базовый; через переменную базового типа вызовется старая версия |

> [!WARNING]
> Если в наследнике написать метод с тем же именем без `override`, компилятор выдаст предупреждение CS0114 и метод будет скрывать базовый, а не переопределять. Почти всегда нужен `override`.
