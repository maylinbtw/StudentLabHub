# Наследование

**Наследование** позволяет создать новый класс на основе существующего. Новый класс (**наследник**) получает поля, свойства и методы базового и может добавить свои.

## Синтаксис

Базовый класс указывают через двоеточие:

```csharp
class Person
{
    public string Name { get; set; } = "";

    public void SayName() => Console.WriteLine($"Меня зовут {Name}");
}

class Student : Person
{
    public string Group { get; set; } = "";
}

var s = new Student { Name = "Анна", Group = "П-21" };
s.SayName();               // метод из Person
Console.WriteLine(s.Group);
```

```output
Меня зовут Анна
П-21
```

В C# у класса может быть только **один** базовый класс. Все классы неявно наследуют `object`.

## Конструктор базового класса

Конструкторы не наследуются. Наследник вызывает конструктор базового класса через `base(...)`:

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

Сначала выполняется конструктор `Person`, потом — `Student`.

## protected

Члены с `protected` видны в самом классе и во всех наследниках, но недоступны снаружи:

```csharp
class Account
{
    protected decimal balance;
}

class SavingsAccount : Account
{
    public void AddInterest() => balance *= 1.05m; // можно
}

// new Account().balance — ошибка: недоступно
```

## Приведение типов

Объект наследника можно хранить в переменной базового типа:

```csharp
Person p = new Student("Олег", "П-22");
// p.Group — недоступно: переменная типа Person

if (p is Student st)
    Console.WriteLine(st.Group); // П-22
```

`is` проверяет тип и сразу создаёт переменную нужного типа. `as` возвращает `null`, если привести нельзя:

```csharp
Student? maybe = p as Student;
```

## sealed

Класс со словом `sealed` нельзя наследовать:

```csharp
sealed class Config { }
// class MyConfig : Config { } — ошибка CS0509
```

> [!NOTE]
> Наследование — отношение «является»: студент **является** человеком. Если отношение «имеет» (у студента **есть** зачётка), лучше сделать поле, а не наследника.
