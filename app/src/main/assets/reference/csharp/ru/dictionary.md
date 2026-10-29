# Словарь Dictionary<K, V>

`Dictionary<TKey, TValue>` хранит пары **ключ → значение**. По ключу значение находится очень быстро, без перебора. Ключи уникальны.

## Создание и добавление

```csharp
var marks = new Dictionary<string, int>
{
    ["Математика"] = 9,
    ["Физика"] = 7
};

marks["Химия"] = 8;          // добавить или заменить
marks.Add("История", 6);     // только добавить
```

> [!WARNING]
> `Add` с уже существующим ключом выбросит `ArgumentException`. Индексатор `marks[key] = value` в этом случае просто заменит значение.

## Чтение

```csharp
Console.WriteLine(marks["Физика"]); // 7
```

Если ключа нет, `marks["Биология"]` выбросит `KeyNotFoundException`. Безопасные способы:

```csharp
if (marks.TryGetValue("Биология", out int mark))
    Console.WriteLine(mark);
else
    Console.WriteLine("Нет оценки");

int value = marks.GetValueOrDefault("Биология", 0); // 0, если нет ключа

bool has = marks.ContainsKey("Химия");              // True
```

## Изменение и удаление

```csharp
marks["Физика"] += 1;        // 8
marks.Remove("История");
Console.WriteLine(marks.Count); // 3
```

## Перебор

Каждый элемент — пара `KeyValuePair<K, V>` со свойствами `Key` и `Value`:

```csharp
foreach (var (subject, m) in marks)
    Console.WriteLine($"{subject}: {m}");

foreach (string subject in marks.Keys) { /* только ключи */ }
foreach (int m in marks.Values) { /* только значения */ }
```

```output
Математика: 9
Физика: 8
Химия: 8
```

> [!NOTE]
> Порядок перебора `Dictionary` не гарантирован. Если нужен порядок по ключу, используйте `SortedDictionary<K, V>`.

## Пример: подсчёт частоты

```csharp
string text = "мама мыла раму мама";
var counts = new Dictionary<string, int>();

foreach (string word in text.Split(' '))
{
    counts[word] = counts.GetValueOrDefault(word) + 1;
}

foreach (var (word, n) in counts)
    Console.WriteLine($"{word}: {n}");
```

```output
мама: 2
мыла: 1
раму: 1
```
