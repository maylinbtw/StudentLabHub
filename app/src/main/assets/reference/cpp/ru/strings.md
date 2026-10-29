# Строки std::string

Для текста в C++ используют класс `std::string` из `<string>`. Он сам управляет памятью, знает свою длину и поддерживает удобные операции.

## Создание и склеивание

```cpp
#include <string>

string name = "Анна";
string group("П-21");
string line(10, '-');            // "----------"

string info = name + ", " + group;
info += "!";
cout << info << '\n';            // Анна, П-21!
```

> [!WARNING]
> Нельзя складывать два строковых литерала: `"a" + "b"` — ошибка компиляции. Хотя бы один операнд должен быть `string`: `string("a") + "b"`.

## Длина и символы

```cpp
string s = "Hello";
cout << s.length() << '\n';   // 5 (то же, что s.size())
cout << s[0] << '\n';         // H
cout << s.back() << '\n';     // o
s[0] = 'J';                   // Jello

for (char c : s)
    cout << c << ' ';
```

> [!NOTE]
> `length()` считает **байты**, а не буквы. В кодировке UTF-8 русская буква занимает 2 байта, поэтому `string("Анна").length()` равно 8.

## Поиск и подстроки

```cpp
string path = "labs/lab3/report.docx";

size_t pos = path.find('/');           // 4
size_t last = path.rfind('/');         // 9
string file = path.substr(last + 1);   // report.docx
string dir = path.substr(0, last);     // labs/lab3

if (path.find("lab3") != string::npos)
    cout << "Найдено\n";
```

`find` возвращает `string::npos`, если ничего не найдено.

## Изменение

```cpp
string s = "Laboratory work";

s.insert(0, "My ");         // My Laboratory work
s.erase(0, 3);              // Laboratory work
s.replace(0, 10, "Lab");    // Lab work
s.clear();                  // пустая строка
cout << s.empty();          // 1
```

Позиции и длины в `insert`, `erase`, `replace`, `substr` — в байтах, поэтому с кириллицей их нужно считать аккуратно.

## Сравнение

Строки сравниваются обычными операторами `==`, `!=`, `<`, `>` — посимвольно, по кодам:

```cpp
string a = "apple", b = "banana";
if (a < b) cout << a << " раньше по алфавиту\n";
```

## Преобразования

```cpp
int n = stoi("42");            // строка → int
double d = stod("3.14");       // строка → double
string s = to_string(2026);    // число → строка
```

Если строка не число, `stoi` выбросит исключение `invalid_argument`.

## Ввод строки

`cin >> s` читает до пробела. Чтобы прочитать строку целиком, используйте `getline`:

```cpp
string fullName;
getline(cin, fullName);
```

> [!WARNING]
> После `cin >> number` в потоке остаётся символ перевода строки, и следующий `getline` сразу вернёт пустую строку. Перед `getline` вызовите `cin.ignore()`.

## Разбиение на слова

```cpp
#include <sstream>

string text = "C++ Java Python";
stringstream ss(text);
string word;
while (ss >> word)
    cout << word << '\n';
```

## Проверь себя

> [!QUIZ]
> Что вернёт `s.find("x")`, если подстроки нет?
> ---
> `string::npos`.

> [!QUIZ]
> Почему `getline` после `cin >> n` возвращает пустую строку?
> ---
> В потоке остался перевод строки после числа. Перед `getline` нужен `cin.ignore()`.
