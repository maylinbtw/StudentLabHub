# Working with files

File classes live in `System.IO` — this namespace is included by default.

## Writing and reading the whole file

The simplest way is the static methods of the `File` class:

```csharp
File.WriteAllText("note.txt", "Submit lab 3 by Friday");
string text = File.ReadAllText("note.txt");
Console.WriteLine(text);

File.AppendAllText("note.txt", "\nBring a flash drive");   // append to the end
```

Line by line:

```csharp
string[] lines = { "Anna;8", "Oleg;7", "Ira;9" };
File.WriteAllLines("marks.csv", lines);

foreach (string line in File.ReadAllLines("marks.csv"))
{
    string[] parts = line.Split(';');
    Console.WriteLine($"{parts[0]} — {parts[1]}");
}
```

```output
Anna — 8
Oleg — 7
Ira — 9
```

## Paths

A relative path is resolved from the program's working folder — usually `bin/Debug/net8.0`. Build paths with `Path` instead of concatenating strings:

```csharp
string dir = Path.Combine("data", "2026");
string file = Path.Combine(dir, "marks.csv");   // data\2026\marks.csv

Path.GetFileName(file);              // marks.csv
Path.GetExtension(file);             // .csv
Path.GetFileNameWithoutExtension(file); // marks
```

> [!NOTE]
> In a regular string the backslash must be escaped: `"C:\\data\\a.txt"`. It is easier to use a verbatim string `@"C:\data\a.txt"` or `Path.Combine`.

## Checks and folders

```csharp
if (!Directory.Exists("data"))
    Directory.CreateDirectory("data");

if (File.Exists("note.txt"))
{
    File.Copy("note.txt", Path.Combine("data", "note.txt"), overwrite: true);
    File.Delete("note.txt");
}

foreach (string path in Directory.GetFiles("data", "*.txt"))
    Console.WriteLine(path);
```

## Streams: StreamReader and StreamWriter

For large files you don't need to read everything into memory — read line by line:

```csharp
using (var writer = new StreamWriter("log.txt", append: true))
{
    writer.WriteLine($"{DateTime.Now:HH:mm} started");
}

using var reader = new StreamReader("log.txt");
string? line;
while ((line = reader.ReadLine()) != null)
    Console.WriteLine(line);
```

`using` guarantees the file is closed even if an error happens inside.

> [!WARNING]
> While a file is open through `StreamWriter`, another program (and your own code) may fail to open it. Don't forget `using`.

## File errors

| Exception | Cause |
|---|---|
| `FileNotFoundException` | the file doesn't exist |
| `DirectoryNotFoundException` | a folder in the path doesn't exist |
| `UnauthorizedAccessException` | no permission or the file is read-only |
| `IOException` | the file is used by another process, and other I/O errors |

```csharp
try
{
    string data = File.ReadAllText("config.txt");
}
catch (FileNotFoundException)
{
    Console.WriteLine("Settings file not found, using defaults");
}
```
