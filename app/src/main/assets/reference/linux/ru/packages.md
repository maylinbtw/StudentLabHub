# Установка программ: apt

В Linux программы ставят из **репозиториев** через **менеджер пакетов**. В Debian и Ubuntu это `apt`. Он сам скачивает пакет и всё, от чего тот зависит.

## Основные команды

```bash
sudo apt update                 # обновить список доступных пакетов
sudo apt upgrade                # обновить установленные пакеты
sudo apt install git            # установить
sudo apt install gcc g++ make   # несколько сразу
sudo apt remove git             # удалить программу
sudo apt purge git              # удалить вместе с настройками
sudo apt autoremove             # убрать ненужные зависимости
```

> [!NOTE]
> Перед установкой всегда делайте `sudo apt update`, иначе `apt` может не найти пакет или скачать старую версию.

## Поиск и информация

```bash
apt search python3              # найти пакеты по слову
apt show python3                # описание, версия, размер
apt list --installed            # что установлено
apt list --installed | grep gcc # проверить конкретный пакет
which python3                   # где находится программа
python3 --version               # версия
```

## Пакеты для учёбы

| Задача | Команда |
|---|---|
| компиляторы C и C++ | `sudo apt install build-essential` |
| Python и pip | `sudo apt install python3 python3-pip python3-venv` |
| Java | `sudo apt install openjdk-21-jdk` |
| .NET | `sudo apt install dotnet-sdk-8.0` |
| Git | `sudo apt install git` |
| редакторы | `sudo apt install nano vim` |
| сеть | `sudo apt install curl wget net-tools` |
| дерево папок, монитор | `sudo apt install tree htop` |

## Установка .deb-файла

Если программа скачана файлом `.deb`:

```bash
sudo apt install ./program.deb
```

`./` нужен, чтобы `apt` понял, что это локальный файл, а не имя пакета.

## Другие дистрибутивы

| Дистрибутив | Менеджер | Установка |
|---|---|---|
| Debian, Ubuntu, Mint | `apt` | `sudo apt install git` |
| Fedora, RHEL | `dnf` | `sudo dnf install git` |
| Arch, Manjaro | `pacman` | `sudo pacman -S git` |
| openSUSE | `zypper` | `sudo zypper install git` |

## snap и flatpak

Универсальные пакеты, которые работают на разных дистрибутивах:

```bash
sudo snap install code --classic     # VS Code через snap
flatpak install flathub org.telegram.desktop
```

> [!WARNING]
> Ошибка `Could not get lock /var/lib/dpkg/lock` значит, что `apt` уже работает в другом окне или идёт автообновление. Дождитесь окончания — не удаляйте lock-файлы вручную.
