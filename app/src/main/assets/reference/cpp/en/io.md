# Input and output

Console I/O in C++ works through **streams** from the `<iostream>` library: `cout` for output, `cin` for input, `cerr` for errors.

## Output: cout

The `<<` operator can be chained — values are printed one after another:

```cpp
#include <iostream>
using namespace std;

int main()
{
    int done = 7, total = 10;
    cout << "Done " << done << " of " << total << endl;
    cout << "Second line\n";
    return 0;
}
```

```output
Done 7 of 10
Second line
```

`endl` ends the line and flushes the output buffer, `'\n'` only ends the line and is faster.

## Input: cin

The `>>` operator reads a value and stores it in a variable. Spaces and line breaks between values are skipped:

```cpp
int age;
double height;

cout << "Age and height: ";
cin >> age >> height;
cout << "Next year you will be " << age + 1 << endl;
```

```output
Age and height: 19 1.82
Next year you will be 20
```

## Reading a whole line

`cin >> s` reads a single word — up to the first space. Use `getline` for a whole line:

```cpp
#include <string>

string fullName;
cout << "Full name: ";
getline(cin, fullName);
```

> [!WARNING]
> If `cin >> ...` came before `getline`, a newline is left in the buffer and `getline` immediately returns an empty string. Skip it with `cin.ignore();` before `getline`.

## Validating input

If the user types letters instead of a number, the stream enters a failure state:

```cpp
int n;
cout << "Enter a number: ";

while (!(cin >> n))
{
    cout << "Not a number, try again: ";
    cin.clear();                  // reset the error
    cin.ignore(10000, '\n');      // discard the bad input
}
```

## Formatting: iomanip

```cpp
#include <iomanip>

double avg = 7.4567;
cout << fixed << setprecision(2) << avg << endl;       // 7.46
cout << setw(6) << 42 << '|' << endl;                   // "    42|"
cout << left << setw(10) << "Name" << "Mark" << endl;   // left-aligned
cout << right << setfill('0') << setw(3) << 7 << endl;  // 007
```

| Manipulator | Effect |
|---|---|
| `fixed` | fixed-point notation |
| `setprecision(n)` | n digits after the point (with `fixed`) |
| `setw(n)` | width of the next value |
| `left` / `right` | alignment |
| `setfill(c)` | fill character |
| `boolalpha` | print `true`/`false` instead of 1/0 |

## printf and scanf

Textbooks also use C-style I/O from `<cstdio>`:

```cpp
#include <cstdio>

int n = 5;
double x = 2.5;
printf("n = %d, x = %.2f\n", n, x);
scanf("%d", &n);
```

Main specifiers: `%d` — int, `%f` — double in printf, `%lf` — double in scanf, `%c` — char, `%s` — a C string.
