# Наследование и полиморфизм

**Наследование** (`extends`) позволяет создать класс на основе другого. Наследник получает поля и методы родителя, может добавить свои и переопределить существующие.

## extends и super

```java
public class Person {
    protected String name;

    public Person(String name) {
        this.name = name;
    }

    public String info() {
        return "Человек " + name;
    }
}

public class Student extends Person {
    private String group;

    public Student(String name, String group) {
        super(name);              // конструктор родителя — первой строкой
        this.group = group;
    }

    public String getGroup() { return group; }

    @Override
    public String info() {
        return super.info() + ", группа " + group;
    }
}
```

```java
Person p = new Student("Анна", "П-21");
System.out.println(p.info());
```

```output
Человек Анна, группа П-21
```

- в Java у класса только **один** родитель;
- `super(...)` вызывает конструктор родителя, `super.info()` — его версию метода;
- `@Override` проверяет, что метод действительно переопределяет родительский.

## Полиморфизм

Переменная типа родителя может хранить объект наследника. Вызывается метод реального класса объекта:

```java
Person[] people = {
    new Person("Олег"),
    new Student("Ира", "П-22")
};

for (Person person : people) {
    System.out.println(person.info());
}
```

```output
Человек Олег
Человек Ира, группа П-22
```

## Абстрактные классы

`abstract`-класс нельзя создать через `new`. Его абстрактные методы обязан реализовать наследник:

```java
public abstract class Shape {
    public abstract double area();

    public void print() {
        System.out.printf("Площадь: %.2f%n", area());
    }
}

public class Circle extends Shape {
    private final double r;
    public Circle(double r) { this.r = r; }

    @Override
    public double area() { return Math.PI * r * r; }
}

new Circle(1).print();   // Площадь: 3,14
// new Shape(); — ошибка компиляции
```

## instanceof

Проверка типа с сопоставлением с образцом (Java 16+):

```java
if (p instanceof Student s) {
    System.out.println("Студент из группы " + s.getGroup());
}
```

## final

| Где | Значение |
|---|---|
| `final class` | от класса нельзя наследоваться (`String` — такой) |
| `final` метод | нельзя переопределить |
| `final` переменная | нельзя присвоить повторно |

> [!NOTE]
> Наследование — отношение «является»: студент **является** человеком. Если отношение «имеет» (у студента **есть** зачётка), используйте поле, а не `extends`.

## Проверь себя

> [!QUIZ]
> Где должен стоять вызов `super(...)` в конструкторе?
> ---
> Первой строкой конструктора.

> [!QUIZ]
> Зачем писать `@Override`?
> ---
> Компилятор проверит, что метод действительно переопределяет родительский, и поймает опечатку в имени или параметрах.
