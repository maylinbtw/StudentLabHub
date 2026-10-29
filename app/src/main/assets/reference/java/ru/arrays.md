# Массивы

**Массив** хранит фиксированное число элементов одного типа. Индексы начинаются с нуля.

## Создание

```java
int[] marks = new int[5];              // 5 нулей
int[] scores = {7, 9, 4};              // со значениями
String[] days = new String[]{"Пн", "Вт", "Ср"};
```

Элементы нового массива получают значения по умолчанию: `0` для чисел, `false` для `boolean`, `null` для объектов.

## Доступ

```java
System.out.println(scores[0]);        // 7
scores[2] = 10;
System.out.println(scores.length);    // 3 — поле, без скобок
System.out.println(scores[scores.length - 1]); // последний
```

> [!WARNING]
> Индекс за пределами массива выбрасывает `ArrayIndexOutOfBoundsException`. Последний допустимый индекс — `length - 1`.

## Перебор

```java
for (int i = 0; i < scores.length; i++) {
    System.out.println("Работа " + (i + 1) + ": " + scores[i]);
}

int sum = 0;
for (int s : scores) {
    sum += s;
}
```

## Вывод массива

`System.out.println(scores)` напечатает что-то вроде `[I@1b6d3586` — адрес, а не элементы. Используйте `Arrays.toString`:

```java
import java.util.Arrays;

int[] a = {5, 2, 9};
System.out.println(Arrays.toString(a));   // [5, 2, 9]
```

## Класс Arrays

```java
int[] a = {5, 2, 9, 1};

Arrays.sort(a);                            // [1, 2, 5, 9]
int idx = Arrays.binarySearch(a, 5);       // 2 — только в отсортированном
int[] copy = Arrays.copyOf(a, a.length);   // независимая копия
int[] part = Arrays.copyOfRange(a, 1, 3);  // [2, 5]
Arrays.fill(a, 0);                         // [0, 0, 0, 0]
boolean same = Arrays.equals(copy, part);  // false
int max = Arrays.stream(copy).max().getAsInt(); // 9
```

## Массив — ссылочный тип

```java
int[] a = {1, 2, 3};
int[] b = a;          // копируется ссылка
b[0] = 100;
System.out.println(a[0]); // 100
```

Сравнение `a == b` проверяет, один ли это массив. Содержимое сравнивают через `Arrays.equals`.

## Двумерные массивы

```java
int[][] table = {
    {1, 2, 3},
    {4, 5, 6}
};

for (int[] row : table) {
    for (int x : row) {
        System.out.print(x + " ");
    }
    System.out.println();
}

System.out.println(table.length);     // 2 — строк
System.out.println(table[0].length);  // 3 — столбцов
```

Строки могут быть разной длины:

```java
int[][] jagged = new int[3][];
jagged[0] = new int[]{1};
jagged[1] = new int[]{1, 2};
jagged[2] = new int[]{1, 2, 3};
```

> [!NOTE]
> Размер массива изменить нельзя. Если элементы добавляются по ходу работы, используйте `ArrayList` — см. главу «ООП и коллекции».
