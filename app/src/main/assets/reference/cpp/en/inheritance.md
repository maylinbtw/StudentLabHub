# Inheritance and virtual functions

**Inheritance** lets you create a class based on an existing one: the derived class gets the fields and methods of the base class and adds its own.

## Syntax

```cpp
class Person
{
protected:
    string name;
public:
    Person(const string& n) : name(n) {}
    void sayName() const { cout << "My name is " << name << '\n'; }
};

class Student : public Person
{
    string group;
public:
    Student(const string& n, const string& g) : Person(n), group(g) {}
    void showGroup() const { cout << name << " — " << group << '\n'; }
};

Student s("Anna", "P-21");
s.sayName();     // Person's method
s.showGroup();   // own method
```

```output
My name is Anna
Anna — P-21
```

- `: public Person` — public inheritance, used almost always;
- the base constructor is called in the initializer list: `Person(n)`;
- the `protected` field `name` is accessible in the derived class.

## Virtual functions

To call the derived version through a pointer or reference to the base class, declare the method `virtual` and mark it `override` in the derived class:

```cpp
class Shape
{
public:
    virtual double area() const { return 0; }
    virtual ~Shape() = default;
};

class Circle : public Shape
{
    double r;
public:
    Circle(double r) : r(r) {}
    double area() const override { return 3.14159 * r * r; }
};

class Square : public Shape
{
    double a;
public:
    Square(double a) : a(a) {}
    double area() const override { return a * a; }
};

void print(const Shape& s)
{
    cout << s.area() << '\n';
}

print(Circle(1));   // 3.14159
print(Square(2));   // 4
```

Without `virtual`, both calls would use `Shape::area()` and print 0.

> [!WARNING]
> If derived objects are deleted through a base-class pointer, the base destructor must be `virtual`. Otherwise the derived destructor won't run.

## Abstract class

A pure virtual function `= 0` has no implementation. A class with such a function is **abstract** and can't be instantiated:

```cpp
class Shape
{
public:
    virtual double area() const = 0;
    virtual ~Shape() = default;
};

// Shape s; — error: the class is abstract
```

## Polymorphism in practice

Different shapes in one collection:

```cpp
#include <vector>
#include <memory>

vector<unique_ptr<Shape>> shapes;
shapes.push_back(make_unique<Circle>(1));
shapes.push_back(make_unique<Square>(3));

double total = 0;
for (const auto& s : shapes)
    total += s->area();
cout << "Total area: " << total << '\n';   // 12.1416
```

## override and final

| Keyword | Meaning |
|---|---|
| `override` | the method overrides a virtual one; a signature mismatch is a compile error |
| `final` on a method | it can't be overridden further |
| `final` on a class | the class can't be inherited from |

Always write `override` — it catches typos in names and parameters.

## Check yourself

> [!QUIZ]
> What does `print(Circle(1))` print if `Shape::area()` isn't `virtual`?
> ---
> `Shape::area()` is called and prints 0: without `virtual` there's no polymorphism.

> [!QUIZ]
> Why does a base class need a virtual destructor?
> ---
> So that deleting a derived object through a base pointer also runs the derived destructor.
