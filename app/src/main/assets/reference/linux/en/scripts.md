# Your first bash script

A **script** is a text file with commands that the shell runs in order. Anything you do by hand in the terminal can be written into a script and run with one command.

## Creating and running

```bash
nano hello.sh
```

```bash
#!/bin/bash
# My first script
echo "Hello, $USER!"
echo "Today is $(date +%d.%m.%Y)"
echo "You are in: $(pwd)"
```

```bash
chmod +x hello.sh     # allow running
./hello.sh            # run
```

```output
Hello, student!
Today is 29.10.2026
You are in: /home/student
```

- the first line `#!/bin/bash` is the **shebang**: it says which program runs the file;
- `#` at the start of a line is a comment;
- `$(command)` substitutes the command's output.

Without `chmod +x` the script can be run like this: `bash hello.sh`.

## Script arguments

```bash
#!/bin/bash
echo "Script name: $0"
echo "First argument: $1"
echo "Second argument: $2"
echo "Argument count: $#"
echo "All arguments: $@"
```

```bash
./args.sh lab3 urgent
```

```output
Script name: ./args.sh
First argument: lab3
Second argument: urgent
Argument count: 2
All arguments: lab3 urgent
```

## User input

```bash
#!/bin/bash
read -p "What's your name? " name
read -p "Lab number: " lab
echo "$name, creating a folder for lab $lab"
mkdir -p "lab$lab"
```

`read -s` hides the input — for passwords.

## Exit code

Every command returns an **exit code**: `0` is success, any other number is an error. The last code is stored in `$?`:

```bash
ls /nonexistent
echo $?          # 2 — error

ls ~
echo $?          # 0 — success
```

A script can return its own code with `exit`:

```bash
if [ $# -eq 0 ]; then
    echo "Please specify a file name"
    exit 1
fi
```

## Command chains

```bash
mkdir build && cd build        # the second runs only if the first succeeds
cd project || exit 1           # the second runs only if the first fails
make; echo "done"              # both run regardless
```

## Strict mode

Serious scripts start with:

```bash
#!/bin/bash
set -euo pipefail
```

| Option | What it does |
|---|---|
| `-e` | stop at the first error |
| `-u` | error when using an unset variable |
| `-o pipefail` | a pipeline fails if any part of it fails |

> [!WARNING]
> A script created on Windows may have `\r\n` line endings. Then bash reports `/bin/bash^M: bad interpreter`. Fix: `sed -i 's/\r$//' script.sh` or save the file with LF endings.

## Check yourself

> [!QUIZ]
> Why does a script start with `#!/bin/bash`?
> ---
> It's the shebang: it says which program should run the file.

> [!QUIZ]
> What does `$?` hold?
> ---
> The exit code of the last command: `0` means success, anything else an error.
