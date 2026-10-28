# Циклы

В Java четыре вида циклов: `for`, расширенный `for` (for-each), `while` и `do…while`.

## for

```java
for (int i = 1; i <= 5; i++) {
    System.out.print(i + " ");
}
System.out.println();
```

```output
1 2 3 4 5 
```

## for-each

Перебирает массивы и коллекции без индекса:

```java
int[] marks = {8, 6, 9, 7};
int sum = 0;
for (int m : marks) {
    sum += m;
}
System.out.printf("Среднее: %.2f%n", (double) sum / marks.length);
```

```output
Среднее: 7,50
```

```java
List<String> labs = List.of("ЛР1", "ЛР2", "ЛР3");
for (String lab : labs) {
    System.out.println(lab);
}
```

> Внутри for-each нельзя удалять элементы из коллекции — будет `ConcurrentModificationException`. Используйте `removeIf` или итератор.

## while и do…while

```java
int n = 1234, digits = 0;
while (n > 0) {
    n /= 10;
    digits++;
}
```

```java
int choice;
do {
    System.out.print("1 — добавить, 0 — выход: ");
    choice = scanner.nextInt();
} while (choice != 0);
```

## break и continue

```java
for (int i = 1; i <= 20; i++) {
    if (i % 3 != 0) continue;
    if (i > 12) break;
    System.out.print(i + " ");   // 3 6 9 12
}
```

## Метки для вложенных циклов

В Java можно выйти сразу из нескольких циклов с помощью **метки**:

```java
outer:
for (int row = 0; row < 5; row++) {
    for (int col = 0; col < 5; col++) {
        if (grid[row][col] == target) {
            System.out.println("Найдено: " + row + ", " + col);
            break outer;
        }
    }
}
```

`continue outer;` переходит к следующему шагу внешнего цикла.

## Перебор с индексом и обратный порядок

```java
String[] days = {"Пн", "Вт", "Ср"};

for (int i = 0; i < days.length; i++) {
    System.out.println((i + 1) + ". " + days[i]);
}

for (int i = days.length - 1; i >= 0; i--) {
    System.out.print(days[i] + " ");   // Ср Вт Пн
}
```

## Потоки вместо циклов

Многие циклы короче записать через Stream API:

```java
int[] marks = {8, 6, 9, 7};
double avg = Arrays.stream(marks).average().orElse(0);
long good = Arrays.stream(marks).filter(m -> m >= 8).count();   // 2
```
