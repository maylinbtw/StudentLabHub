# Параметры: ref, out, in, params

По умолчанию аргумент передаётся **по значению**: метод получает копию, и изменения внутри метода не видны снаружи.

```csharp
static void AddOne(int n)
{
    n++;
}

int x = 5;
AddOne(x);
Console.WriteLine(x); // 5 — изменилась только копия
```

## ref — передача по ссылке

С `ref` метод работает с самой переменной вызывающего кода. Слово `ref` пишут и в объявлении, и при вызове:

```csharp
static void AddOne(ref int n)
{
    n++;
}

int x = 5;
AddOne(ref x);
Console.WriteLine(x); // 6
```

Классический пример — обмен значений:

```csharp
static void Swap(ref int a, ref int b)
{
    (a, b) = (b, a);
}

int first = 1, second = 2;
Swap(ref first, ref second);
Console.WriteLine($"{first} {second}"); // 2 1
```

> [!WARNING]
> Переменная, передаваемая через `ref`, должна быть инициализирована до вызова.

## out — выходной параметр

`out` позволяет вернуть из метода несколько значений. Метод **обязан** присвоить значение каждому `out`-параметру:

```csharp
static void MinMax(int[] items, out int min, out int max)
{
    min = items.Min();
    max = items.Max();
}

MinMax(new[] { 4, 9, 2, 7 }, out int lo, out int hi);
Console.WriteLine($"мин {lo}, макс {hi}");
```

```output
мин 2, макс 9
```

Переменную можно объявить прямо в вызове: `out int lo`. Так устроены методы `TryParse`:

```csharp
Console.Write("Оценка: ");
if (int.TryParse(Console.ReadLine(), out int mark))
    Console.WriteLine($"Принято: {mark}");
else
    Console.WriteLine("Это не число");
```

Если значение не нужно, вместо имени ставят `_`: `int.TryParse(text, out _)`.

## in — только для чтения

`in` передаёт аргумент по ссылке, но запрещает его менять. Полезно для больших структур, чтобы не копировать их:

```csharp
static double Length(in (double X, double Y) v)
{
    // v.X = 0; — ошибка: параметр только для чтения
    return Math.Sqrt(v.X * v.X + v.Y * v.Y);
}
```

| Модификатор | Нужна инициализация до вызова | Метод обязан присвоить | Метод может менять |
|---|---|---|---|
| нет | да | нет | только копию |
| `ref` | да | нет | да |
| `out` | нет | да | да |
| `in` | да | нет | нет |

## Необязательные параметры

Параметру можно задать значение по умолчанию. Такие параметры пишут в конце списка:

```csharp
static void Greet(string name, string greeting = "Привет")
{
    Console.WriteLine($"{greeting}, {name}!");
}

Greet("Анна");                 // Привет, Анна!
Greet("Олег", "Добрый день");  // Добрый день, Олег!
```

## Именованные аргументы

Аргумент можно передать по имени параметра — тогда порядок не важен, а вызов читается понятнее:

```csharp
static void CreateLab(string title, int number, bool done = false) { /* ... */ }

CreateLab(number: 3, title: "Массивы");
CreateLab("Строки", 4, done: true);
```

## params — переменное число аргументов

`params` позволяет передать любое количество аргументов через запятую. Внутри метода они приходят массивом:

```csharp
static int Sum(params int[] numbers)
{
    int total = 0;
    foreach (int n in numbers) total += n;
    return total;
}

Console.WriteLine(Sum());           // 0
Console.WriteLine(Sum(5));          // 5
Console.WriteLine(Sum(1, 2, 3, 4)); // 10
```

> [!NOTE]
> `params` может быть только один, и он должен стоять последним в списке параметров.
