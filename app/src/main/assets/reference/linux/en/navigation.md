# The file system and navigation

Linux has no `C:` or `D:` drives. All files live in a single tree that starts at the **root** `/`. USB sticks and other disks are attached (mounted) to folders in that tree.

## Main directories

| Directory | Contents |
|---|---|
| `/` | the root of the file system |
| `/home` | users' home folders (`/home/student`) |
| `/root` | the administrator's home folder |
| `/etc` | configuration files of the system and programs |
| `/bin`, `/usr/bin` | programs (commands) |
| `/var/log` | system logs |
| `/tmp` | temporary files, cleared on reboot |
| `/dev` | device files: disks, terminals |
| `/mnt`, `/media` | mount points for disks and USB sticks |

## Absolute and relative paths

- an **absolute** path starts with `/` and gives the location from the root: `/home/student/labs/lab1.c`;
- a **relative** path is counted from the current folder: `labs/lab1.c`.

Special notations:

| Notation | Meaning |
|---|---|
| `~` | the current user's home folder |
| `.` | the current folder |
| `..` | the parent folder (one level up) |
| `-` | the previous folder (for `cd`) |

> File names are case-sensitive: `Lab.txt` and `lab.txt` are two different files. Spaces in names must be escaped: `cd "My Labs"` or `cd My\ Labs`.

## pwd — where am I

```bash
pwd
```

```output
/home/student/labs
```

## cd — change folder

```bash
cd /var/log        # by absolute path
cd labs/lab2       # by relative path
cd ..              # one level up
cd ../..           # two levels up
cd ~               # home (same as plain cd)
cd -               # back to the previous folder
```

## ls — folder contents

```bash
ls                 # names of files and folders
ls -l              # long listing: permissions, owner, size, date
ls -a              # show hidden files (their names start with a dot)
ls -lh             # human-readable sizes: 4.0K, 12M
ls -lt             # sort by modification time
ls -R              # recursively, with all subfolders
ls /etc            # list another folder without entering it
```

Sample `ls -lh` output:

```output
drwxr-xr-x 2 student student 4.0K Oct 27 10:12 lab1
-rw-r--r-- 1 student student  12K Oct 27 11:05 report.odt
```

The first character of a line: `d` — a folder, `-` — a regular file, `l` — a link. The rest are permissions, covered in a separate article.

## tree — directory tree

```bash
tree -L 2          # structure two levels deep
```

If the command is missing, install it: `sudo apt install tree` (Debian, Ubuntu).

## Disk usage

```bash
df -h              # free space on disks
du -sh labs        # how much the labs folder takes
du -sh *           # size of each item in the current folder
```

## Check yourself

> [!QUIZ]
> Where do `cd ~` and `cd -` take you?
> ---
> `cd ~` goes home, `cd -` goes to the previous folder.

> [!QUIZ]
> How do you show hidden files?
> ---
> `ls -a`: files whose names start with a dot are hidden.
