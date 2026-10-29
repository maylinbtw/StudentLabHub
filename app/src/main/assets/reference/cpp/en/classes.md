# Classes and objects

A **class** combines data (**fields**) and functions that work with it (**methods**). An **object** is an instance of a class.

## Declaring a class

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
        cout << "I'm " << name << ", year " << course << '\n';
    }

    void nextCourse() { course++; }
};

int main()
{
    Student anna("Anna", 2);
    anna.introduce();
    anna.nextCourse();
    anna.introduce();
}
```

```output
I'm Anna, year 2
I'm Anna, year 3
```

> [!WARNING]
> A semicolon is required after the closing brace of a class: `};`. Without it the compiler gives a confusing error on the next line.

## private and public

| Section | Who can see it |
|---|---|
| `public` | any code |
| `private` | only the class's own methods (default in `class`) |
| `protected` | the class and subclasses |

`struct` is the same as `class`, but everything is `public` by default. `struct` is usually used for simple data without logic.

## Constructor and initializer list

A constructor is named after the class and has no return type. Fields are conveniently initialized in the **initializer list** after a colon:

```cpp
class Lab
{
    string title;
    int number;
public:
    Lab() : title("Untitled"), number(0) {}                  // default
    Lab(const string& t, int n) : title(t), number(n) {}
};

Lab a;              // first constructor
Lab b("Arrays", 3); // second
```

## const methods

A method with `const` after the parentheses promises not to change the object. Only such methods can be called on a const object:

```cpp
int getCourse() const { return course; }
```

## Getters and setters

Fields are hidden, and access is given through methods that validate values:

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

## Destructor

The destructor `~Name()` is called automatically when an object is destroyed. Resources are released there:

```cpp
class Logger
{
public:
    Logger()  { cout << "Opened\n"; }
    ~Logger() { cout << "Closed\n"; }
};

{
    Logger log;
}   // the destructor runs here
```

## Splitting into .h and .cpp

In projects, the class declaration goes into `Student.h` and method bodies into `Student.cpp`:

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

`Student::` tells which class the method belongs to.

## Check yourself

> [!QUIZ]
> How does `struct` differ from `class` in C++?
> ---
> Only in default access: everything is `public` in a `struct` and `private` in a `class`.

> [!QUIZ]
> What does `const` after a method's parameter list promise?
> ---
> The method doesn't modify the object, so it can be called on a const object.
