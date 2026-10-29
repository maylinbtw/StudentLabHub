# Installing software: apt

On Linux, software is installed from **repositories** with a **package manager**. On Debian and Ubuntu it is `apt`. It downloads the package and everything it depends on.

## Basic commands

```bash
sudo apt update                 # refresh the list of available packages
sudo apt upgrade                # upgrade installed packages
sudo apt install git            # install
sudo apt install gcc g++ make   # several at once
sudo apt remove git             # remove the program
sudo apt purge git              # remove together with its settings
sudo apt autoremove             # remove unneeded dependencies
```

> [!NOTE]
> Always run `sudo apt update` before installing, otherwise `apt` may not find a package or may fetch an old version.

## Search and info

```bash
apt search python3              # find packages by a word
apt show python3                # description, version, size
apt list --installed            # what is installed
apt list --installed | grep gcc # check a specific package
which python3                   # where the program is
python3 --version               # version
```

## Packages for studying

| Task | Command |
|---|---|
| C and C++ compilers | `sudo apt install build-essential` |
| Python and pip | `sudo apt install python3 python3-pip python3-venv` |
| Java | `sudo apt install openjdk-21-jdk` |
| .NET | `sudo apt install dotnet-sdk-8.0` |
| Git | `sudo apt install git` |
| editors | `sudo apt install nano vim` |
| networking | `sudo apt install curl wget net-tools` |
| folder tree, monitor | `sudo apt install tree htop` |

## Installing a .deb file

If a program was downloaded as a `.deb` file:

```bash
sudo apt install ./program.deb
```

The `./` tells `apt` it's a local file, not a package name.

## Other distributions

| Distribution | Manager | Install |
|---|---|---|
| Debian, Ubuntu, Mint | `apt` | `sudo apt install git` |
| Fedora, RHEL | `dnf` | `sudo dnf install git` |
| Arch, Manjaro | `pacman` | `sudo pacman -S git` |
| openSUSE | `zypper` | `sudo zypper install git` |

## snap and flatpak

Universal packages that work across distributions:

```bash
sudo snap install code --classic     # VS Code via snap
flatpak install flathub org.telegram.desktop
```

> [!WARNING]
> The error `Could not get lock /var/lib/dpkg/lock` means `apt` is already running in another window or an automatic update is in progress. Wait for it to finish — don't delete lock files by hand.
