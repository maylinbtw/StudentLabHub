# JSON

**JSON** is a text format for storing and exchanging data. Configs, web APIs and backups use it. .NET has the built-in `System.Text.Json` for it.

## What JSON looks like

```json
{
  "name": "Anna",
  "group": "P-21",
  "marks": [8, 9, 7],
  "isActive": true
}
```

An object is in curly braces, an array in square brackets, strings in double quotes.

## Object → JSON

```csharp
using System.Text.Json;

record Student(string Name, string Group, int[] Marks);

var s = new Student("Anna", "P-21", new[] { 8, 9, 7 });
string json = JsonSerializer.Serialize(s);
Console.WriteLine(json);
```

```output
{"Name":"Anna","Group":"P-21","Marks":[8,9,7]}
```

By default there are no indents, and non-Latin letters are escaped. Options change this:

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
  "name": "Anna",
  "group": "P-21",
  "marks": [
    8,
    9,
    7
  ]
}
```

## JSON → object

```csharp
string text = """
    { "name": "Oleg", "group": "P-22", "marks": [6, 7] }
    """;

var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
Student? st = JsonSerializer.Deserialize<Student>(text, options);

Console.WriteLine($"{st?.Name}: {st?.Marks.Average()}"); // Oleg: 6.5
```

> [!WARNING]
> By default, JSON names must match property names exactly, including case. If the file has `name` and the class has `Name`, enable `PropertyNameCaseInsensitive` or the `CamelCase` policy.

## Saving to a file

```csharp
var students = new List<Student>
{
    new("Anna", "P-21", new[] { 8, 9 }),
    new("Ira", "P-21", new[] { 10 })
};

File.WriteAllText("students.json", JsonSerializer.Serialize(students, options));

var loaded = JsonSerializer.Deserialize<List<Student>>(
    File.ReadAllText("students.json"), options) ?? new();
Console.WriteLine(loaded.Count); // 2
```

## Attributes

```csharp
using System.Text.Json.Serialization;

class Lab
{
    [JsonPropertyName("title")]
    public string Title { get; set; } = "";

    [JsonIgnore]
    public string TempNote { get; set; } = ""; // won't be in the JSON
}
```

> [!NOTE]
> Triple quotes `"""..."""` (C# 11) make a raw string: quotes inside don't need escaping. Handy for JSON in code.
