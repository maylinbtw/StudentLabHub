# Variables and data types

Java is statically and strongly typed. Types fall into **primitive** ones (numbers, characters, boolean) and **reference** ones (strings, arrays, class objects).

## Primitive types

| Type | Size | Range / example |
|---|---|---|
| `byte` | 1 byte | −128 … 127 |
| `short` | 2 bytes | −32,768 … 32,767 |
| `int` | 4 bytes | about ±2.1 billion |
| `long` | 8 bytes | about ±9.2·10¹⁸, literal `10L` |
| `float` | 4 bytes | ~7 digits, literal `3.5f` |
| `double` | 8 bytes | ~15 digits, `3.5` |
| `char` | 2 bytes | a Unicode character: `'Z'` |
| `boolean` | — | `true` or `false` |

Unlike C++, type sizes are the same on every platform, and there are no unsigned types.

```java
int labs = 12;
long fileSize = 3_000_000_000L;   // without L — error: the number does not fit into int
double avg = 7.5;
char grade = 'A';
boolean passed = true;
```

## Strings

`String` is a reference type, but strings can be used almost like primitives:

```java
String name = "Anna";
String greet = "Hello, " + name + "!";
int len = name.length();             // 4
char first = name.charAt(0);         // 'A'
String upper = name.toUpperCase();   // "ANNA"
```

> [!WARNING]
> Compare strings with `equals`, not `==`. The `==` operator checks whether it is the same object and may return `false` for identical text.

```java
String input = scanner.nextLine();
if (input.equals("exit")) { ... }
if (input.equalsIgnoreCase("EXIT")) { ... }
```

## var

Since Java 10, the type of a local variable can be inferred:

```java
var count = 10;           // int
var title = "Report";     // String
var list = new ArrayList<String>();
```

## Constants

```java
final int MAX_LABS = 20;          // the value cannot change
static final double PI = 3.14159; // a class constant
```

## Default values

Class fields get default values: `0`, `0.0`, `false`, `'\u0000'` and `null` for references. Local variables do not: reading an uninitialized local variable does not compile.

## Type conversion

```java
int i = 100;
long l = i;                  // widening — automatic
double d = i;                // 100.0

double price = 9.99;
int whole = (int) price;     // 9 — narrowing only explicitly

int n = Integer.parseInt("42");          // string → number
double x = Double.parseDouble("3.5");
String s = String.valueOf(42);           // number → string
String t = "" + 42;                      // works too
```

## Wrapper classes

Every primitive has a class: `Integer`, `Double`, `Character`, `Boolean`… They are needed, for example, in collections — you cannot write `ArrayList<int>`, only `ArrayList<Integer>`. Conversion between them is automatic (autoboxing).

```java
Integer boxed = 5;          // int → Integer
int raw = boxed;            // Integer → int
int max = Integer.MAX_VALUE;
```

## Check yourself

> [!QUIZ]
> How does `int` differ from `Integer`?
> ---
> `int` is a primitive, `Integer` is a wrapper class. Wrappers are used in collections and can be `null`.

> [!QUIZ]
> What does `final int MAX = 10;` mean?
> ---
> A constant: the value can't be reassigned.
