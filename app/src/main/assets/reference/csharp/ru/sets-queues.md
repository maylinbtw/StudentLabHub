# HashSet, Queue, Stack

Кроме списка и словаря, в `System.Collections.Generic` есть коллекции для частых особых задач.

## HashSet<T> — множество

Хранит только **уникальные** значения и быстро проверяет, есть ли элемент:

```csharp
var groups = new HashSet<string>();

groups.Add("П-21");
groups.Add("П-22");
bool added = groups.Add("П-21");   // false — уже есть

Console.WriteLine(groups.Count);            // 2
Console.WriteLine(groups.Contains("П-22")); // True
```

Убрать повторы из массива:

```csharp
int[] marks = { 7, 9, 7, 4, 9 };
var unique = new HashSet<int>(marks);
Console.WriteLine(string.Join(" ", unique)); // 7 9 4
```

Операции над множествами:

```csharp
var a = new HashSet<int> { 1, 2, 3 };
var b = new HashSet<int> { 2, 3, 4 };

a.IntersectWith(b);   // a = {2, 3} — пересечение
// a.UnionWith(b);    — объединение
// a.ExceptWith(b);   — разность
```

## Queue<T> — очередь

Первым пришёл — первым вышел (FIFO). Как очередь на защиту лабораторных:

```csharp
var queue = new Queue<string>();
queue.Enqueue("Анна");
queue.Enqueue("Олег");
queue.Enqueue("Ира");

Console.WriteLine(queue.Peek());     // Анна — посмотреть первого
Console.WriteLine(queue.Dequeue());  // Анна — забрать первого
Console.WriteLine(queue.Count);      // 2
```

## Stack<T> — стек

Последним пришёл — первым вышел (LIFO). Как стопка тетрадей: берут верхнюю.

```csharp
var history = new Stack<string>();
history.Push("Главная");
history.Push("Предметы");
history.Push("Лабораторная 3");

Console.WriteLine(history.Pop());   // Лабораторная 3 — «назад»
Console.WriteLine(history.Peek());  // Предметы
```

> [!WARNING]
> `Dequeue`, `Pop` и `Peek` на пустой коллекции выбрасывают `InvalidOperationException`. Проверяйте `Count > 0` или используйте `TryDequeue` / `TryPop`.

```csharp
while (queue.TryDequeue(out string? name))
    Console.WriteLine($"Защищается {name}");
```

## Какую коллекцию выбрать

| Задача | Коллекция |
|---|---|
| набор элементов по порядку, доступ по индексу | `List<T>` |
| поиск значения по ключу | `Dictionary<K, V>` |
| только уникальные значения, быстрая проверка «есть ли» | `HashSet<T>` |
| обработка по очереди | `Queue<T>` |
| отмена действий, «назад», разбор скобок | `Stack<T>` |

## Проверь себя

> [!QUIZ]
> Какая коллекция подходит для очереди на защиту: первым пришёл — первым ушёл?
> ---
> `Queue<T>`: `Enqueue` добавляет в конец, `Dequeue` забирает первого.

> [!QUIZ]
> Что вернёт `set.Add(x)`, если `x` уже есть в `HashSet`?
> ---
> `false` — элемент не добавится.
