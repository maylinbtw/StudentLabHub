# Loops in bash

## for over a list

```bash
for subject in Math Physics Programming; do
    echo "Subject: $subject"
done
```

Over files — with a pattern:

```bash
for file in *.txt; do
    echo "$file: $(wc -l < "$file") lines"
done
```

> [!NOTE]
> If no files match, the variable gets the literal string `*.txt`. Guard: `[ -e "$file" ] || continue` at the start of the loop body.

## for over numbers

```bash
for i in {1..5}; do
    mkdir -p "lab$i"
done

for i in {0..20..5}; do      # step 5: 0 5 10 15 20
    echo $i
done

for ((i = 1; i <= 3; i++)); do   # C style
    echo "Iteration $i"
done
```

## while

```bash
count=1
while [ $count -le 3 ]; do
    echo "Attempt $count"
    ((count++))
done
```

Reading a file line by line:

```bash
while IFS= read -r line; do
    echo "> $line"
done < students.txt
```

`IFS=` keeps leading spaces, `-r` stops `\` from being processed.

Parsing CSV:

```bash
while IFS=';' read -r name group mark; do
    echo "$name from $group: $mark"
done < marks.csv
```

## until

Runs **while the condition is false**:

```bash
until ping -c 1 -W 1 192.168.1.20 > /dev/null; do
    echo "Server unreachable, waiting..."
    sleep 5
done
echo "Server is online"
```

## break and continue

```bash
for file in *.log; do
    [ -s "$file" ] || continue      # skip empty ones
    if grep -q "FATAL" "$file"; then
        echo "Critical error in $file"
        break                       # stop the loop
    fi
done
```

## Iterating command output

```bash
for user in $(cut -d: -f1 /etc/passwd | head -5); do
    echo "User: $user"
done

find . -name "*.cpp" | while read -r f; do
    echo "Source: $f"
done
```

## Example: renaming files

Replace spaces in names with `_`:

```bash
for f in *\ *; do
    [ -e "$f" ] || continue
    mv -- "$f" "${f// /_}"
done
```

## Example: backup

```bash
#!/bin/bash
src="$HOME/labs"
dest="$HOME/backup"
mkdir -p "$dest"

for dir in "$src"/*/; do
    name=$(basename "$dir")
    tar -czf "$dest/${name}_$(date +%F).tar.gz" -C "$src" "$name"
    echo "Saved: $name"
done
```

> [!WARNING]
> An infinite loop `while true; do ... done` stops only with `break`, `exit` or `Ctrl+C`. Don't forget the exit condition.
