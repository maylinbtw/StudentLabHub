package com.slh.studentlabhub.data.reference

import com.slh.studentlabhub.domain.model.RefTech

object ReferenceSeed {

    const val VERSION = 1

    data class SeedCategory(
        val id: String,
        val tech: RefTech,
        val title: String,
        val entries: List<SeedEntry>,
    )

    data class SeedEntry(
        val title: String,
        val description: String,
        val example: String,
    )

    private fun e(title: String, description: String, example: String) =
        SeedEntry(title = title, description = description, example = example)

    val categories: List<SeedCategory> = listOf(
        SeedCategory(
            id = "linux_files",
            tech = RefTech.LINUX,
            title = "Файлы и директории",
            entries = listOf(
                e("ls", "Содержимое директории", "ls -la /home/user"),
                e("cd", "Смена директории", "cd /var/log"),
                e("pwd", "Текущая директория", "pwd"),
                e("mkdir", "Создать директорию", "mkdir -p ~/project/src"),
                e("rm", "Удалить файл/директорию", "rm -rf ./temp"),
                e("cp", "Копировать файл", "cp file.txt ~/backup/"),
                e("mv", "Переместить/переименовать", "mv old.txt new.txt"),
                e("touch", "Создать пустой файл", "touch notes.txt"),
                e("cat", "Вывести содержимое файла", "cat /etc/hostname"),
                e("less", "Постраничный просмотр файла", "less /var/log/syslog"),
                e("find", "Поиск файлов", "find . -name \"*.kt\""),
                e("ln", "Создать ссылку на файл", "ln -s /opt/app/bin app"),
            ),
        ),
        SeedCategory(
            id = "linux_text",
            tech = RefTech.LINUX,
            title = "Текст и поиск",
            entries = listOf(
                e("grep", "Поиск строки в файлах", "grep -rn \"TODO\" ./src"),
                e("head", "Первые строки файла", "head -n 20 log.txt"),
                e("tail", "Последние строки, слежение", "tail -f /var/log/syslog"),
                e("wc", "Подсчёт строк, слов, байт", "wc -l main.cpp"),
                e("sort", "Сортировка строк", "sort -n numbers.txt"),
                e("uniq", "Удалить повторы подряд", "sort names.txt | uniq -c"),
                e("cut", "Вырезать столбцы", "cut -d ':' -f 1 /etc/passwd"),
                e("sed", "Замена текста в потоке", "sed 's/old/new/g' file.txt"),
                e("awk", "Обработка столбцов", "awk '{print \$1}' access.log"),
                e("diff", "Сравнить два файла", "diff -u a.txt b.txt"),
            ),
        ),
        SeedCategory(
            id = "linux_processes",
            tech = RefTech.LINUX,
            title = "Процессы",
            entries = listOf(
                e("ps", "Список процессов", "ps aux | grep java"),
                e("top", "Монитор процессов", "top"),
                e("htop", "Интерактивный монитор", "htop"),
                e("kill", "Завершить процесс по PID", "kill -9 1234"),
                e("killall", "Завершить процессы по имени", "killall firefox"),
                e("jobs", "Фоновые задачи терминала", "jobs -l"),
                e("bg · fg", "Перевести задачу в фон / на передний план", "fg %1"),
                e("nohup", "Запуск, не зависящий от терминала", "nohup ./server &"),
            ),
        ),
        SeedCategory(
            id = "linux_network",
            tech = RefTech.LINUX,
            title = "Сеть",
            entries = listOf(
                e("ping", "Проверка доступности узла", "ping -c 4 google.com"),
                e("curl", "HTTP-запрос", "curl -I https://example.com"),
                e("wget", "Скачать файл", "wget https://example.com/file.zip"),
                e("ssh", "Удалённый вход", "ssh user@192.168.1.10"),
                e("scp", "Копирование по SSH", "scp file.txt user@host:~/"),
                e("ip", "Адреса и интерфейсы", "ip addr show"),
                e("ifconfig", "Настройки интерфейсов (устар.)", "ifconfig eth0"),
                e("ss", "Открытые порты и соединения", "ss -tulpn"),
                e("nslookup", "DNS-запрос", "nslookup example.com"),
                e("traceroute", "Маршрут до узла", "traceroute google.com"),
            ),
        ),
        SeedCategory(
            id = "linux_permissions",
            tech = RefTech.LINUX,
            title = "Права доступа",
            entries = listOf(
                e("chmod", "Изменение прав доступа", "chmod 755 script.sh"),
                e("chown", "Смена владельца файла", "chown user:group file.txt"),
                e("chgrp", "Смена группы файла", "chgrp developers project/"),
                e("umask", "Маска прав по умолчанию", "umask 022"),
                e("sudo", "Выполнить от имени root", "sudo apt update"),
                e("su", "Сменить пользователя", "su - admin"),
            ),
        ),
        SeedCategory(
            id = "linux_system",
            tech = RefTech.LINUX,
            title = "Система и пакеты",
            entries = listOf(
                e("df", "Свободное место на дисках", "df -h"),
                e("du", "Размер файлов и папок", "du -sh ~/Downloads"),
                e("free", "Использование памяти", "free -h"),
                e("uname", "Информация о системе", "uname -a"),
                e("whoami", "Текущий пользователь", "whoami"),
                e("history", "История команд", "history | tail -20"),
                e("man", "Справка по команде", "man chmod"),
                e("apt", "Установка пакетов (Debian/Ubuntu)", "sudo apt install git"),
                e("tar", "Архивация и распаковка", "tar -czvf backup.tar.gz ./project"),
            ),
        ),

        SeedCategory(
            id = "cpp_basics",
            tech = RefTech.CPP,
            title = "Основы",
            entries = listOf(
                e("int · double · char · bool", "Базовые типы C++", "int n = 5; double x = 3.14; bool ok = true;"),
                e("auto", "Вывод типа компилятором", "auto total = price * count;"),
                e("cin · cout", "Ввод и вывод", "std::cin >> n; std::cout << n << std::endl;"),
                e("if · switch", "Условия", "if (n > 0) { ... } else { ... }"),
                e("for · while", "Циклы", "for (int i = 0; i < n; ++i) { ... }"),
                e("& · *", "Ссылки и указатели", "int& ref = n; int* ptr = &n; *ptr = 10;"),
            ),
        ),
        SeedCategory(
            id = "cpp_stl",
            tech = RefTech.CPP,
            title = "Строки и STL",
            entries = listOf(
                e("std::string", "Строки", "std::string s = \"hi\"; s += \"!\"; s.size();"),
                e("std::vector", "Динамический массив", "std::vector<int> v = {1, 2}; v.push_back(3);"),
                e("std::map", "Словарь ключ:значение", "std::map<std::string, int> m; m[\"a\"] = 1;"),
                e("range-for", "Перебор контейнера", "for (const auto& x : v) { std::cout << x; }"),
                e("std::sort", "Сортировка", "std::sort(v.begin(), v.end());"),
            ),
        ),
        SeedCategory(
            id = "cpp_oop",
            tech = RefTech.CPP,
            title = "Функции и ООП",
            entries = listOf(
                e("функция", "Объявление функции", "int sum(int a, int b) { return a + b; }"),
                e("class", "Класс с конструктором", "class Dog { public: Dog(std::string n) : name(n) {} private: std::string name; };"),
                e("virtual · override", "Наследование и полиморфизм", "class Cat : public Animal { void speak() override; };"),
                e("try · catch", "Обработка исключений", "try { throw std::runtime_error(\"x\"); } catch (const std::exception& e) { }"),
            ),
        ),

        SeedCategory(
            id = "java_basics",
            tech = RefTech.JAVA,
            title = "Основы",
            entries = listOf(
                e("int · double · boolean · char", "Примитивные типы", "int n = 5; double x = 3.14; boolean ok = true;"),
                e("String", "Строки", "String s = \"hi\"; s.length(); s.equals(\"hi\");"),
                e("массивы", "Массив фиксированной длины", "int[] a = {1, 2, 3}; a.length;"),
                e("Scanner", "Ввод с клавиатуры", "Scanner sc = new Scanner(System.in); int n = sc.nextInt();"),
                e("for · while", "Циклы", "for (int i = 0; i < n; i++) { ... }"),
            ),
        ),
        SeedCategory(
            id = "java_collections",
            tech = RefTech.JAVA,
            title = "Коллекции",
            entries = listOf(
                e("ArrayList", "Динамический список", "List<String> list = new ArrayList<>(); list.add(\"a\");"),
                e("HashMap", "Словарь ключ:значение", "Map<String, Integer> m = new HashMap<>(); m.put(\"a\", 1);"),
                e("HashSet", "Множество без повторов", "Set<Integer> set = new HashSet<>(); set.add(5);"),
                e("for-each", "Перебор коллекции", "for (String s : list) { System.out.println(s); }"),
                e("stream", "Обработка коллекций", "list.stream().filter(s -> s.length() > 2).toList();"),
            ),
        ),
        SeedCategory(
            id = "java_oop",
            tech = RefTech.JAVA,
            title = "Классы и исключения",
            entries = listOf(
                e("class", "Класс с конструктором", "class Dog { String name; Dog(String name) { this.name = name; } }"),
                e("extends", "Наследование", "class Cat extends Animal { @Override void speak() { } }"),
                e("interface", "Интерфейс", "interface Shape { double area(); }"),
                e("lambda", "Анонимная функция", "Runnable r = () -> System.out.println(\"run\");"),
                e("try · catch · finally", "Обработка исключений", "try { ... } catch (IOException e) { ... } finally { ... }"),
            ),
        ),

        SeedCategory(
            id = "python_basics",
            tech = RefTech.PYTHON,
            title = "Основы",
            entries = listOf(
                e("int · float · str · bool", "Базовые типы Python", "x: int = 5 · s: str = \"hi\""),
                e("input · print", "Ввод и вывод", "name = input(); print(f\"Привет, {name}\")"),
                e("f-строки", "Форматирование строк", "f\"{price:.2f} руб.\""),
                e("if · elif · else", "Условия", "if x > 0: ... elif x == 0: ... else: ..."),
                e("for · range", "Цикл по диапазону", "for i in range(10): print(i)"),
                e("enumerate", "Индекс и значение", "for i, v in enumerate(items): ..."),
            ),
        ),
        SeedCategory(
            id = "python_collections",
            tech = RefTech.PYTHON,
            title = "Коллекции",
            entries = listOf(
                e("list · tuple · set", "Коллекции", "a=[1,2,3]; a.append(4)"),
                e("dict", "Словарь ключ:значение", "d={'k':'v'}; d.get('k')"),
                e("срезы", "Части последовательности", "a[1:3]; s[::-1]"),
                e("list comprehension", "Генератор списка", "[x * x for x in range(5) if x % 2 == 0]"),
            ),
        ),
        SeedCategory(
            id = "python_functions",
            tech = RefTech.PYTHON,
            title = "Функции, ООП, файлы",
            entries = listOf(
                e("def · lambda", "Функции", "def f(x): return x*2"),
                e("class", "ООП · класс", "class Dog: def __init__(self,n)"),
                e("try · except", "Обработка ошибок", "try: x=1/0 except: pass"),
                e("with open", "Чтение файла", "with open('data.txt', encoding='utf-8') as f: text = f.read()"),
                e("import", "Подключение модулей", "import math; from random import randint"),
            ),
        ),
    )
}
