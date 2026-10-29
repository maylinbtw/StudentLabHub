# Text editors: nano and vim

On a server or in a terminal without a GUI, files are edited with console editors. The most common are `nano` (simple) and `vim` (powerful but unusual).

## nano

```bash
nano notes.txt          # open or create a file
nano +15 main.c         # open at line 15
```

Key hints are shown at the bottom of the screen. `^` means `Ctrl`, `M-` means `Alt`.

| Keys | Action |
|---|---|
| `Ctrl+O`, then `Enter` | save |
| `Ctrl+X` | exit (asks about saving) |
| `Ctrl+W` | search |
| `Ctrl+\` | search and replace |
| `Ctrl+K` | cut the line |
| `Ctrl+U` | paste |
| `Ctrl+_` | go to line |
| `Alt+U` | undo |

## vim

`vim` works in **modes** — that's the key thing to understand:

| Mode | How to enter | What for |
|---|---|---|
| normal | `Esc` | moving and commands |
| insert | `i` | typing text |
| command-line | `:` from normal | saving, quitting, replacing |

```bash
vim main.py
```

The minimal flow: `i` → type text → `Esc` → `:wq` → `Enter`.

## How to exit vim

| Command | Action |
|---|---|
| `:w` | save |
| `:q` | quit |
| `:wq` or `:x` | save and quit |
| `:q!` | quit **without** saving |

> [!NOTE]
> If vim ignores your commands, you are probably in insert mode. Press `Esc` and try again.

## Normal mode commands

| Keys | Action |
|---|---|
| `h j k l` | left, down, up, right (arrow keys work too) |
| `gg` / `G` | to the start / end of the file |
| `0` / `$` | to the start / end of the line |
| `dd` | delete (cut) the line |
| `yy` | copy the line |
| `p` | paste after the cursor |
| `u` | undo |
| `Ctrl+r` | redo |
| `/text` | search, `n` — next match |
| `o` | new line below and insert mode |

## Replacing

```text
:%s/old/new/g
```

`%` means the whole file, `g` means all matches in a line. Add `c` at the end (`/gc`) to confirm each replacement.

## Useful settings

The `~/.vimrc` file:

```text
set number
syntax on
set tabstop=4
set shiftwidth=4
set expandtab
```

## Which one to choose

- quick config edits — `nano`;
- lots of server work — learn the basics of `vim`: it's available almost everywhere;
- for everyday development, VS Code or an IDE is more convenient.

The default editor (for example, for `git commit`) is set with the `EDITOR` variable:

```bash
export EDITOR=nano
```

## Check yourself

> [!QUIZ]
> How do you quit vim without saving?
> ---
> `Esc`, then `:q!` and `Enter`.

> [!QUIZ]
> How do you save a file in nano?
> ---
> `Ctrl+O`, then `Enter`. Exit with `Ctrl+X`.
