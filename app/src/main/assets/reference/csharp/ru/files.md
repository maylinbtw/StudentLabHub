# Работа с файлами

Классы для файлов находятся в `System.IO` — это пространство имён подключено по умолчанию.

## Записать и прочитать целиком

Самый простой способ — статические методы класса `File`:

```csharp
File.WriteAllText("note.txt", "Сдать лабу 3 до пятницы");
string text = File.ReadAllText("note.txt");
Console.WriteLine(text);

File.AppendAllText("note.txt", "\nВзять флешку");   // дописать в конец
```

Построчно:

```csharp
string[] lines = { "Анна;8", "Олег;7", "Ира;9" };
File.WriteAllLines("marks.csv", lines);

foreach (string line in File.ReadAllLines("marks.csv"))
{
    string[] parts = line.Split(';');
    Console.WriteLine($"{parts[0]} — {parts[1]}");
}
```

```output
Анна — 8
Олег — 7
Ира — 9
```

## Пути

Относительный путь считается от рабочей папки программы — обычно это `bin/Debug/net8.0`. Для сборки путей используют `Path`, а не склеивание строк:

```csharp
string dir = Path.Combine("data", "2026");
string file = Path.Combine(dir, "marks.csv");   // data\2026\marks.csv

Path.GetFileName(file);              // marks.csv
Path.GetExtension(file);             // .csv
Path.GetFileNameWithoutExtension(file); // marks
```

> [!NOTE]
> В обычной строке обратный слэш нужно экранировать: `"C:\\data\\a.txt"`. Проще использовать дословную строку `@"C:\data\a.txt"` или `Path.Combine`.

## Проверки и папки

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

## Потоки: StreamReader и StreamWriter

Для больших файлов не нужно читать всё в память — читают по строке:

```csharp
using (var writer = new StreamWriter("log.txt", append: true))
{
    writer.WriteLine($"{DateTime.Now:HH:mm} запуск");
}

using var reader = new StreamReader("log.txt");
string? line;
while ((line = reader.ReadLine()) != null)
    Console.WriteLine(line);
```

`using` гарантирует, что файл закроется, даже если внутри произойдёт ошибка.

> [!WARNING]
> Пока файл открыт через `StreamWriter`, другая программа (и ваш же код) может не суметь его открыть. Не забывайте `using`.

## Ошибки при работе с файлами

| Исключение | Причина |
|---|---|
| `FileNotFoundException` | файла нет |
| `DirectoryNotFoundException` | нет папки из пути |
| `UnauthorizedAccessException` | нет прав или файл только для чтения |
| `IOException` | файл занят другим процессом и прочие ошибки ввода-вывода |

```csharp
try
{
    string data = File.ReadAllText("config.txt");
}
catch (FileNotFoundException)
{
    Console.WriteLine("Файл настроек не найден, используются значения по умолчанию");
}
```

## Проверь себя

> [!QUIZ]
> Зачем писать `using` при работе со `StreamWriter`?
> ---
> Чтобы файл гарантированно закрылся, даже если внутри произойдёт ошибка.

> [!QUIZ]
> Как правильно собрать путь из папки и имени файла?
> ---
> `Path.Combine(dir, "marks.csv")` — он сам поставит нужный разделитель.
