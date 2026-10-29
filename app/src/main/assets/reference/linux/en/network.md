# Networking and remote access

## Your address and interfaces

```bash
ip a                  # network interfaces and IP addresses
ip r                  # routes, the default gateway
hostname -I           # IP addresses only
```

In the `ip a` output look for a line like `inet 192.168.1.15/24` on an interface such as `eth0`, `enp0s3` or `wlan0`. The `lo` interface with `127.0.0.1` is the computer itself.

## Checking connectivity

```bash
ping -c 4 google.com        # 4 packets, then stop
traceroute google.com       # which hops packets go through
nslookup google.com         # IP address by name (DNS)
```

Without `-c`, `ping` runs forever — stop it with `Ctrl+C`.

## Ports

```bash
ss -tuln              # which ports the system listens on
ss -tulnp | grep 8080 # who uses port 8080 (with sudo you see the process)
```

| Flag | Meaning |
|---|---|
| `-t` / `-u` | TCP / UDP |
| `-l` | listening only |
| `-n` | numbers instead of names |
| `-p` | show the process |

## Downloading: curl and wget

```bash
wget https://example.com/file.zip           # download a file
curl -O https://example.com/file.zip        # the same with curl
curl https://api.github.com/users/octocat   # print the response
curl -I https://example.com                 # response headers only
```

A request to your own API:

```bash
curl -X POST http://localhost:8080/api/labs \
     -H "Content-Type: application/json" \
     -d '{"title": "Arrays", "number": 3}'
```

A `\` at the end of a line continues the command on the next line.

## SSH — connecting to a remote computer

```bash
ssh student@192.168.1.20          # connect
ssh -p 2222 student@server.local  # another port
exit                              # disconnect
```

On the first connection SSH asks whether to trust the server's key — answer `yes`.

## SSH keys

Key login is safer and more convenient than a password:

```bash
ssh-keygen -t ed25519 -C "student@college"    # create a key pair
ssh-copy-id student@192.168.1.20              # send the public key to the server
ssh student@192.168.1.20                      # now without a password
```

Two files are created: `~/.ssh/id_ed25519` — the **private** key, `~/.ssh/id_ed25519.pub` — the **public** one.

> [!WARNING]
> Never share the private key or upload it to repositories. Only the `.pub` file goes to servers and GitHub.

## Copying files: scp and rsync

```bash
scp report.pdf student@192.168.1.20:~/labs/       # to the server
scp student@192.168.1.20:~/labs/result.txt .      # from the server to here
scp -r project/ student@192.168.1.20:~/           # a whole folder

rsync -avz project/ student@192.168.1.20:~/project/   # sync, sends only changes
```

## The ~/.ssh/config file

To avoid typing the address and port every time:

```text
Host lab
    HostName 192.168.1.20
    User student
    Port 22
```

Now `ssh lab` and `scp file.txt lab:~/` are enough.

## Check yourself

> [!QUIZ]
> Which SSH key can you upload to servers and GitHub?
> ---
> Only the public one — the `.pub` file. Never share the private key.

> [!QUIZ]
> How do you copy a file to a remote machine?
> ---
> `scp file user@host:~/folder/`.
