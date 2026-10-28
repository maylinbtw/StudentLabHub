# Working with strings

The `string` type stores text as a sequence of `char` characters. Strings come with many ready-made methods: searching, replacing, splitting, changing case.

## Strings are immutable

Any method that "changes" a string actually returns a **new** string. The original stays the same:

```csharp
string group = "cs-44";
group.ToUpper();                 // the result is lost
Console.WriteLine(group);        // cs-44

group = group.ToUpper();         // keep the result
Console.WriteLine(group);        // CS-44
```

## Length and characters

```csharp
string word = "Defense";

Console.WriteLine(word.Length);     // 7
Console.WriteLine(word[0]);         // D
Console.WriteLine(word[^1]);        // e — the last character

foreach (char c in word)
    Console.Write($"{c} ");         // D e f e n s e
```

The indexer is read-only: `word[0] = 'd'` does not compile.

## Main methods

| Method | What it does | Example → result |
|---|---|---|
| `ToUpper()` / `ToLower()` | changes case | `"Lab".ToUpper()` → `"LAB"` |
| `Trim()` | removes surrounding spaces | `"  yes  ".Trim()` → `"yes"` |
| `Contains(s)` | substring present? | `"Lab #5".Contains("#")` → True |
| `StartsWith(s)` / `EndsWith(s)` | start / end | `"main.cs".EndsWith(".cs")` → True |
| `IndexOf(s)` | position or −1 | `"abcabc".IndexOf("c")` → 2 |
| `Substring(i, n)` | part of the string | `"Hello".Substring(1, 3)` → `"ell"` |
| `Replace(a, b)` | replace | `"1-2-3".Replace("-", "+")` → `"1+2+3"` |
| `Split(c)` | split into an array | `"a,b,c".Split(',')` → 3 elements |
| `Insert(i, s)` / `Remove(i, n)` | insert / delete | `"ab".Insert(1, "X")` → `"aXb"` |
| `PadLeft(n)` / `PadRight(n)` | pad to a length | `"7".PadLeft(3, '0')` → `"007"` |

## Splitting and joining

`Split` turns a string into an array, `string.Join` turns it back:

```csharp
string line = "Smith;9;8;10";
string[] parts = line.Split(';');

string surname = parts[0];
int first = int.Parse(parts[1]);

Console.WriteLine($"{surname}: {first}");
Console.WriteLine(string.Join(" | ", parts));
```

```output
Smith: 9
Smith | 9 | 8 | 10
```

Empty entries can be dropped: `text.Split(' ', StringSplitOptions.RemoveEmptyEntries)`.

## Comparing strings

```csharp
string a = "Linux", b = "linux";

Console.WriteLine(a == b);                                         // False
Console.WriteLine(string.Equals(a, b, StringComparison.OrdinalIgnoreCase)); // True
Console.WriteLine(string.Compare("apple", "banana"));              // a negative number
Console.WriteLine(string.IsNullOrWhiteSpace("   "));               // True
```

`string.IsNullOrEmpty` and `string.IsNullOrWhiteSpace` are handy for validating input.

## Special literals

```csharp
string path = @"C:\Users\student\Labs";       // verbatim string: \ is not an escape
string multi = """
    Multi-line text
    with unescaped "quotes"
    """;                                       // C# 11: raw literal
string name = "Anna";
string greet = $"Hello, {name}!";              // interpolation
string both = $@"Folder: {path}\{name}";       // interpolation + verbatim
```

## StringBuilder

Every `+=` creates a new string. Building text from thousands of pieces in a loop this way is slow. `StringBuilder` exists for that:

```csharp
using System.Text;

var builder = new StringBuilder();
for (int i = 1; i <= 5; i++)
{
    builder.Append(i).Append(' ');
}
builder.AppendLine();
builder.Append("done");

string result = builder.ToString();
Console.WriteLine(result);
```

```output
1 2 3 4 5 
done
```

> For a few concatenations, `+` and interpolation are perfectly fine. `StringBuilder` is for strings built in a loop.
