# HashSet, Queue, Stack

Besides lists and dictionaries, `System.Collections.Generic` has collections for common special tasks.

## HashSet<T> — a set

Stores only **unique** values and quickly checks whether an element is present:

```csharp
var groups = new HashSet<string>();

groups.Add("P-21");
groups.Add("P-22");
bool added = groups.Add("P-21");   // false — already there

Console.WriteLine(groups.Count);            // 2
Console.WriteLine(groups.Contains("P-22")); // True
```

Removing duplicates from an array:

```csharp
int[] marks = { 7, 9, 7, 4, 9 };
var unique = new HashSet<int>(marks);
Console.WriteLine(string.Join(" ", unique)); // 7 9 4
```

Set operations:

```csharp
var a = new HashSet<int> { 1, 2, 3 };
var b = new HashSet<int> { 2, 3, 4 };

a.IntersectWith(b);   // a = {2, 3} — intersection
// a.UnionWith(b);    — union
// a.ExceptWith(b);   — difference
```

## Queue<T> — a queue

First in, first out (FIFO). Like the queue to defend your labs:

```csharp
var queue = new Queue<string>();
queue.Enqueue("Anna");
queue.Enqueue("Oleg");
queue.Enqueue("Ira");

Console.WriteLine(queue.Peek());     // Anna — look at the first
Console.WriteLine(queue.Dequeue());  // Anna — take the first
Console.WriteLine(queue.Count);      // 2
```

## Stack<T> — a stack

Last in, first out (LIFO). Like a pile of notebooks: you take the top one.

```csharp
var history = new Stack<string>();
history.Push("Home");
history.Push("Subjects");
history.Push("Lab 3");

Console.WriteLine(history.Pop());   // Lab 3 — "back"
Console.WriteLine(history.Peek());  // Subjects
```

> [!WARNING]
> `Dequeue`, `Pop` and `Peek` on an empty collection throw `InvalidOperationException`. Check `Count > 0` or use `TryDequeue` / `TryPop`.

```csharp
while (queue.TryDequeue(out string? name))
    Console.WriteLine($"Now defending: {name}");
```

## Which collection to choose

| Task | Collection |
|---|---|
| ordered elements, access by index | `List<T>` |
| look up a value by key | `Dictionary<K, V>` |
| unique values only, fast "is it there" check | `HashSet<T>` |
| process items in turn | `Queue<T>` |
| undo, "back", matching brackets | `Stack<T>` |

## Check yourself

> [!QUIZ]
> Which collection fits a first-in, first-out defense queue?
> ---
> `Queue<T>`: `Enqueue` adds to the end, `Dequeue` takes the first.

> [!QUIZ]
> What does `set.Add(x)` return if `x` is already in the `HashSet`?
> ---
> `false` — the element isn't added.
