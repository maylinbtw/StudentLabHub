# Processes

A **process** is a running program. Every process has a **PID** number, an owner and a parent process that started it.

## ps — listing processes

```bash
ps                           # processes of the current terminal
ps aux                       # every process in the system
ps aux | grep firefox        # find the one you need
ps -ef --forest              # a tree: who started whom
pgrep -l java                # PIDs of processes by name
```

Main `ps aux` columns:

| Column | Meaning |
|---|---|
| `USER` | process owner |
| `PID` | process number |
| `%CPU`, `%MEM` | CPU and memory usage |
| `STAT` | state: `R` — running, `S` — sleeping, `Z` — zombie |
| `COMMAND` | the command line |

## top and htop — live monitors

```bash
top                          # the built-in monitor
htop                         # nicer: colors, mouse, scrolling
```

Keys in `top`: `P` — sort by CPU, `M` — by memory, `k` — kill a process, `q` — quit.

## Signals and stopping processes

You can send a process a **signal**. Most often — to stop it:

```bash
kill 4321                    # politely ask it to exit (SIGTERM)
kill -9 4321                 # kill it immediately (SIGKILL)
killall firefox              # every process with that name
pkill -f "python bot.py"     # by part of the command line
```

| Signal | Number | What it does |
|---|---|---|
| `SIGTERM` | 15 | asks to exit, the program can save its data |
| `SIGKILL` | 9 | immediate termination, cannot be caught |
| `SIGINT` | 2 | same as `Ctrl+C` |
| `SIGHUP` | 1 | terminal closed; many services reload their settings on it |

> Try a plain `kill` first. `kill -9` is a last resort: the program gets no chance to save files and release resources.

## Background jobs

```bash
./server &                   # run in the background, the terminal stays free
jobs                         # background jobs of this terminal
fg %1                        # bring job 1 back to the foreground
```

If a program is already running: `Ctrl+Z` suspends it, `bg` resumes it in the background.

A background job ends when the terminal closes. To keep it running after you log out:

```bash
nohup ./server > server.log 2>&1 &
```

## systemd services

Servers and background services in most distributions are managed with `systemctl`:

```bash
systemctl status ssh         # service state
sudo systemctl start ssh     # start
sudo systemctl stop ssh      # stop
sudo systemctl restart ssh   # restart
sudo systemctl enable ssh    # start on boot
journalctl -u ssh -f         # follow the service log
```

## System resources

```bash
free -h                      # memory
uptime                       # uptime and load average
nproc                        # number of CPU cores
```

## Check yourself

> [!QUIZ]
> How does `kill PID` differ from `kill -9 PID`?
> ---
> `kill` sends SIGTERM — a request to exit. `-9` (SIGKILL) kills it immediately with no chance to clean up.

> [!QUIZ]
> How do you run a command in the background?
> ---
> Add `&` at the end: `long_task &`.
