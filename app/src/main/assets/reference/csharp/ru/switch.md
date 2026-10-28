# Конструкция switch

`switch` сравнивает одно значение с набором вариантов. Она удобнее длинной цепочки `else if`, когда проверяется одна и та же переменная.

## Оператор switch

```csharp
int day = 3;

switch (day)
{
    case 1:
        Console.WriteLine("Понедельник");
        break;
    case 2:
        Console.WriteLine("Вторник");
        break;
    case 3:
        Console.WriteLine("Среда");
        break;
    default:
        Console.WriteLine("Другой день");
        break;
}
```

```output
Среда
```

- после `case` указывается константа того же типа, что и проверяемое значение;
- `break` завершает выполнение `switch`;
- ветка `default` срабатывает, если ни один `case` не подошёл. Она необязательна.

> [!WARNING]
> В отличие от C++, в C# нельзя «проваливаться» из одного непустого `case` в следующий. Каждая ветка должна заканчиваться `break`, `return`, `throw` или `goto case`, иначе будет ошибка CS0163.

## Несколько значений в одной ветке

Пустые `case` можно ставить подряд — они разделят один код:

```csharp
switch (day)
{
    case 6:
    case 7:
        Console.WriteLine("Выходной");
        break;
    default:
        Console.WriteLine("Учёба");
        break;
}
```

## switch со строками

Проверять можно числа, символы, строки, перечисления:

```csharp
string command = "help";

switch (command)
{
    case "start":
        Console.WriteLine("Запуск");
        break;
    case "help":
        Console.WriteLine("Список команд");
        break;
    default:
        Console.WriteLine("Неизвестная команда");
        break;
}
```

Сравнение строк в `switch` учитывает регистр: `"Help"` не совпадёт с `"help"`. Приведите строку к одному регистру заранее: `command.ToLower()`.

## Условия в case: when

С C# 7 в `case` можно добавить дополнительное условие:

```csharp
int mark = 8;

switch (mark)
{
    case int m when m >= 9:
        Console.WriteLine("Отлично");
        break;
    case int m when m >= 4:
        Console.WriteLine("Зачтено");
        break;
    default:
        Console.WriteLine("Пересдача");
        break;
}
```

## Выражение switch

С C# 8 есть компактная форма — **выражение switch**. Она сразу возвращает значение:

```csharp
int day = 6;

string name = day switch
{
    1 => "Пн",
    2 => "Вт",
    3 => "Ср",
    4 => "Чт",
    5 => "Пт",
    6 or 7 => "Выходной",
    _ => "Ошибка"
};

Console.WriteLine(name);
```

```output
Выходной
```

- варианты разделяются запятыми, после стрелки `=>` — результат;
- `_` — вариант «всё остальное», аналог `default`;
- если ни один вариант не подошёл и `_` нет, во время работы будет исключение.

## Шаблоны сравнения

В выражении switch удобно проверять диапазоны:

```csharp
int score = 73;

string grade = score switch
{
    >= 90 => "A",
    >= 75 => "B",
    >= 60 => "C",
    _ => "F"
};

Console.WriteLine(grade); // C
```

> Шаблоны `or`, `and`, `>=` в `case` и выражениях switch появились в C# 9. В старых версиях используйте `when`.
