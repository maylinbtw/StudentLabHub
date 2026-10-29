# Collections: List, Map, Set

Collections from `java.util` hold groups of objects and, unlike arrays, can change size.

## ArrayList

```java
import java.util.*;

List<String> subjects = new ArrayList<>();
subjects.add("Math");
subjects.add("Physics");
subjects.add(0, "History");          // at an index

System.out.println(subjects.get(1)); // Math
System.out.println(subjects.size()); // 3
subjects.set(2, "Chemistry");        // replace
subjects.remove("History");          // by value
System.out.println(subjects);        // [Math, Chemistry]
System.out.println(subjects.contains("Chemistry")); // true
```

An immutable list: `List.of("Mon", "Tue", "Wed")`.

> [!WARNING]
> Collections hold only objects. For numbers use wrapper classes: `List<Integer>`, not `List<int>`. Be careful with `remove`: on a `List<Integer>`, `list.remove(1)` removes the element **at index 1**, not the number 1. To remove the value: `list.remove(Integer.valueOf(1))`.

## HashMap

Key → value pairs:

```java
Map<String, Integer> marks = new HashMap<>();
marks.put("Math", 9);
marks.put("Physics", 7);
marks.put("Physics", 8);                  // replaces the value

System.out.println(marks.get("Physics"));   // 8
System.out.println(marks.get("Chemistry")); // null — no such key
System.out.println(marks.getOrDefault("Chemistry", 0)); // 0
System.out.println(marks.containsKey("Math"));          // true

for (Map.Entry<String, Integer> e : marks.entrySet()) {
    System.out.println(e.getKey() + ": " + e.getValue());
}
```

Counting words:

```java
Map<String, Integer> counts = new HashMap<>();
for (String w : "to be or not to be".split(" ")) {
    counts.merge(w, 1, Integer::sum);
}
System.out.println(counts.get("to")); // 2
```

`HashMap` keeps no order. For insertion order use `LinkedHashMap`, for key order — `TreeMap`.

## HashSet

Unique values only:

```java
Set<String> groups = new HashSet<>();
groups.add("P-21");
groups.add("P-22");
groups.add("P-21");                   // not added
System.out.println(groups.size());    // 2
```

`TreeSet` keeps elements sorted.

## Iterating and removing

```java
for (String s : subjects) {
    System.out.println(s);
}

subjects.forEach(System.out::println);

subjects.removeIf(s -> s.startsWith("C"));   // remove by condition
```

> [!WARNING]
> Removing elements inside a `for-each` over the same collection throws `ConcurrentModificationException`. Use `removeIf` or an `Iterator`.

## Sorting

```java
List<Integer> nums = new ArrayList<>(List.of(7, 2, 9, 4));
Collections.sort(nums);                         // [2, 4, 7, 9]
nums.sort(Comparator.reverseOrder());           // [9, 7, 4, 2]
```

## Stream API

Processing chains that read like queries:

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

## What to choose

| Task | Collection |
|---|---|
| ordered list, access by index | `ArrayList` |
| key → value | `HashMap` |
| unique values | `HashSet` |
| queue | `ArrayDeque` (`offer`, `poll`) |
| stack | `ArrayDeque` (`push`, `pop`) |
