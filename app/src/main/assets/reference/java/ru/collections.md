# Коллекции: List, Map, Set

Коллекции из `java.util` хранят группы объектов и, в отличие от массивов, меняют размер.

## ArrayList

```java
import java.util.*;

List<String> subjects = new ArrayList<>();
subjects.add("Математика");
subjects.add("Физика");
subjects.add(0, "История");          // по индексу

System.out.println(subjects.get(1)); // Математика
System.out.println(subjects.size()); // 3
subjects.set(2, "Химия");            // заменить
subjects.remove("История");          // по значению
System.out.println(subjects);        // [Математика, Химия]
System.out.println(subjects.contains("Химия")); // true
```

Неизменяемый список: `List.of("Пн", "Вт", "Ср")`.

> [!WARNING]
> Коллекции хранят только объекты. Для чисел используют классы-обёртки: `List<Integer>`, а не `List<int>`. Осторожно с `remove`: у `List<Integer>` вызов `list.remove(1)` удалит элемент **по индексу 1**, а не число 1. Для удаления значения: `list.remove(Integer.valueOf(1))`.

## HashMap

Пары ключ → значение:

```java
Map<String, Integer> marks = new HashMap<>();
marks.put("Математика", 9);
marks.put("Физика", 7);
marks.put("Физика", 8);                  // заменит значение

System.out.println(marks.get("Физика")); // 8
System.out.println(marks.get("Химия"));  // null — ключа нет
System.out.println(marks.getOrDefault("Химия", 0)); // 0
System.out.println(marks.containsKey("Математика")); // true

for (Map.Entry<String, Integer> e : marks.entrySet()) {
    System.out.println(e.getKey() + ": " + e.getValue());
}
```

Подсчёт частоты:

```java
Map<String, Integer> counts = new HashMap<>();
for (String w : "to be or not to be".split(" ")) {
    counts.merge(w, 1, Integer::sum);
}
System.out.println(counts.get("to")); // 2
```

`HashMap` не хранит порядок. Нужен порядок добавления — `LinkedHashMap`, сортировка по ключу — `TreeMap`.

## HashSet

Только уникальные значения:

```java
Set<String> groups = new HashSet<>();
groups.add("П-21");
groups.add("П-22");
groups.add("П-21");                   // не добавится
System.out.println(groups.size());    // 2
```

`TreeSet` хранит элементы отсортированными.

## Перебор и удаление

```java
for (String s : subjects) {
    System.out.println(s);
}

subjects.forEach(System.out::println);

subjects.removeIf(s -> s.startsWith("Х"));   // удаление по условию
```

> [!WARNING]
> Удаление элементов внутри `for-each` по той же коллекции выбрасывает `ConcurrentModificationException`. Используйте `removeIf` или `Iterator`.

## Сортировка

```java
List<Integer> nums = new ArrayList<>(List.of(7, 2, 9, 4));
Collections.sort(nums);                         // [2, 4, 7, 9]
nums.sort(Comparator.reverseOrder());           // [9, 7, 4, 2]
```

## Stream API

Цепочки обработки, похожие на запросы:

```java
List<Integer> marks = List.of(7, 9, 4, 8, 10);

List<Integer> good = marks.stream()
        .filter(m -> m >= 8)
        .sorted()
        .toList();                              // [8, 9, 10] (Java 16+)

double avg = marks.stream()
        .mapToInt(Integer::intValue)
        .average()
        .orElse(0);                             // 7.6

long failed = marks.stream().filter(m -> m < 5).count(); // 1
```

## Что выбрать

| Задача | Коллекция |
|---|---|
| список по порядку, доступ по индексу | `ArrayList` |
| ключ → значение | `HashMap` |
| уникальные значения | `HashSet` |
| очередь | `ArrayDeque` (`offer`, `poll`) |
| стек | `ArrayDeque` (`push`, `pop`) |

## Проверь себя

> [!QUIZ]
> Что удалит `list.remove(1)` у `List<Integer>`?
> ---
> Элемент по индексу 1. Чтобы удалить число 1, нужно `list.remove(Integer.valueOf(1))`.

> [!QUIZ]
> Что вернёт `map.get(key)`, если ключа нет?
> ---
> `null`. Значение по умолчанию даёт `getOrDefault`.
