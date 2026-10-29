# Список List<T>

`List<T>` — список, который растёт и уменьшается по мере добавления и удаления элементов. `T` — тип элементов: `List<int>`, `List<string>`, `List<Student>`.

## Создание

```csharp
var marks = new List<int>();                       // пустой
var days = new List<string> { "Пн", "Вт", "Ср" };  // со значениями
List<double> prices = [9.5, 12.0];                 // C# 12
```

## Добавление и удаление

```csharp
var subjects = new List<string> { "Математика", "Физика" };

subjects.Add("Химия");                // в конец
subjects.Insert(0, "История");        // по индексу
subjects.AddRange(new[] { "ОАиП", "БД" });

subjects.Remove("Физика");            // по значению (первое вхождение)
subjects.RemoveAt(0);                 // по индексу
subjects.RemoveAll(s => s.Length < 3);// по условию

Console.WriteLine(string.Join(", ", subjects));
```

```output
Математика, Химия, ОАиП
```

## Доступ и поиск

```csharp
var marks = new List<int> { 7, 9, 4, 9 };

Console.WriteLine(marks[0]);            // 7
Console.WriteLine(marks.Count);         // 4 — у списка Count, а не Length
Console.WriteLine(marks.Contains(4));   // True
Console.WriteLine(marks.IndexOf(9));    // 1
Console.WriteLine(marks.Find(m => m > 7));     // 9 — первый подходящий
Console.WriteLine(marks.Exists(m => m < 5));   // True
```

> [!WARNING]
> Индекс должен быть от `0` до `Count - 1`, иначе будет `ArgumentOutOfRangeException`.

## Перебор и сортировка

```csharp
var marks = new List<int> { 7, 9, 4 };

marks.Sort();          // 4 7 9
marks.Reverse();       // 9 7 4

foreach (int m in marks)
    Console.Write($"{m} ");
```

Список объектов сортируют, указывая, по чему сравнивать:

```csharp
var students = new List<Student> { /* ... */ };
students.Sort((a, b) => a.Name.CompareTo(b.Name));
```

> [!WARNING]
> Внутри `foreach` нельзя добавлять и удалять элементы того же списка — будет `InvalidOperationException`. Для удаления по условию используйте `RemoveAll` или цикл `for` с конца.

## Массив или список

| | Массив `T[]` | `List<T>` |
|---|---|---|
| размер | фиксирован | меняется |
| количество | `Length` | `Count` |
| добавление | нельзя | `Add`, `Insert` |
| когда брать | размер известен заранее | данные приходят постепенно |

Преобразования: `list.ToArray()` и `array.ToList()`.

## Проверь себя

> [!QUIZ]
> Как узнать количество элементов в `List<T>`?
> ---
> Через свойство `Count` (у массива — `Length`).

> [!QUIZ]
> Почему нельзя удалять элементы списка внутри `foreach` по нему же?
> ---
> Будет `InvalidOperationException`: коллекция изменилась во время перебора. Используйте `RemoveAll` или `for` с конца.
