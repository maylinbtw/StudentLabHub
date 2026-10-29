# Environment variables and shell setup

**Environment variables** are "name = value" pairs that the shell passes to every program it starts. Programs use them to learn the home folder, the language, the command search path and so on.

## Viewing

```bash
echo $HOME          # /home/student
echo $USER          # student
echo $SHELL         # /bin/bash
printenv            # all environment variables
printenv PATH       # one variable
```

| Variable | Meaning |
|---|---|
| `HOME` | home folder |
| `USER` | user name |
| `PATH` | folders where commands are searched |
| `PWD` | current folder |
| `LANG` | language and encoding |
| `EDITOR` | default editor |

## Creating variables

```bash
NAME="Anna"               # a shell variable — only here
echo "Hello, $NAME"

export GROUP="P-21"       # an environment variable — visible to programs
python3 -c 'import os; print(os.environ["GROUP"])'   # P-21

MODE=debug ./app          # for a single run only
unset NAME                # delete
```

> [!WARNING]
> There must be no spaces around `=`. With `NAME = "Anna"` the shell tries to run a command called `NAME`.

## PATH

When you type `python3`, the shell looks for a `python3` file in the `PATH` folders in order:

```bash
echo $PATH
```

```output
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
```

Adding your own scripts folder:

```bash
mkdir -p ~/bin
export PATH="$HOME/bin:$PATH"
```

Now scripts from `~/bin` run by name from any folder.

## Where to save settings: .bashrc

Anything set by a command in the terminal is lost when it closes. To apply settings in every new terminal, put them into `~/.bashrc`:

```bash
nano ~/.bashrc
```

Add at the end:

```bash
export PATH="$HOME/bin:$PATH"
export EDITOR=nano
alias ll='ls -alF'
```

Apply without restarting the terminal:

```bash
source ~/.bashrc
```

## alias — short command names

```bash
alias ll='ls -alF'
alias gs='git status'
alias update='sudo apt update && sudo apt upgrade -y'
alias ..='cd ..'

alias              # list all aliases
unalias gs         # remove
```

## Command prompt

The prompt look is set by the `PS1` variable:

```bash
PS1='\u@\h:\w\$ '
```

| Code | Meaning |
|---|---|
| `\u` | user name |
| `\h` | host name |
| `\w` | current folder |
| `\t` | time |
| `\$` | `$` for a user, `#` for root |

## .env file in projects

Many projects keep settings in a `.env` file:

```text
DB_HOST=localhost
DB_PORT=5432
API_KEY=secret
```

> [!WARNING]
> A `.env` file with passwords and keys must not be added to git. Put it into `.gitignore`.

## Check yourself

> [!QUIZ]
> Why doesn't `NAME = "Anna"` work?
> ---
> There must be no spaces around `=`: otherwise the shell treats `NAME` as a command.

> [!QUIZ]
> Where do you put `alias` and `export` so they work in every terminal?
> ---
> In `~/.bashrc`, then apply with `source ~/.bashrc`.
