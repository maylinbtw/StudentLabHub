# Functions in bash

Functions let you give a name to a repeated piece of a script.

## Declaring and calling

```bash
#!/bin/bash

greet() {
    echo "Hello, $1!"
}

greet "Anna"
greet "Oleg"
```

```output
Hello, Anna!
Hello, Oleg!
```

- a function must be declared **before** it is called;
- arguments are passed without parentheses, separated by spaces;
- inside the function arguments are `$1`, `$2`, …, `$#`, `$@` — just like in a script.

## Local variables

By default variables in a function are global. `local` limits them to the function:

```bash
count=10

reset() {
    local count=0
    echo "Inside: $count"
}

reset
echo "Outside: $count"
```

```output
Inside: 0
Outside: 10
```

## Returning a result

`return` returns only an **exit code** (0–255). To return data, the function prints it, and the caller captures it with `$(...)`:

```bash
sum() {
    echo $(( $1 + $2 ))
}

result=$(sum 7 5)
echo "Sum: $result"     # Sum: 12
```

The exit code is for yes/no answers:

```bash
is_number() {
    [[ "$1" =~ ^[0-9]+$ ]]
}

if is_number "$1"; then
    echo "$1 is a number"
else
    echo "$1 is not a number"
fi
```

## Checking arguments

```bash
create_lab() {
    if [ $# -lt 2 ]; then
        echo "Usage: create_lab <subject> <number>" >&2
        return 1
    fi
    local dir="$1/lab$2"
    mkdir -p "$dir"
    touch "$dir/report.md"
    echo "Created: $dir"
}

create_lab Programming 3
create_lab Programming || echo "Error"
```

`>&2` sends the message to the error stream.

## Example: a menu

```bash
#!/bin/bash

show_menu() {
    echo "1) List labs"
    echo "2) New lab"
    echo "0) Exit"
}

list_labs() {
    ls -d lab*/ 2>/dev/null || echo "No labs yet"
}

new_lab() {
    local n
    read -p "Number: " n
    mkdir -p "lab$n" && echo "Created lab$n"
}

while true; do
    show_menu
    read -p "> " choice
    case "$choice" in
        1) list_labs ;;
        2) new_lab ;;
        0) break ;;
        *) echo "No such option" ;;
    esac
done
```

## A function library

Shared functions can go into a separate file loaded with `source`:

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
log "Script started"
```

## Debugging

```bash
bash -x script.sh        # print each command before running it
```

Or enable debugging for part of a script:

```bash
set -x
# ... the problem area ...
set +x
```

> [!NOTE]
> The `shellcheck` tool checks scripts for common mistakes: `sudo apt install shellcheck`, then `shellcheck script.sh`.
