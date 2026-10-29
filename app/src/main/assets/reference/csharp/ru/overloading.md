# Перегрузка методов

**Перегрузка** — несколько методов с одним именем, но разными параметрами. Компилятор сам выбирает нужный вариант по аргументам вызова.

## Пример

```csharp
static int Max(int a, int b) => a > b ? a : b;

static int Max(int a, int b, int c) => Max(Max(a, b), c);

static double Max(double a, double b) => a > b ? a : b;

Console.WriteLine(Max(3, 8));       // 8 — вызван Max(int, int)
Console.WriteLine(Max(3, 8, 5));    // 8 — вызван Max(int, int, int)
Console.WriteLine(Max(2.5, 1.5));   // 2,5 — вызван Max(double, double)
```

Так устроен `Console.WriteLine`: у него есть версии для `int`, `double`, `string`, `bool` и других типов.

## Чем могут отличаться перегрузки

Методы считаются разными, если у них отличается **сигнатура**:

- количество параметров;
- типы параметров;
- порядок типов параметров;
- модификаторы `ref`, `out`, `in`.

```csharp
static void Print(int n) { }
static void Print(string s) { }
static void Print(int n, string s) { }
static void Print(string s, int n) { }
```

## Что не является перегрузкой

Тип возвращаемого значения в сигнатуру **не входит**:

```csharp
static int Parse(string s) => int.Parse(s);
static double Parse(string s) => double.Parse(s); // CS0111: метод уже определён
```

Имена параметров тоже не учитываются: `Print(int a)` и `Print(int b)` — один и тот же метод.

> [!WARNING]
> `ref` и `out` различают только с вариантом без модификатора. Методы `F(ref int x)` и `F(out int x)` одновременно объявить нельзя.

## Как выбирается перегрузка

1. Сначала ищется точное совпадение типов.
2. Если его нет — вариант, для которого нужно неявное преобразование (например, `int` → `long` → `double`).
3. Если подходят несколько вариантов одинаково хорошо, будет ошибка неоднозначного вызова CS0121.

```csharp
static void Show(long x) => Console.WriteLine("long");
static void Show(double x) => Console.WriteLine("double");

Show(5);     // long — int ближе к long, чем к double
Show(5.0);   // double
```

## Перегрузка или параметры по умолчанию

Часто вместо нескольких перегрузок достаточно одного метода с необязательными параметрами:

```csharp
// две перегрузки
static void Log(string text) => Log(text, "INFO");
static void Log(string text, string level) =>
    Console.WriteLine($"[{level}] {text}");

// то же одним методом
static void Log2(string text, string level = "INFO") =>
    Console.WriteLine($"[{level}] {text}");
```

Перегрузки нужны, когда варианты принимают **разные типы** или работают по-разному. Если меняется только значение по умолчанию — проще необязательный параметр.

## Проверь себя

> [!QUIZ]
> Можно ли перегрузить методы, отличающиеся только типом результата?
> ---
> Нет: тип результата не входит в сигнатуру, будет ошибка CS0111.

> [!QUIZ]
> Есть `Show(long)` и `Show(double)`. Какой вызовется для `Show(5)`?
> ---
> `Show(long)`: преобразование `int` → `long` лучше, чем `int` → `double`.
