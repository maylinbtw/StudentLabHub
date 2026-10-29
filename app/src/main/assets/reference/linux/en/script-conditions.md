# Conditions in bash: if, test, case

## if

```bash
#!/bin/bash
read -p "Mark: " mark

if [ "$mark" -ge 9 ]; then
    echo "Excellent"
elif [ "$mark" -ge 4 ]; then
    echo "Passed"
else
    echo "Failed"
fi
```

> [!WARNING]
> Spaces inside `[ ]` are required: `[ "$a" -eq 1 ]`. Writing `["$a" -eq 1]` gives `command not found`.

## Comparing numbers

| Operator | Meaning |
|---|---|
| `-eq` | equal |
| `-ne` | not equal |
| `-lt` / `-le` | less / less or equal |
| `-gt` / `-ge` | greater / greater or equal |

Inside double parentheses you can use the familiar signs:

```bash
if (( mark >= 4 && mark <= 10 )); then
    echo "The mark is in range"
fi
```

## Comparing strings

| Form | Meaning |
|---|---|
| `[ "$a" = "$b" ]` | strings are equal |
| `[ "$a" != "$b" ]` | not equal |
| `[ -z "$a" ]` | string is empty |
| `[ -n "$a" ]` | string is not empty |

```bash
if [ -z "$1" ]; then
    echo "Usage: $0 <file name>"
    exit 1
fi
```

## File checks

| Form | True if |
|---|---|
| `-e path` | exists |
| `-f path` | is a regular file |
| `-d path` | is a directory |
| `-r` / `-w` / `-x` | readable / writable / executable |
| `-s path` | the file is not empty |

```bash
if [ -d "build" ]; then
    echo "The build folder already exists"
else
    mkdir build
fi

[ -f config.txt ] || echo "No config.txt"
```

## Logical operators

```bash
if [ -f "$file" ] && [ -r "$file" ]; then
    cat "$file"
fi

if [[ "$ext" == "cpp" || "$ext" == "c" ]]; then
    echo "C/C++ source"
fi
```

`[[ ]]` is bash's extended test: `&&`, `||`, patterns and regular expressions work inside it:

```bash
if [[ "$file" == *.txt ]]; then
    echo "A text file"
fi

if [[ "$group" =~ ^P-[0-9]{2}$ ]]; then
    echo "The group number is valid"
fi
```

## Checking a command's result

`if` checks a command's exit code — `[ ]` is just a command too:

```bash
if grep -q "TODO" main.py; then
    echo "There are TODOs left in the code"
fi

if ping -c 1 -W 1 8.8.8.8 > /dev/null; then
    echo "Internet is available"
fi
```

## case

Handy when one value is compared against several options:

```bash
#!/bin/bash
case "$1" in
    build)
        echo "Building..."
        ;;
    run|start)
        echo "Starting..."
        ;;
    *.zip)
        echo "Extracting $1"
        unzip "$1"
        ;;
    *)
        echo "Usage: $0 {build|run|archive.zip}"
        exit 1
        ;;
esac
```

- each branch ends with `;;`;
- `|` puts several options in one branch;
- `*` matches everything else, like `default`.

## Check yourself

> [!QUIZ]
> Which `[ ]` operator means "greater or equal" for numbers?
> ---
> `-ge`.

> [!QUIZ]
> How do you check that the `build` folder exists?
> ---
> `[ -d build ]`.
