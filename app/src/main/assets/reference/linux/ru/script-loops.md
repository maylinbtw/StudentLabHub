# Циклы в bash

## for по списку

```bash
for subject in Математика Физика ОАиП; do
    echo "Предмет: $subject"
done
```

По файлам — с помощью маски:

```bash
for file in *.txt; do
    echo "$file: $(wc -l < "$file") строк"
done
```

> [!NOTE]
> Если файлов по маске нет, в переменную попадёт сама строка `*.txt`. Защита: `[ -e "$file" ] || continue` в начале тела цикла.

## for по числам

```bash
for i in {1..5}; do
    mkdir -p "lab$i"
done

for i in {0..20..5}; do      # с шагом 5: 0 5 10 15 20
    echo $i
done

for ((i = 1; i <= 3; i++)); do   # в стиле C
    echo "Итерация $i"
done
```

## while

```bash
count=1
while [ $count -le 3 ]; do
    echo "Попытка $count"
    ((count++))
done
```

Чтение файла построчно:

```bash
while IFS= read -r line; do
    echo "> $line"
done < students.txt
```

`IFS=` сохраняет пробелы в начале строки, `-r` не даёт обрабатывать `\`.

Разбор CSV:

```bash
while IFS=';' read -r name group mark; do
    echo "$name из $group: $mark"
done < marks.csv
```

## until

Выполняется, **пока условие ложно**:

```bash
until ping -c 1 -W 1 192.168.1.20 > /dev/null; do
    echo "Сервер недоступен, жду..."
    sleep 5
done
echo "Сервер в сети"
```

## break и continue

```bash
for file in *.log; do
    [ -s "$file" ] || continue      # пропустить пустые
    if grep -q "FATAL" "$file"; then
        echo "Критическая ошибка в $file"
        break                       # остановить цикл
    fi
done
```

## Перебор вывода команды

```bash
for user in $(cut -d: -f1 /etc/passwd | head -5); do
    echo "Пользователь: $user"
done

find . -name "*.cpp" | while read -r f; do
    echo "Исходник: $f"
done
```

## Пример: переименование файлов

Заменить пробелы в именах на `_`:

```bash
for f in *\ *; do
    [ -e "$f" ] || continue
    mv -- "$f" "${f// /_}"
done
```

## Пример: резервная копия

```bash
#!/bin/bash
src="$HOME/labs"
dest="$HOME/backup"
mkdir -p "$dest"

for dir in "$src"/*/; do
    name=$(basename "$dir")
    tar -czf "$dest/${name}_$(date +%F).tar.gz" -C "$src" "$name"
    echo "Сохранено: $name"
done
```

> [!WARNING]
> Бесконечный цикл `while true; do ... done` останавливается только `break`, `exit` или `Ctrl+C`. Не забывайте условие выхода.

## Проверь себя

> [!QUIZ]
> Как перебрать числа от 1 до 5?
> ---
> `for i in {1..5}; do ... done`.

> [!QUIZ]
> Как прочитать файл построчно?
> ---
> `while IFS= read -r line; do ... done < файл`.
