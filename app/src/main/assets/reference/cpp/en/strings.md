# std::string

For text, C++ uses the `std::string` class from `<string>`. It manages memory itself, knows its length and supports handy operations.

## Creating and concatenating

```cpp
#include <string>

string name = "Anna";
string group("P-21");
string line(10, '-');            // "----------"

string info = name + ", " + group;
info += "!";
cout << info << '\n';            // Anna, P-21!
```

> [!WARNING]
> You can't add two string literals: `"a" + "b"` is a compile error. At least one operand must be a `string`: `string("a") + "b"`.

## Length and characters

```cpp
string s = "Hello";
cout << s.length() << '\n';   // 5 (same as s.size())
cout << s[0] << '\n';         // H
cout << s.back() << '\n';     // o
s[0] = 'J';                   // Jello

for (char c : s)
    cout << c << ' ';
```

> [!NOTE]
> `length()` counts **bytes**, not letters. In UTF-8 a non-Latin letter such as Cyrillic takes 2 bytes, so `string("Анна").length()` is 8.

## Search and substrings

```cpp
string path = "labs/lab3/report.docx";

size_t pos = path.find('/');           // 4
size_t last = path.rfind('/');         // 9
string file = path.substr(last + 1);   // report.docx
string dir = path.substr(0, last);     // labs/lab3

if (path.find("lab3") != string::npos)
    cout << "Found\n";
```

`find` returns `string::npos` if nothing is found.

## Modifying

```cpp
string s = "Laboratory work";

s.insert(0, "My ");         // My Laboratory work
s.erase(0, 3);              // Laboratory work
s.replace(0, 10, "Lab");    // Lab work
s.clear();                  // empty string
cout << s.empty();          // 1
```

## Comparison

Strings are compared with the usual `==`, `!=`, `<`, `>` operators — character by character, by code:

```cpp
string a = "apple", b = "banana";
if (a < b) cout << a << " comes first alphabetically\n";
```

## Conversions

```cpp
int n = stoi("42");            // string → int
double d = stod("3.14");       // string → double
string s = to_string(2026);    // number → string
```

If the string is not a number, `stoi` throws `invalid_argument`.

## Reading a line

`cin >> s` reads up to a space. To read a whole line, use `getline`:

```cpp
string fullName;
getline(cin, fullName);
```

> [!WARNING]
> After `cin >> number` a newline stays in the stream, and the next `getline` immediately returns an empty string. Call `cin.ignore()` before `getline`.

## Splitting into words

```cpp
#include <sstream>

string text = "C++ Java Python";
stringstream ss(text);
string word;
while (ss >> word)
    cout << word << '\n';
```
