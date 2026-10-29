# Классы и объекты

**Класс** объединяет данные (**поля**) и функции для работы с ними (**методы**). **Объект** — экземпляр класса.

## Объявление класса

```cpp
#include <iostream>
#include <string>
using namespace std;

class Student
{
private:
    string name;
    int course;

public:
    Student(const string& n, int c) : name(n), course(c) {}

    void introduce() const
    {
        cout << "Я " << name << ", " << course << " курс\n";
    }

    void nextCourse() { course++; }
};

int main()
{
    Student anna("Анна", 2);
    anna.introduce();
    anna.nextCourse();
    anna.introduce();
}
```

```output
Я Анна, 2 курс
Я Анна, 3 курс
```

> [!WARNING]
> После закрывающей скобки класса обязательна точка с запятой: `};`. Без неё компилятор выдаст непонятную ошибку на следующей строке.

## private и public

| Раздел | Кто видит |
|---|---|
| `public` | любой код |
| `private` | только методы самого класса (по умолчанию в `class`) |
| `protected` | класс и наследники |

`struct` — то же самое, что `class`, но по умолчанию всё `public`. `struct` обычно используют для простых наборов данных без логики.

## Конструктор и список инициализации

Конструктор называется как класс и не имеет типа результата. Поля удобно инициализировать в **списке инициализации** после двоеточия:

```cpp
class Lab
{
    string title;
    int number;
public:
    Lab() : title("Без названия"), number(0) {}              // по умолчанию
    Lab(const string& t, int n) : title(t), number(n) {}
};

Lab a;               // первый конструктор
Lab b("Массивы", 3); // второй
```

## const-методы

Метод со словом `const` после скобок обещает не менять объект. Только такие методы можно вызывать у константного объекта:

```cpp
int getCourse() const { return course; }
```

## Геттеры и сеттеры

Поля закрывают, а доступ дают через методы, которые проверяют значения:

```cpp
class Mark
{
    int value = 0;
public:
    int get() const { return value; }
    void set(int v)
    {
        if (v >= 0 && v <= 10) value = v;
    }
};
```

## Деструктор

Деструктор `~Имя()` вызывается автоматически, когда объект уничтожается. Там освобождают ресурсы:

```cpp
class Logger
{
public:
    Logger()  { cout << "Открыт\n"; }
    ~Logger() { cout << "Закрыт\n"; }
};

{
    Logger log;
}   // здесь вызовется деструктор
```

## Разделение на .h и .cpp

В проектах объявление класса пишут в `Student.h`, а тела методов — в `Student.cpp`:

```cpp
// Student.h
#pragma once
#include <string>

class Student
{
    std::string name;
public:
    Student(const std::string& n);
    void introduce() const;
};
```

```cpp
// Student.cpp
#include "Student.h"
#include <iostream>

Student::Student(const std::string& n) : name(n) {}

void Student::introduce() const
{
    std::cout << name << '\n';
}
```

`Student::` указывает, к какому классу относится метод.
