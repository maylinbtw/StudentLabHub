# Наследование и виртуальные функции

**Наследование** позволяет создать класс на основе существующего: наследник получает поля и методы базового класса и добавляет свои.

## Синтаксис

```cpp
class Person
{
protected:
    string name;
public:
    Person(const string& n) : name(n) {}
    void sayName() const { cout << "Меня зовут " << name << '\n'; }
};

class Student : public Person
{
    string group;
public:
    Student(const string& n, const string& g) : Person(n), group(g) {}
    void showGroup() const { cout << name << " — " << group << '\n'; }
};

Student s("Анна", "П-21");
s.sayName();     // метод Person
s.showGroup();   // свой метод
```

```output
Меня зовут Анна
Анна — П-21
```

- `: public Person` — открытое наследование, используется почти всегда;
- конструктор базового класса вызывается в списке инициализации: `Person(n)`;
- `protected`-поле `name` доступно в наследнике.

## Виртуальные функции

Чтобы через указатель или ссылку на базовый класс вызывалась версия наследника, метод объявляют `virtual`, а в наследнике помечают `override`:

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

Без `virtual` в обоих случаях вызвался бы `Shape::area()` и вывелся бы 0.

> [!WARNING]
> Если объекты наследников удаляют через указатель на базовый класс, деструктор базового класса должен быть `virtual`. Иначе деструктор наследника не вызовется.

## Абстрактный класс

Чисто виртуальная функция `= 0` не имеет реализации. Класс с такой функцией — **абстрактный**, его объект создать нельзя:

```cpp
class Shape
{
public:
    virtual double area() const = 0;
    virtual ~Shape() = default;
};

// Shape s; — ошибка: класс абстрактный
```

## Полиморфизм на практике

Разные фигуры в одной коллекции:

```cpp
#include <vector>
#include <memory>

vector<unique_ptr<Shape>> shapes;
shapes.push_back(make_unique<Circle>(1));
shapes.push_back(make_unique<Square>(3));

double total = 0;
for (const auto& s : shapes)
    total += s->area();
cout << "Общая площадь: " << total << '\n';   // 12.1416
```

## override и final

| Слово | Значение |
|---|---|
| `override` | метод переопределяет виртуальный; если сигнатура не совпала — ошибка компиляции |
| `final` у метода | дальше переопределять нельзя |
| `final` у класса | от класса нельзя наследоваться |

Всегда пишите `override` — он ловит опечатки в имени и параметрах.

## Проверь себя

> [!QUIZ]
> Что выведет `print(Circle(1))`, если `area()` в `Shape` не `virtual`?
> ---
> Вызовется `Shape::area()` и выведется 0: без `virtual` нет полиморфизма.

> [!QUIZ]
> Зачем базовому классу виртуальный деструктор?
> ---
> Чтобы при удалении наследника через указатель на базовый класс вызвался и деструктор наследника.
