# Interfaces

An **interface** is a set of methods a class promises to implement. A class can implement any number of interfaces with `implements`.

## Declaration and implementation

```java
public interface Printable {
    void print();
}

public class Lab implements Printable {
    private final String title;
    public Lab(String title) { this.title = title; }

    @Override
    public void print() {
        System.out.println("Lab: " + title);
    }
}

public class Report implements Printable {
    @Override
    public void print() {
        System.out.println("Report");
    }
}
```

```java
Printable[] items = { new Lab("Arrays"), new Report() };
for (Printable item : items) {
    item.print();
}
```

```output
Lab: Arrays
Report
```

Interface methods are `public abstract` by default — these words can be omitted.

## Several interfaces

```java
public interface Gradable {
    int getMark();
}

public class Lab extends Work implements Printable, Gradable {
    // ...
}
```

First `extends` (one class), then `implements` (any number of interfaces).

## default and static methods

Since Java 8 an interface can contain methods with an implementation:

```java
public interface Gradable {
    int getMark();

    default boolean isPassed() {
        return getMark() >= 4;
    }

    static Gradable of(int mark) {
        return () -> mark;
    }
}
```

## Functional interfaces and lambdas

An interface with a single abstract method can be implemented with a **lambda**:

```java
Printable hello = () -> System.out.println("Hello");
hello.print();
```

Built-in interfaces from `java.util.function`:

| Interface | Method | Example |
|---|---|---|
| `Predicate<T>` | `boolean test(T)` | `m -> m >= 4` |
| `Function<T, R>` | `R apply(T)` | `s -> s.length()` |
| `Consumer<T>` | `void accept(T)` | `s -> System.out.println(s)` |
| `Supplier<T>` | `T get()` | `() -> new ArrayList<>()` |

## Comparable and Comparator

`Comparable` defines the natural order of objects, `Comparator` defines any other:

```java
public class Student implements Comparable<Student> {
    private final String name;
    private final double average;
    // constructor, getters...

    @Override
    public int compareTo(Student other) {
        return name.compareTo(other.name);   // by name
    }
}

List<Student> list = new ArrayList<>(/* ... */);
Collections.sort(list);                                        // by name
list.sort(Comparator.comparingDouble(Student::getAverage).reversed()); // by average, descending
```

## Interface or abstract class

| | Interface | Abstract class |
|---|---|---|
| how many can be inherited | many | one |
| fields with state | no (constants only) | yes |
| constructor | no | yes |
| when to choose | a shared **ability** | a shared **base** |
