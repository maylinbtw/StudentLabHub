# Variables and arithmetic in bash

## Variables

```bash
name="Anna"
lab=3
echo "Student: $name, lab $lab"
echo "File: ${name}_lab${lab}.txt"   # braces mark where the name ends
```

```output
Student: Anna, lab 3
File: Anna_lab3.txt
```

> [!WARNING]
> Don't put spaces around `=` when assigning: `lab = 3` gives `lab: command not found`.

## Quotes

```bash
name="Anna"
echo "Hello, $name"    # double — variables are substituted: Hello, Anna
echo 'Hello, $name'    # single — text as is: Hello, $name
```

Always put variables in double quotes: `"$file"`. Otherwise a name with spaces (`my labs.txt`) splits into two arguments.

## Command substitution

```bash
today=$(date +%Y-%m-%d)
files=$(ls | wc -l)
echo "Today is $today, files in folder: $files"
```

## Arithmetic

bash works with integers only:

```bash
a=7
b=3
echo $((a + b))     # 10
echo $((a * b))     # 21
echo $((a / b))     # 2 — integer division
echo $((a % b))     # 1 — remainder

count=0
((count++))
((count += 5))
echo $count         # 6
```

For decimals use `bc`:

```bash
echo "scale=2; 17 / 3" | bc     # 5.66
```

## String operations

```bash
file="report_lab3.docx"

echo ${#file}            # 16 — length
echo ${file%.docx}       # report_lab3 — strip from the end
echo ${file#report_}     # lab3.docx — strip from the start
echo ${file/lab3/lab4}   # report_lab4.docx — replace
echo ${file:0:6}         # report — substring
echo ${file^^}           # REPORT_LAB3.DOCX — to upper case
```

Default value:

```bash
dir=${1:-.}              # the first argument or "." if not given
echo "Processing folder $dir"
```

## Arrays

```bash
subjects=("Math" "Physics" "Programming")

echo ${subjects[0]}          # Math
echo ${#subjects[@]}         # 3 — count
subjects+=("DB")             # add

for s in "${subjects[@]}"; do
    echo "- $s"
done
```

## Special variables

| Variable | Meaning |
|---|---|
| `$0` | script name |
| `$1`…`$9` | arguments |
| `$#` | argument count |
| `$@` | all arguments as a list |
| `$?` | exit code of the last command |
| `$$` | PID of the current script |
| `$RANDOM` | a random number 0–32767 |

## Constants and scope

```bash
readonly MAX_LABS=10
# MAX_LABS=5 — error: read-only variable
```

Script variables are **global** by default and aren't visible to programs it starts until they are `export`ed.

## Check yourself

> [!QUIZ]
> How does `"$name"` differ from `'$name'`?
> ---
> Double quotes substitute the variable, single quotes keep the text as is.

> [!QUIZ]
> What does `echo $((7 / 2))` print?
> ---
> `3`: bash works with integers only.
