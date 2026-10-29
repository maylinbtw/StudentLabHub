# Сеть и удалённый доступ

## Свой адрес и интерфейсы

```bash
ip a                  # сетевые интерфейсы и IP-адреса
ip r                  # маршруты, шлюз по умолчанию
hostname -I           # только IP-адреса
```

В выводе `ip a` ищите строку `inet 192.168.1.15/24` у интерфейса `eth0`, `enp0s3` или `wlan0`. Интерфейс `lo` с `127.0.0.1` — сам компьютер.

## Проверка связи

```bash
ping -c 4 google.com        # 4 пакета и остановиться
traceroute google.com       # через какие узлы идут пакеты
nslookup google.com         # IP-адрес по имени (DNS)
```

Без `-c` команда `ping` работает бесконечно — остановите её `Ctrl+C`.

## Порты

```bash
ss -tuln              # какие порты слушает система
ss -tulnp | grep 8080 # кто занял порт 8080 (с sudo видно процесс)
```

| Флаг | Значение |
|---|---|
| `-t` / `-u` | TCP / UDP |
| `-l` | только слушающие |
| `-n` | номера вместо имён |
| `-p` | показать процесс |

## Загрузка: curl и wget

```bash
wget https://example.com/file.zip           # скачать файл
curl -O https://example.com/file.zip        # то же через curl
curl https://api.github.com/users/octocat   # вывести ответ в терминал
curl -I https://example.com                 # только заголовки ответа
```

Запрос к своему API:

```bash
curl -X POST http://localhost:8080/api/labs \
     -H "Content-Type: application/json" \
     -d '{"title": "Массивы", "number": 3}'
```

`\` в конце строки переносит команду на следующую строку.

## SSH — подключение к удалённому компьютеру

```bash
ssh student@192.168.1.20          # подключиться
ssh -p 2222 student@server.local  # другой порт
exit                              # отключиться
```

При первом подключении SSH спросит, доверять ли ключу сервера — ответьте `yes`.

## Ключи SSH

Вход по ключу безопаснее и удобнее пароля:

```bash
ssh-keygen -t ed25519 -C "student@college"    # создать пару ключей
ssh-copy-id student@192.168.1.20              # отправить открытый ключ на сервер
ssh student@192.168.1.20                      # теперь без пароля
```

Создаются два файла: `~/.ssh/id_ed25519` — **закрытый** ключ, `~/.ssh/id_ed25519.pub` — **открытый**.

> [!WARNING]
> Закрытый ключ никому не передавайте и не загружайте в репозитории. На сервер и в GitHub отправляют только `.pub`.

## Копирование файлов: scp и rsync

```bash
scp report.pdf student@192.168.1.20:~/labs/       # на сервер
scp student@192.168.1.20:~/labs/result.txt .      # с сервера сюда
scp -r project/ student@192.168.1.20:~/           # папку целиком

rsync -avz project/ student@192.168.1.20:~/project/   # синхронизировать, передаёт только изменения
```

## Файл ~/.ssh/config

Чтобы не вводить адрес и порт каждый раз:

```text
Host lab
    HostName 192.168.1.20
    User student
    Port 22
```

Теперь достаточно `ssh lab` и `scp file.txt lab:~/`.
