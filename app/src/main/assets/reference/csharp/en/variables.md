# Variables and constants

A **variable** is a named piece of memory that stores a value of a specific type. The value can change while the program runs.

## Declaration

The type comes first, then the name:

```csharp
string name;
int age;
```

Several variables of the same type can be declared with commas:

```csharp
int x, y, z;
```

## Assignment and initialization

The `=` operator stores a value in a variable. Doing it right in the declaration is called **initialization**:

```csharp
string name = "Anna";
int age;
age = 19;          // assignment
age = age + 1;     // new value is 20

Console.WriteLine($"{name}: {age}");
```

```output
Anna: 20
```

> [!WARNING]
> A local variable cannot be read before something is assigned to it. `int n; Console.WriteLine(n);` does not compile: error CS0165.

## Naming rules

- a name may contain letters, digits and `_`;
- a name cannot start with a digit: `2count` is an error, `count2` is fine;
- keywords (`int`, `class`, `if`…) cannot be used. If you really need one, prefix it with `@`: `@class`;
- case matters: `age` and `Age` are different variables.

## Type inference: var

The `var` keyword lets you omit the type — the compiler infers it from the value on the right:

```csharp
var city = "London";   // string
var count = 10;        // int
var price = 9.99;      // double
```

`var` does not make a variable "any type": the type is fixed after the declaration.

```csharp
var count = 10;
count = "ten"; // CS0029: cannot convert string to int
```

`var` requires initialization: `var x;` is an error.

## Constants

A **constant** gets its value once and never changes. It is declared with `const`:

```csharp
const double Pi = 3.14159;
const int MaxStudents = 30;

double area = Pi * 2 * 2;
// Pi = 3; — error CS0131: a constant cannot be assigned
```

A constant's value must be known at compile time: a number, a string, a `bool` or an expression built from other constants.

## Scope

A variable exists from its declaration to the end of the `{ }` block where it was declared:

```csharp
int outer = 1;
{
    int inner = 2;
    Console.WriteLine(outer + inner); // 3
}
// Console.WriteLine(inner); — error: inner is not visible here
```

## Check yourself

> [!QUIZ]
> Does `var x;` compile?
> ---
> No. `var` requires an initializer: the type is inferred from the value on the right.

> [!QUIZ]
> How is `const` different from a regular variable?
> ---
> A constant's value is set once, known at compile time and never changes.
