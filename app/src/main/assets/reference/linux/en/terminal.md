# The terminal and the command line

A **terminal** is a program where you type commands as text. The commands are run by a **shell**. Most distributions use `bash`, some use `zsh`.

## The prompt

When the shell is ready for a command, it shows a **prompt**:

```text
student@pc:~/labs$
```

- `student` — the user name;
- `pc` — the computer name;
- `~/labs` — the current folder (`~` means the home folder);
- `$` — a regular user, `#` — the administrator (root).

> The examples in this reference omit the prompt: copy only the command itself.

## Command syntax

```text
command [options] [arguments]
```

```bash
ls -l -a /etc
ls -la /etc        # short options can be combined
ls --all /etc      # long form of an option
```

- the **command** is the program name: `ls`, `cp`, `grep`;
- **options** (flags) change the behavior: short ones start with `-`, long ones with `--`;
- **arguments** are what the command works on: files, folders, text.

Case matters: `ls` and `LS` are different commands, `-a` and `-A` are different options.

## Getting help

```bash
ls --help          # short help right in the terminal
man ls             # the full manual (press q to quit)
type cd            # what it is: a builtin, a program or an alias
```

> If a command is missing from this reference, search for it: a short page can be downloaded from tldr-pages and stays available offline.

## Handy keys

| Keys | Action |
|---|---|
| `Tab` | complete a command or file name |
| `Tab Tab` | show all completion options |
| `↑` / `↓` | browse the command history |
| `Ctrl+C` | interrupt the running command |
| `Ctrl+L` | clear the screen (same as `clear`) |
| `Ctrl+R` | search the history |
| `Ctrl+D` | exit the shell |

`Tab` is your best friend: it saves time and prevents typos in paths.

## Command history

```bash
history            # recent commands with numbers
!42                # repeat command number 42
!!                 # repeat the last command
sudo !!            # repeat the last command as administrator
```

## Several commands on one line

```bash
mkdir lab1 ; cd lab1          # run one after another
make && ./app                 # run the second only if the first succeeds
ping -c1 host || echo "offline"   # run the second only if the first fails
```

Every command returns an **exit code**: `0` means success, any other number means an error. `echo $?` shows the exit code of the last command.

## Check yourself

> [!QUIZ]
> How do you quickly find a command you typed earlier?
> ---
> `Ctrl+R` and start typing — history search. There's also `history` and the up arrow.

> [!QUIZ]
> How does `cmd1 && cmd2` differ from `cmd1; cmd2`?
> ---
> With `&&` the second runs only if the first succeeds. With `;` it always runs.
