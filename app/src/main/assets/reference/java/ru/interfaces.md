# Интерфейсы

**Интерфейс** — набор методов, которые класс обязуется реализовать. Класс может реализовать сколько угодно интерфейсов через `implements`.

## Объявление и реализация

```java
public interface Printable {
    void print();
}

public class Lab implements Printable {
    private final String title;
    public Lab(String title) { this.title = title; }

    @Override
    public void print() {
        System.out.println("Лабораторная: " + title);
    }
}

public class Report implements Printable {
    @Override
    public void print() {
        System.out.println("Отчёт");
    }
}
```

```java
Printable[] items = { new Lab("Массивы"), new Report() };
for (Printable item : items) {
    item.print();
}
```

```output
Лабораторная: Массивы
Отчёт
```

Методы интерфейса по умолчанию `public abstract` — эти слова можно не писать.

## Несколько интерфейсов

```java
public interface Gradable {
    int getMark();
}

public class Lab extends Work implements Printable, Gradable {
    // ...
}
```

Сначала `extends` (один класс), затем `implements` (любое число интерфейсов).

## default и static методы

С Java 8 в интерфейсе можно писать методы с реализацией:

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

## Функциональные интерфейсы и лямбды

Интерфейс с одним абстрактным методом можно реализовать **лямбдой**:

```java
Printable hello = () -> System.out.println("Привет");
hello.print();
```

Готовые интерфейсы из `java.util.function`:

| Интерфейс | Метод | Пример |
|---|---|---|
| `Predicate<T>` | `boolean test(T)` | `m -> m >= 4` |
| `Function<T, R>` | `R apply(T)` | `s -> s.length()` |
| `Consumer<T>` | `void accept(T)` | `s -> System.out.println(s)` |
| `Supplier<T>` | `T get()` | `() -> new ArrayList<>()` |

## Comparable и Comparator

`Comparable` задаёт естественный порядок объектов, `Comparator` — любой другой:

```java
public class Student implements Comparable<Student> {
    private final String name;
    private final double average;
    // конструктор, геттеры...

    @Override
    public int compareTo(Student other) {
        return name.compareTo(other.name);   // по имени
    }
}

List<Student> list = new ArrayList<>(/* ... */);
Collections.sort(list);                                        // по имени
list.sort(Comparator.comparingDouble(Student::getAverage).reversed()); // по баллу, убывание
```

## Интерфейс или абстрактный класс

| | Интерфейс | Абстрактный класс |
|---|---|---|
| сколько можно унаследовать | много | один |
| поля с состоянием | нет (только константы) | да |
| конструктор | нет | да |
| когда выбирать | общее **умение** | общая **основа** |
