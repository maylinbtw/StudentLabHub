# Функции в bash

Функции позволяют дать имя повторяющемуся куску скрипта.

## Объявление и вызов

```bash
#!/bin/bash

greet() {
    echo "Привет, $1!"
}

greet "Анна"
greet "Олег"
```

```output
Привет, Анна!
Привет, Олег!
```

- функцию нужно объявить **до** вызова;
- аргументы вызываются без скобок, через пробел;
- внутри функции аргументы доступны как `$1`, `$2`, …, `$#`, `$@` — как у скрипта.

## Локальные переменные

По умолчанию переменные в функции глобальные. Слово `local` ограничивает их функцией:

```bash
count=10

reset() {
    local count=0
    echo "Внутри: $count"
}

reset
echo "Снаружи: $count"
```

```output
Внутри: 0
Снаружи: 10
```

## Возврат результата

`return` возвращает только **код завершения** (0–255). Чтобы вернуть данные, функция печатает их, а вызывающий код забирает через `$(...)`:

```bash
sum() {
    echo $(( $1 + $2 ))
}

result=$(sum 7 5)
echo "Сумма: $result"     # Сумма: 12
```

Код завершения — для «да/нет»:

```bash
is_number() {
    [[ "$1" =~ ^[0-9]+$ ]]
}

if is_number "$1"; then
    echo "$1 — число"
else
    echo "$1 — не число"
fi
```

## Проверка аргументов

```bash
create_lab() {
    if [ $# -lt 2 ]; then
        echo "Использование: create_lab <предмет> <номер>" >&2
        return 1
    fi
    local dir="$1/lab$2"
    mkdir -p "$dir"
    touch "$dir/report.md"
    echo "Создано: $dir"
}

create_lab ОАиП 3
create_lab ОАиП || echo "Ошибка"
```

`>&2` выводит сообщение в поток ошибок.

## Пример: меню

```bash
#!/bin/bash

show_menu() {
    echo "1) Список лаб"
    echo "2) Новая лаба"
    echo "0) Выход"
}

list_labs() {
    ls -d lab*/ 2>/dev/null || echo "Лаб пока нет"
}

new_lab() {
    local n
    read -p "Номер: " n
    mkdir -p "lab$n" && echo "Создана lab$n"
}

while true; do
    show_menu
    read -p "> " choice
    case "$choice" in
        1) list_labs ;;
        2) new_lab ;;
        0) break ;;
        *) echo "Нет такого пункта" ;;
    esac
done
```

## Библиотека функций

Общие функции можно вынести в отдельный файл и подключить через `source`:

```bash
# utils.sh
log() {
    echo "[$(date +%T)] $*"
}
```

```bash
# main.sh
#!/bin/bash
source ./utils.sh
log "Скрипт запущен"
```

## Отладка

```bash
bash -x script.sh        # печатать каждую команду перед выполнением
```

Или включить отладку для части скрипта:

```bash
set -x
# ... проблемный участок ...
set +x
```

> [!NOTE]
> Для проверки скриптов на типичные ошибки есть утилита `shellcheck`: `sudo apt install shellcheck`, затем `shellcheck script.sh`.
