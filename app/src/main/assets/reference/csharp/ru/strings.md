# Работа со строками

Тип `string` хранит текст как последовательность символов `char`. У строк много готовых методов: поиск, замена, разбиение, смена регистра.

## Строки неизменяемы

Любой метод, который «меняет» строку, на самом деле возвращает **новую** строку. Исходная остаётся прежней:

```csharp
string group = "пзт-44";
group.ToUpper();                 // результат потерян
Console.WriteLine(group);        // пзт-44

group = group.ToUpper();         // сохраняем результат
Console.WriteLine(group);        // ПЗТ-44
```

## Длина и символы

```csharp
string word = "Защита";

Console.WriteLine(word.Length);     // 6
Console.WriteLine(word[0]);         // З
Console.WriteLine(word[^1]);        // а — последний символ

foreach (char c in word)
    Console.Write($"{c} ");         // З а щ и т а
```

Индекс работает только на чтение: `word[0] = 'з'` — ошибка компиляции.

## Основные методы

| Метод | Что делает | Пример → результат |
|---|---|---|
| `ToUpper()` / `ToLower()` | меняет регистр | `"Лаб".ToUpper()` → `"ЛАБ"` |
| `Trim()` | убирает пробелы по краям | `"  да  ".Trim()` → `"да"` |
| `Contains(s)` | есть ли подстрока | `"ЛР №5".Contains("№")` → True |
| `StartsWith(s)` / `EndsWith(s)` | начало / конец | `"main.cs".EndsWith(".cs")` → True |
| `IndexOf(s)` | позиция подстроки или −1 | `"abcabc".IndexOf("c")` → 2 |
| `Substring(i, n)` | часть строки | `"Привет".Substring(1, 3)` → `"рив"` |
| `Replace(a, b)` | замена | `"1-2-3".Replace("-", "+")` → `"1+2+3"` |
| `Split(c)` | разбить на массив | `"a,b,c".Split(',')` → 3 элемента |
| `Insert(i, s)` / `Remove(i, n)` | вставить / удалить | `"ab".Insert(1, "X")` → `"aXb"` |
| `PadLeft(n)` / `PadRight(n)` | дополнить до длины | `"7".PadLeft(3, '0')` → `"007"` |

## Разбиение и объединение

`Split` превращает строку в массив, `string.Join` — обратно:

```csharp
string line = "Иванов;9;8;10";
string[] parts = line.Split(';');

string surname = parts[0];
int first = int.Parse(parts[1]);

Console.WriteLine($"{surname}: {first}");
Console.WriteLine(string.Join(" | ", parts));
```

```output
Иванов: 9
Иванов | 9 | 8 | 10
```

Лишние пустые элементы можно отбросить: `text.Split(' ', StringSplitOptions.RemoveEmptyEntries)`.

## Сравнение строк

```csharp
string a = "Linux", b = "linux";

Console.WriteLine(a == b);                                         // False
Console.WriteLine(string.Equals(a, b, StringComparison.OrdinalIgnoreCase)); // True
Console.WriteLine(string.Compare("apple", "banana"));              // отрицательное число
Console.WriteLine(string.IsNullOrWhiteSpace("   "));               // True
```

`string.IsNullOrEmpty` и `string.IsNullOrWhiteSpace` удобно применять для проверки ввода.

## Особые литералы

```csharp
string path = @"C:\Users\student\Labs";       // дословная строка: \ не экранирует
string multi = """
    Многострочный текст
    без экранирования "кавычек"
    """;                                       // C# 11: сырой литерал
string name = "Анна";
string greet = $"Привет, {name}!";             // интерполяция
string both = $@"Папка: {path}\{name}";        // интерполяция + дословная
```

## StringBuilder

Каждое `+=` создаёт новую строку. Если собирать текст в цикле из тысяч кусочков, это медленно. Для этого есть `StringBuilder`:

```csharp
using System.Text;

var builder = new StringBuilder();
for (int i = 1; i <= 5; i++)
{
    builder.Append(i).Append(' ');
}
builder.AppendLine();
builder.Append("готово");

string result = builder.ToString();
Console.WriteLine(result);
```

```output
1 2 3 4 5 
готово
```

> Для нескольких склеек `+` и интерполяция вполне подходят. `StringBuilder` нужен, когда строка собирается в цикле.
