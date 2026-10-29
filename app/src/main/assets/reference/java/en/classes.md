# Classes and objects

A **class** describes data (**fields**) and behavior (**methods**). An **object** is an instance of a class created with `new`.

## A class with fields, a constructor and methods

```java
public class Student {
    private String name;
    private int course;

    public Student(String name, int course) {
        this.name = name;
        this.course = course;
    }

    public void introduce() {
        System.out.println("I'm " + name + ", year " + course);
    }

    public String getName() { return name; }

    public int getCourse() { return course; }

    public void setCourse(int course) {
        if (course >= 1 && course <= 4) this.course = course;
    }
}
```

```java
Student anna = new Student("Anna", 2);
anna.introduce();
anna.setCourse(3);
System.out.println(anna.getCourse());
```

```output
I'm Anna, year 2
3
```

- a public class lives in a file with the same name: `Student.java`;
- `this.name` is the object's field, `name` is the constructor parameter;
- fields are hidden (`private`), access goes through **getters** and **setters**. This is **encapsulation**.

## Access modifiers

| Modifier | Who can see it |
|---|---|
| `public` | everyone |
| `protected` | the package and subclasses |
| *(no modifier)* | only classes in the same package |
| `private` | only the class itself |

## Several constructors

```java
public class Lab {
    private final String title;
    private final int number;

    public Lab(String title, int number) {
        this.title = title;
        this.number = number;
    }

    public Lab(String title) {
        this(title, 1);     // calls another constructor
    }
}
```

A `final` field is assigned once — in its declaration or in a constructor.

## static

Static fields and methods belong to the class, not to an object:

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

## toString and equals

All classes inherit from `Object`. Its methods are overridden so the object prints and compares properly:

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
> When you override `equals`, always override `hashCode` too. Otherwise objects misbehave in `HashSet` and `HashMap`.

## record

For simple data classes (Java 16+) there is `record` — the constructor, getters, `equals`, `hashCode` and `toString` are generated automatically:

```java
public record Mark(String subject, int value) {}

Mark m = new Mark("Physics", 8);
System.out.println(m.subject());  // Physics — a getter without "get"
System.out.println(m);            // Mark[subject=Physics, value=8]
```

`record` fields are immutable.
