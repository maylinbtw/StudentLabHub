# Searching and processing text

The strength of Linux lies in small commands chained together. Each one does a single thing with text, and the **pipe** `|` hands the result to the next one.

## Redirecting output

```bash
ls -l > list.txt             # write output to a file (overwrites it)
echo "lab3" >> done.txt      # append to the end of a file
sort < names.txt             # read input from a file
gcc main.c 2> errors.txt     # errors (stream 2) go to a separate file
make > build.log 2>&1        # both output and errors into one file
cmd > /dev/null              # discard the output
```

Every program has three streams: `0` — input (stdin), `1` — output (stdout), `2` — errors (stderr).

## Pipes

`|` sends the output of one command to the input of another:

```bash
ls -l /etc | less                    # page through a long list
ps aux | grep java                   # find java processes
cat access.log | wc -l               # number of lines in a log
history | tail -n 20                 # last 20 commands
```

## grep — finding lines

```bash
grep "error" app.log                 # lines containing error
grep -i "error" app.log              # case-insensitive
grep -n "TODO" main.c                # with line numbers
grep -r "main(" src/                 # in every file of a folder
grep -v "^#" config.ini              # lines that do NOT start with #
grep -c "GET" access.log             # only the number of matches
grep -E "lab[0-9]+" list.txt         # extended regular expressions
```

The most useful regex elements: `^` — start of line, `$` — end of line, `.` — any character, `*` — the previous item 0 or more times, `[0-9]` — any digit.

## wc, sort, uniq

```bash
wc -l main.c                         # lines
wc -w essay.txt                      # words
sort names.txt                       # alphabetically
sort -n numbers.txt                  # numerically
sort -r -k2 marks.txt                # by the second column, reversed
sort names.txt | uniq                # remove duplicates
sort names.txt | uniq -c | sort -rn  # count each line, most frequent first
```

> `uniq` removes only **adjacent** duplicates, which is why `sort` almost always comes first.

## cut and tr

```bash
cut -d ':' -f 1 /etc/passwd          # first column, ":" as separator
cut -d ',' -f 2,4 data.csv           # 2nd and 4th CSV columns
echo "Hello" | tr 'a-z' 'A-Z'        # translate characters: HELLO
tr -d '\r' < win.txt > unix.txt      # strip \r characters from a Windows file
```

## sed — stream editing

```bash
sed 's/old/new/' file.txt            # first match on each line
sed 's/old/new/g' file.txt           # every match
sed -i 's/8080/9090/g' config.ini    # edit the file itself
sed -n '10,20p' log.txt              # print lines 10 to 20
sed '/^$/d' file.txt                 # delete empty lines
```

## awk — working with columns

`awk` splits every line into fields: `$1`, `$2`… (`$0` is the whole line).

```bash
awk '{print $1}' access.log                  # first column
awk -F ',' '{print $2, $3}' data.csv         # CSV: 2nd and 3rd columns
awk '$3 > 8 {print $1}' marks.txt            # surnames with a mark above 8
awk '{sum += $2} END {print sum / NR}' marks.txt   # average of the 2nd column
```

## A chain example

The ten IP addresses that hit the server most often:

```bash
cut -d ' ' -f 1 access.log | sort | uniq -c | sort -rn | head -n 10
```

```output
    412 192.168.1.15
    208 192.168.1.40
     97 10.0.0.7
```

## Comparing files

```bash
diff old.c new.c                     # line-by-line differences
diff -u old.c new.c                  # patch format (as in git)
```

## Check yourself

> [!QUIZ]
> How does `>` differ from `>>`?
> ---
> `>` overwrites the file, `>>` appends to it.

> [!QUIZ]
> How do you count lines containing `error`?
> ---
> `grep -c error log.txt` or `grep error log.txt | wc -l`.
