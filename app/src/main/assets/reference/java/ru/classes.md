# Классы и объекты

**Класс** описывает данные (**поля**) и поведение (**методы**). **Объект** — экземпляр класса, созданный через `new`.

## Класс с полями, конструктором и методами

```java
public class Student {
    private String name;
    private int course;

    public Student(String name, int course) {
        this.name = name;
        this.course = course;
    }

    public void introduce() {
        System.out.println("Я " + name + ", " + course + " курс");
    }

    public String getName() { return name; }

    public int getCourse() { return course; }

    public void setCourse(int course) {
        if (course >= 1 && course <= 4) this.course = course;
    }
}
```

```java
Student anna = new Student("Анна", 2);
anna.introduce();
anna.setCourse(3);
System.out.println(anna.getCourse());
```

```output
Я Анна, 2 курс
3
```

- публичный класс лежит в файле с таким же именем: `Student.java`;
- `this.name` — поле объекта, `name` — параметр конструктора;
- поля закрыты (`private`), доступ — через **геттеры** и **сеттеры**. Это **инкапсуляция**.

## Модификаторы доступа

| Модификатор | Кто видит |
|---|---|
| `public` | все |
| `protected` | пакет и наследники |
| *(без модификатора)* | только классы того же пакета |
| `private` | только сам класс |

## Несколько конструкторов

```java
public class Lab {
    private final String title;
    private final int number;

    public Lab(String title, int number) {
        this.title = title;
        this.number = number;
    }

    public Lab(String title) {
        this(title, 1);     // вызов другого конструктора
    }
}
```

`final`-поле присваивается один раз — в объявлении или конструкторе.

## static

Статические поля и методы принадлежат классу, а не объекту:

```java
public class Student {
    private static int count = 0;

    public Student() {
        count++;
    }

    public static int getCount() { return count; }
}

new Student();
new Student();
System.out.println(Student.getCount()); // 2
```

## toString и equals

Все классы наследуют `Object`. Его методы переопределяют, чтобы объект нормально печатался и сравнивался:

```java
@Override
public String toString() {
    return "Student{name=" + name + ", course=" + course + "}";
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Student other)) return false;
    return course == other.course && name.equals(other.name);
}

@Override
public int hashCode() {
    return java.util.Objects.hash(name, course);
}
```

> [!WARNING]
> Переопределяя `equals`, всегда переопределяйте и `hashCode`. Иначе объекты будут вести себя неправильно в `HashSet` и `HashMap`.

## record

Для простых классов-данных (Java 16+) есть `record` — конструктор, геттеры, `equals`, `hashCode` и `toString` создаются автоматически:

```java
public record Mark(String subject, int value) {}

Mark m = new Mark("Физика", 8);
System.out.println(m.subject());  // Физика — геттер без get
System.out.println(m);            // Mark[subject=Физика, value=8]
```

Поля `record` неизменяемы.

## Проверь себя

> [!QUIZ]
> Что нужно переопределить вместе с `equals`?
> ---
> `hashCode`, иначе объекты будут неправильно работать в `HashSet` и `HashMap`.

> [!QUIZ]
> Как называются геттеры у `record Mark(String subject, int value)`?
> ---
> Как поля, без `get`: `subject()` и `value()`.
