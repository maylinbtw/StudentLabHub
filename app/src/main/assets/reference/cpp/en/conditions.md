# Conditions: if and switch

Conditional statements choose which code to run depending on the value of an expression.

## if, else if, else

```cpp
int mark;
cin >> mark;

if (mark >= 9)
{
    cout << "Excellent\n";
}
else if (mark >= 4)
{
    cout << "Passed\n";
}
else
{
    cout << "Retake\n";
}
```

Conditions are checked from top to bottom, and the first matching branch runs.

> A single-statement branch doesn't need braces. But `else` always belongs to the nearest unmatched `if`, so nested conditions are safer with braces.

## if with an initializer (C++17)

A variable can be declared right in the condition — it is visible only inside the `if` and `else`:

```cpp
if (int left = total - done; left > 0)
{
    cout << "Remaining: " << left << '\n';
}
```

## The conditional operator

```cpp
int mark = 5;
string status = mark >= 4 ? "passed" : "failed";
cout << "Max: " << (a > b ? a : b) << '\n';
```

## switch

`switch` compares an integer value (or a `char`, `enum`) against constants:

```cpp
char op;
double a, b;
cin >> a >> op >> b;

switch (op)
{
    case '+':
        cout << a + b << '\n';
        break;
    case '-':
        cout << a - b << '\n';
        break;
    case '*':
        cout << a * b << '\n';
        break;
    case '/':
        if (b != 0)
            cout << a / b << '\n';
        else
            cout << "Division by zero\n";
        break;
    default:
        cout << "Unknown operation\n";
}
```

```output
6 * 7
42
```

> [!WARNING]
> Without `break`, execution **falls through** into the next `case`. Sometimes that is intended (several values, one action), but more often it is a bug. Mark intended fall-through with the `[[fallthrough]];` attribute.

```cpp
switch (day)
{
    case 6:
    case 7:
        cout << "Weekend\n";
        break;
    default:
        cout << "Study\n";
}
```

## switch limitations

- `std::string` cannot be switched on — use an `if` chain;
- ranges such as `case 1..5` are not allowed;
- a variable can be declared inside a `case` only within braces:

```cpp
case 1:
{
    int temp = 10;
    cout << temp;
    break;
}
```

## Check yourself

> [!QUIZ]
> Why does `if (x = 5)` compile but misbehave?
> ---
> It's an assignment: `x` becomes 5 and the condition is always true. Comparison needs `==`.

> [!QUIZ]
> Can you `switch` on a `std::string`?
> ---
> No, only integer types, `char` and enums.
