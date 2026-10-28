# Permissions

Every file in Linux has an **owner**, a **group** and a set of **permissions**: who may read, change and run it.

## Reading permissions

```bash
ls -l script.sh
```

```output
-rwxr-xr-- 1 student students 312 Oct 27 14:02 script.sh
```

The `rwxr-xr--` string consists of three triplets:

| Triplet | For whom | Here |
|---|---|---|
| `rwx` | the owner (user, `u`) | read, write, execute |
| `r-x` | the group (group, `g`) | read and execute |
| `r--` | everyone else (others, `o`) | read only |

The letters mean:

| Letter | For a file | For a folder |
|---|---|---|
| `r` | read the contents | list the files |
| `w` | modify | create and delete files inside |
| `x` | run as a program | enter the folder (`cd`) |

## chmod — change permissions

**Symbolic** form: for whom (`u`, `g`, `o`, `a` — all), what to do (`+`, `-`, `=`), which permissions:

```bash
chmod +x script.sh           # allow everyone to run it
chmod u+x script.sh          # only the owner
chmod go-w report.txt        # deny writing to group and others
chmod a=r notes.txt          # read-only for everyone
chmod -R g+w project/        # recursively for a folder
```

**Numeric** form: each triplet is a sum of `r = 4`, `w = 2`, `x = 1`:

| Number | Permissions | Typical use |
|---|---|---|
| `755` | `rwxr-xr-x` | programs, scripts, folders |
| `644` | `rw-r--r--` | regular files |
| `700` | `rwx------` | a private folder |
| `600` | `rw-------` | secrets, SSH keys |

```bash
chmod 755 deploy.sh
chmod 600 ~/.ssh/id_ed25519
```

> [!WARNING]
> Don't use `chmod 777` "to make it work": it lets any user on the system change or delete the file.

## Running your own script

```bash
nano hello.sh                # create a script
chmod +x hello.sh            # allow running it
./hello.sh                   # run it from the current folder
```

The `./` is required: the shell looks for commands only in the folders listed in `PATH`, and the current folder is not one of them.

## Owner and group

```bash
chown student report.txt             # change the owner
chown student:students report.txt    # owner and group
chown -R www-data /var/www/site      # recursively
chgrp students shared/               # group only
```

Only the administrator can change the owner, so these commands usually run with `sudo`.

## sudo — administrator rights

```bash
sudo apt update              # run one command as root
sudo -i                      # open an administrator shell (exit to leave)
whoami                       # which user you are
id                           # your UID and groups
groups                       # which groups you belong to
```

`sudo` asks for **your** password, not root's. Nothing is shown while you type the password — that is normal.

## Users and groups

```bash
sudo adduser ivan                    # create a user
sudo usermod -aG students ivan       # add to the students group
passwd                               # change your password
su - ivan                            # switch to another user
```
