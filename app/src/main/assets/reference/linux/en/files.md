# Working with files and folders

Creating, copying, moving, deleting and viewing files is what you do in the terminal all the time.

## Creating

```bash
touch notes.txt              # empty file (or update the date of an existing one)
mkdir lab3                   # folder
mkdir -p labs/2026/lab3      # nested folders at once, no error if they exist
```

## Copying: cp

```bash
cp main.c main_backup.c      # copy a file
cp main.c ~/backup/          # copy into another folder
cp -r lab1 lab1_copy         # copy a whole folder (-r — recursive)
cp -i *.c src/               # ask before overwriting
```

## Moving and renaming: mv

`mv` both moves and renames — it is the same operation:

```bash
mv old.txt new.txt           # rename
mv report.odt ~/Documents/   # move
mv *.png images/             # move all png files
```

## Deleting: rm and rmdir

```bash
rm temp.txt                  # delete a file
rm -i *.log                  # ask for confirmation for each one
rmdir empty_dir              # delete an empty folder
rm -r old_lab                # delete a folder with everything in it
```

> [!WARNING]
> The terminal has no recycle bin: `rm` deletes for good. Be especially careful with `rm -rf` — it asks no questions. Before deleting by pattern, check what it matches: `ls *.log`.

## Wildcards

The shell replaces wildcards with matching file names:

| Pattern | Matches |
|---|---|
| `*` | any number of any characters |
| `?` | exactly one character |
| `[abc]` | one of the listed characters |
| `{a,b}` | each option in turn |

```bash
ls *.c                       # all files with the .c extension
ls lab?.txt                  # lab1.txt, lab2.txt, but not lab10.txt
cp report.{odt,pdf} ~/send/  # same as cp report.odt report.pdf ~/send/
```

## Viewing contents

```bash
cat notes.txt                # print the whole file
cat -n main.c                # with line numbers
less /var/log/syslog         # page through a file
head -n 20 data.csv          # first 20 lines
tail -n 5 data.csv           # last 5 lines
tail -f app.log              # follow a file in real time
```

Keys in `less`: space — page down, `b` — page up, `/text` — search, `n` — next match, `q` — quit.

## Finding files: find

```bash
find . -name "*.java"              # all .java files here and below
find ~ -iname "report*"            # case-insensitive
find . -type d -name build         # only folders named build
find . -size +10M                  # files larger than 10 MB
find . -mtime -1                   # modified within the last day
find . -name "*.tmp" -delete       # find and delete
```

## Links

```bash
ln -s /opt/idea/bin/idea.sh ~/idea     # symbolic link (shortcut)
ls -l ~/idea
```

```output
lrwxrwxrwx 1 student student 22 Oct 27 12:00 /home/student/idea -> /opt/idea/bin/idea.sh
```

## Archives

```bash
tar -czf lab3.tar.gz lab3/         # pack a folder into a .tar.gz archive
tar -xzf lab3.tar.gz               # unpack
zip -r lab3.zip lab3/              # zip archive
unzip lab3.zip                     # unpack a zip
```

Letters in `tar`: `c` — create, `x` — extract, `z` — gzip compression, `f` — archive file name, `v` — show progress.

## Check yourself

> [!QUIZ]
> How do you delete a folder with its contents?
> ---
> `rm -r folder`. There's no trash — deletion is permanent.

> [!QUIZ]
> How do you extract `lab3.tar.gz`?
> ---
> `tar -xzf lab3.tar.gz`.
