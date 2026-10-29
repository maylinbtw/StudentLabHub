# Свойства

**Свойство** выглядит снаружи как поле, но внутри состоит из методов доступа `get` и `set`. Так можно проверять значения и скрывать устройство класса.

## Автосвойства

Самая частая форма — компилятор сам создаёт скрытое поле:

```csharp
class Student
{
    public string Name { get; set; } = "";
    public int Course { get; set; }
}

var s = new Student { Name = "Анна", Course = 2 };
Console.WriteLine(s.Name);
```

В C# публичные данные принято оформлять свойствами, а не полями.

## Свойство с проверкой

Если нужно проверить значение, свойство пишут с полем вручную. В `set` новое значение доступно как `value`:

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
                throw new ArgumentOutOfRangeException(nameof(value), "Оценка от 0 до 10");
            mark = value;
        }
    }
}

var lab = new Lab();
lab.Mark = 9;     // ок
lab.Mark = 15;    // исключение
```

## Только для чтения

| Запись | Кто может менять |
|---|---|
| `{ get; set; }` | все |
| `{ get; private set; }` | только сам класс |
| `{ get; init; }` | только при создании объекта |
| `{ get; }` | только конструктор |

```csharp
class Subject
{
    public string Title { get; init; } = "";
    public int Hours { get; private set; }

    public void AddHours(int h) => Hours += h;
}

var math = new Subject { Title = "Математика" };
// math.Title = "Физика"; — ошибка: init-свойство
math.AddHours(2);
```

## Вычисляемые свойства

Свойство может не хранить значение, а вычислять его при каждом обращении:

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

Слово `required` (C# 11) заставляет задать свойство при создании объекта:

```csharp
class Group
{
    public required string Name { get; init; }
}

var g = new Group { Name = "П-21" };
// var e = new Group(); — ошибка CS9035: не задано Name
```

> [!NOTE]
> Свойства с `get` без `set` и методы без параметров похожи. Правило: если это характеристика объекта и вычисляется быстро — свойство; если действие или долгая операция — метод.

## Проверь себя

> [!QUIZ]
> Что означает `{ get; init; }`?
> ---
> Свойство можно задать только при создании объекта (в конструкторе или инициализаторе), потом — только читать.

> [!QUIZ]
> Как в `set` получить присваиваемое значение?
> ---
> Через ключевое слово `value`.
