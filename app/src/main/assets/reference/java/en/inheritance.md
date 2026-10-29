# Inheritance and polymorphism

**Inheritance** (`extends`) lets you create a class based on another. The subclass gets the parent's fields and methods, can add its own and override existing ones.

## extends and super

```java
public class Person {
    protected String name;

    public Person(String name) {
        this.name = name;
    }

    public String info() {
        return "Person " + name;
    }
}

public class Student extends Person {
    private String group;

    public Student(String name, String group) {
        super(name);              // the parent constructor — on the first line
        this.group = group;
    }

    public String getGroup() { return group; }

    @Override
    public String info() {
        return super.info() + ", group " + group;
    }
}
```

```java
Person p = new Student("Anna", "P-21");
System.out.println(p.info());
```

```output
Person Anna, group P-21
```

- in Java a class has only **one** parent;
- `super(...)` calls the parent constructor, `super.info()` calls its version of the method;
- `@Override` checks that the method really overrides a parent method.

## Polymorphism

A parent-type variable can hold a subclass object. The method of the object's actual class is called:

```java
Person[] people = {
    new Person("Oleg"),
    new Student("Ira", "P-22")
};

for (Person person : people) {
    System.out.println(person.info());
}
```

```output
Person Oleg
Person Ira, group P-22
```

## Abstract classes

An `abstract` class can't be created with `new`. Its abstract methods must be implemented by a subclass:

```java
public abstract class Shape {
    public abstract double area();

    public void print() {
        System.out.printf("Area: %.2f%n", area());
    }
}

public class Circle extends Shape {
    private final double r;
    public Circle(double r) { this.r = r; }

    @Override
    public double area() { return Math.PI * r * r; }
}

new Circle(1).print();   // Area: 3.14
// new Shape(); — compile error
```

## instanceof

Type check with pattern matching (Java 16+):

```java
if (p instanceof Student s) {
    System.out.println("A student from group " + s.getGroup());
}
```

## final

| Where | Meaning |
|---|---|
| `final class` | the class can't be extended (`String` is one) |
| `final` method | can't be overridden |
| `final` variable | can't be reassigned |

> [!NOTE]
> Inheritance is an "is a" relationship: a student **is a** person. For "has a" (a student **has** a record book), use a field instead of `extends`.

## Check yourself

> [!QUIZ]
> Where must the `super(...)` call go in a constructor?
> ---
> On the first line of the constructor.

> [!QUIZ]
> Why write `@Override`?
> ---
> The compiler checks that the method really overrides a parent one and catches typos in its name or parameters.
