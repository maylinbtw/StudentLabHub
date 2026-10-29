# JSON

**JSON** — текстовый формат для хранения и передачи данных. Его используют конфиги, веб-API и резервные копии. В .NET для него есть встроенный `System.Text.Json`.

## Как выглядит JSON

```json
{
  "name": "Анна",
  "group": "П-21",
  "marks": [8, 9, 7],
  "isActive": true
}
```

Объект — в фигурных скобках, массив — в квадратных, строки — в двойных кавычках.

## Объект → JSON

```csharp
using System.Text.Json;

record Student(string Name, string Group, int[] Marks);

var s = new Student("Анна", "П-21", new[] { 8, 9, 7 });
string json = JsonSerializer.Serialize(s);
Console.WriteLine(json);
```

```output
{"Name":"Анна","Group":"П-21","Marks":[8,9,7]}
```

По умолчанию кириллица экранируется, а отступов нет. Настройки это меняют:

```csharp
using System.Text.Encodings.Web;

var options = new JsonSerializerOptions
{
    WriteIndented = true,
    PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
    Encoder = JavaScriptEncoder.UnsafeRelaxedJsonEscaping
};

Console.WriteLine(JsonSerializer.Serialize(s, options));
```

```output
{
  "name": "Анна",
  "group": "П-21",
  "marks": [
    8,
    9,
    7
  ]
}
```

## JSON → объект

```csharp
string text = """
    { "name": "Олег", "group": "П-22", "marks": [6, 7] }
    """;

var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
Student? st = JsonSerializer.Deserialize<Student>(text, options);

Console.WriteLine($"{st?.Name}: {st?.Marks.Average()}"); // Олег: 6,5
```

> [!WARNING]
> Имена в JSON по умолчанию должны точно совпадать с именами свойств, включая регистр. Если в файле `name`, а в классе `Name`, включите `PropertyNameCaseInsensitive` или `CamelCase`-политику.

## Сохранение в файл

```csharp
var students = new List<Student>
{
    new("Анна", "П-21", new[] { 8, 9 }),
    new("Ира", "П-21", new[] { 10 })
};

File.WriteAllText("students.json", JsonSerializer.Serialize(students, options));

var loaded = JsonSerializer.Deserialize<List<Student>>(
    File.ReadAllText("students.json"), options) ?? new();
Console.WriteLine(loaded.Count); // 2
```

## Атрибуты

```csharp
using System.Text.Json.Serialization;

class Lab
{
    [JsonPropertyName("title")]
    public string Title { get; set; } = "";

    [JsonIgnore]
    public string TempNote { get; set; } = ""; // не попадёт в JSON
}
```

> [!NOTE]
> Тройные кавычки `"""..."""` (C# 11) — сырая строка: внутри не нужно экранировать кавычки. Удобно для JSON в коде.
