# Интерфейсы

**Интерфейс** — контракт: список методов и свойств, которые класс обязуется реализовать. Сам интерфейс (как правило) не содержит реализации и не хранит данных.

## Объявление и реализация

Имена интерфейсов принято начинать с буквы `I`:

```csharp
interface IPrintable
{
    void Print();
}

class Lab : IPrintable
{
    public string Title { get; init; } = "";
    public void Print() => Console.WriteLine($"Лабораторная: {Title}");
}

class Report : IPrintable
{
    public void Print() => Console.WriteLine("Отчёт");
}
```

Методы интерфейса в классе должны быть `public`.

## Работа через интерфейс

Переменная типа интерфейса может хранить любой объект, который его реализует:

```csharp
IPrintable[] items = { new Lab { Title = "Массивы" }, new Report() };
foreach (IPrintable item in items)
    item.Print();
```

```output
Лабораторная: Массивы
Отчёт
```

Коду, который вызывает `Print()`, неважно, какой там класс, — важно только, что он умеет печататься.

## Несколько интерфейсов

Базовый класс может быть только один, а интерфейсов — сколько угодно:

```csharp
interface IGradable
{
    int Mark { get; }
}

class Lab : IPrintable, IGradable
{
    public int Mark { get; set; }
    public void Print() => Console.WriteLine($"Оценка: {Mark}");
}
```

Если есть и базовый класс, он пишется первым: `class Student : Person, IComparable<Student>`.

## Стандартные интерфейсы

| Интерфейс | Что даёт |
|---|---|
| `IComparable<T>` | сортировка через `Array.Sort`, `List.Sort` |
| `IEquatable<T>` | своё сравнение на равенство |
| `IEnumerable<T>` | перебор через `foreach` и LINQ |
| `IDisposable` | освобождение ресурсов через `using` |

Пример с сортировкой:

```csharp
class Student : IComparable<Student>
{
    public string Name { get; init; } = "";
    public double Average { get; init; }

    public int CompareTo(Student? other) =>
        other is null ? 1 : other.Average.CompareTo(Average); // по убыванию балла
}

var list = new List<Student>
{
    new() { Name = "Анна", Average = 8.2 },
    new() { Name = "Олег", Average = 9.1 }
};
list.Sort();
Console.WriteLine(list[0].Name); // Олег
```

## Интерфейс или абстрактный класс

| | Интерфейс | Абстрактный класс |
|---|---|---|
| сколько можно унаследовать | много | один |
| поля с данными | нет | да |
| конструктор | нет | да |
| когда выбирать | общее **умение** у разных классов | общая **основа** у родственных классов |

> [!NOTE]
> С C# 8 в интерфейсе можно написать реализацию метода по умолчанию. Но обычно интерфейс оставляют чистым контрактом.

## Проверь себя

> [!QUIZ]
> Какой модификатор доступа нужен у метода, реализующего интерфейс?
> ---
> `public`.

> [!QUIZ]
> Какой интерфейс реализовать, чтобы `list.Sort()` сортировал ваши объекты?
> ---
> `IComparable<T>` с методом `CompareTo`.
