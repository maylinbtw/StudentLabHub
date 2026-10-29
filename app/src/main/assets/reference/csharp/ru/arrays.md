# Массивы

**Массив** хранит несколько значений одного типа под одним именем. К элементам обращаются по **индексу** — номеру, который начинается с нуля.

## Создание массива

```csharp
int[] marks = new int[5];                 // 5 элементов, все равны 0
int[] scores = new int[] { 7, 9, 4 };     // сразу со значениями
string[] days = { "Пн", "Вт", "Ср" };     // короткая запись
int[] empty = [];                          // C# 12: выражение коллекции
```

Размер массива задаётся при создании и потом не меняется. Если нужен список, который растёт, используют `List<T>`.

## Доступ к элементам

```csharp
string[] days = { "Пн", "Вт", "Ср" };

Console.WriteLine(days[0]);            // Пн — первый элемент
days[2] = "Среда";                     // изменить элемент
Console.WriteLine(days.Length);        // 3 — количество элементов
Console.WriteLine(days[^1]);           // Среда — последний элемент
```

> [!WARNING]
> Последний индекс равен `Length - 1`. Обращение `days[3]` к массиву из трёх элементов выбросит `IndexOutOfRangeException`.

## Перебор: for и foreach

С `for` доступен индекс элемента:

```csharp
int[] marks = { 8, 6, 9, 7 };

for (int i = 0; i < marks.Length; i++)
{
    Console.WriteLine($"Работа {i + 1}: {marks[i]}");
}
```

`foreach` проходит по всем элементам без индекса — короче и без риска выйти за границы:

```csharp
int sum = 0;
foreach (int mark in marks)
{
    sum += mark;
}
Console.WriteLine($"Средний балл: {(double)sum / marks.Length:F2}");
```

```output
Средний балл: 7,50
```

> Внутри `foreach` нельзя присвоить новое значение переменной цикла (`mark = 10` — ошибка). Чтобы менять элементы, используйте `for`.

## Полезные методы

```csharp
int[] data = { 5, 2, 9, 1 };

Array.Sort(data);                          // 1 2 5 9
Array.Reverse(data);                       // 9 5 2 1
int index = Array.IndexOf(data, 5);        // 1, или -1 если нет
bool has = data.Contains(2);               // True (System.Linq)

Console.WriteLine(string.Join(", ", data));
Console.WriteLine(data.Max());             // 9 (System.Linq)
Console.WriteLine(data.Sum());             // 17 (System.Linq)
```

`string.Join` — быстрый способ вывести массив: `Console.WriteLine(data)` напечатает только имя типа `System.Int32[]`.

## Массивы — ссылочный тип

При присваивании копируется ссылка, а не сами элементы:

```csharp
int[] a = { 1, 2, 3 };
int[] b = a;
b[0] = 100;

Console.WriteLine(a[0]);   // 100 — a и b указывают на один массив
```

Чтобы получить независимую копию, используйте `(int[])a.Clone()` или `a.ToArray()`.

## Двумерные массивы

Прямоугольная таблица задаётся через запятую в квадратных скобках:

```csharp
int[,] table =
{
    { 1, 2, 3 },
    { 4, 5, 6 }
};

int rows = table.GetLength(0);   // 2
int cols = table.GetLength(1);   // 3

for (int r = 0; r < rows; r++)
{
    for (int c = 0; c < cols; c++)
        Console.Write($"{table[r, c]} ");
    Console.WriteLine();
}
```

```output
1 2 3 
4 5 6 
```

## Зубчатые массивы

**Зубчатый** массив — массив массивов, где строки могут быть разной длины:

```csharp
int[][] jagged = new int[3][];
jagged[0] = new[] { 1 };
jagged[1] = new[] { 1, 2 };
jagged[2] = new[] { 1, 2, 3 };

foreach (int[] row in jagged)
    Console.WriteLine(string.Join(" ", row));
```

```output
1
1 2
1 2 3
```

## Проверь себя

> [!QUIZ]
> Какой индекс у последнего элемента массива `a`?
> ---
> `a.Length - 1` или короче `a[^1]`.

> [!QUIZ]
> `int[] b = a; b[0] = 100;` — изменится ли `a[0]`?
> ---
> Да. Массив — ссылочный тип: `a` и `b` указывают на один массив. Копию дают `a.Clone()` или `a.ToArray()`.
