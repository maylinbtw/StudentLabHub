# Исключения: try, catch, finally

**Исключение** — ошибка во время работы программы: деление на ноль, неверный формат числа, выход за границы массива. Если исключение не обработать, программа завершится с сообщением об ошибке.

```csharp
int a = 10, b = 0;
Console.WriteLine(a / b); // DivideByZeroException
```

## try и catch

Опасный код помещают в `try`, а реакцию на ошибку — в `catch`:

```csharp
try
{
    Console.Write("Число: ");
    int n = int.Parse(Console.ReadLine()!);
    Console.WriteLine(100 / n);
}
catch (FormatException)
{
    Console.WriteLine("Нужно ввести целое число");
}
catch (DivideByZeroException)
{
    Console.WriteLine("На ноль делить нельзя");
}
```

Когда в `try` возникает исключение, остаток блока пропускается и выполняется первый подходящий `catch`. После него программа продолжает работу.

## Объект исключения

В `catch` можно получить сам объект и узнать подробности:

```csharp
try
{
    int[] marks = { 7, 8 };
    Console.WriteLine(marks[5]);
}
catch (IndexOutOfRangeException ex)
{
    Console.WriteLine($"Ошибка: {ex.Message}");
}
```

```output
Ошибка: Index was outside the bounds of the array.
```

## Частые исключения

| Исключение | Когда возникает |
|---|---|
| `FormatException` | `int.Parse("abc")` — строка не в нужном формате |
| `DivideByZeroException` | целочисленное деление на 0 |
| `IndexOutOfRangeException` | индекс за границами массива |
| `NullReferenceException` | обращение к членам `null`-объекта |
| `OverflowException` | переполнение в блоке `checked` |
| `ArgumentException` | методу передали недопустимый аргумент |
| `FileNotFoundException` | файл не найден |

## Порядок catch

Блоки проверяются сверху вниз. Более конкретные типы пишут раньше, общий `Exception` — последним:

```csharp
try
{
    // ...
}
catch (FormatException)
{
    Console.WriteLine("Неверный формат");
}
catch (Exception ex)
{
    Console.WriteLine($"Что-то пошло не так: {ex.Message}");
}
```

> [!WARNING]
> Если поставить `catch (Exception)` первым, остальные блоки никогда не сработают — компилятор выдаст ошибку CS0160.

## finally

Блок `finally` выполняется всегда — и при ошибке, и без неё. Туда помещают освобождение ресурсов:

```csharp
StreamReader? reader = null;
try
{
    reader = new StreamReader("marks.txt");
    Console.WriteLine(reader.ReadLine());
}
catch (FileNotFoundException)
{
    Console.WriteLine("Файл не найден");
}
finally
{
    reader?.Dispose();
    Console.WriteLine("Готово");
}
```

Для файлов и других ресурсов короче использовать `using` — он сам вызовет `Dispose`:

```csharp
using var reader = new StreamReader("marks.txt");
Console.WriteLine(reader.ReadLine());
```

## throw — выбросить исключение

Метод может сам сообщить об ошибке через `throw`:

```csharp
static double Average(int[] marks)
{
    if (marks.Length == 0)
        throw new ArgumentException("Массив оценок пуст");
    return marks.Average();
}
```

## Исключения или проверки

Исключения — для неожиданных ситуаций. Если ошибка ожидаема (пользователь ввёл не число), лучше проверить заранее:

```csharp
if (int.TryParse(input, out int n))
    Console.WriteLine(n * 2);
else
    Console.WriteLine("Не число");
```

`TryParse` не выбрасывает исключение и работает быстрее, чем `try/catch` вокруг `Parse`.
