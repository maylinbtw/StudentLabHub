# Dictionary<K, V>

`Dictionary<TKey, TValue>` stores **key → value** pairs. A value is found by its key very quickly, without iterating. Keys are unique.

## Creating and adding

```csharp
var marks = new Dictionary<string, int>
{
    ["Math"] = 9,
    ["Physics"] = 7
};

marks["Chemistry"] = 8;      // add or replace
marks.Add("History", 6);     // add only
```

> [!WARNING]
> `Add` with an existing key throws `ArgumentException`. The indexer `marks[key] = value` simply replaces the value in that case.

## Reading

```csharp
Console.WriteLine(marks["Physics"]); // 7
```

If the key is missing, `marks["Biology"]` throws `KeyNotFoundException`. Safe ways:

```csharp
if (marks.TryGetValue("Biology", out int mark))
    Console.WriteLine(mark);
else
    Console.WriteLine("No mark");

int value = marks.GetValueOrDefault("Biology", 0); // 0 if the key is missing

bool has = marks.ContainsKey("Chemistry");         // True
```

## Changing and removing

```csharp
marks["Physics"] += 1;       // 8
marks.Remove("History");
Console.WriteLine(marks.Count); // 3
```

## Iterating

Each element is a `KeyValuePair<K, V>` with `Key` and `Value` properties:

```csharp
foreach (var (subject, m) in marks)
    Console.WriteLine($"{subject}: {m}");

foreach (string subject in marks.Keys) { /* keys only */ }
foreach (int m in marks.Values) { /* values only */ }
```

```output
Math: 9
Physics: 8
Chemistry: 8
```

> [!NOTE]
> The iteration order of a `Dictionary` is not guaranteed. If you need order by key, use `SortedDictionary<K, V>`.

## Example: counting words

```csharp
string text = "to be or not to be";
var counts = new Dictionary<string, int>();

foreach (string word in text.Split(' '))
{
    counts[word] = counts.GetValueOrDefault(word) + 1;
}

foreach (var (word, n) in counts)
    Console.WriteLine($"{word}: {n}");
```

```output
to: 2
be: 2
or: 1
not: 1
```
