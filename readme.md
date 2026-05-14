# storage-place

## Используемые технологии

* Java 25
* JavaFX
* Maven
* PostgreSQL - 18.3
* JDBC
* Flyway

## Подготовка базы данных

Перед запуском необходимо создать базу данных PostgreSQL.

Подключиться к PostgreSQL через `psql`:

```bash
psql -U postgres -p 5432
```

Создать базу данных:

```sql
CREATE DATABASE storage_place;
```

Проверить, что база создана:

```sql
\l
```

Выйти из `psql`:

```sql
\q
```

## Файл настроек подключения

В проекте нужно создать файл:

```text
src/main/resources/ru/storageplace/application.properties
```

Пример содержимого файла:

```properties
db.url=jdbc:postgresql://localhost:5432/storage_place
db.usern=postgres
db.password=your_password
```

Где:

* `db.url` — строка подключения к базе PostgreSQL;
* `db.user` — имя пользователя PostgreSQL;
* `db.password` — пароль пользователя PostgreSQL.

## Запуск проекта

Запустить через Maven:

```bash
mvn javafx:run
```

Запустить только миграции:

```bash
mvn javafx:run --migrate-only
```

Запустить без миграций:

```bash
mvn javafx:run --skip-migrations
```

Запуски тестов:

```bash
mvn javafx:run --dev-test-income
mvn javafx:run --dev-test-outcome
mvn javafx:run --dev-test-transfer
```

## Основные разделы программы

В интерфейсе доступны вкладки:

* «Товары» — справочник товаров;
* «Склады» — справочник складов;
* «Типы мест хранения» — справочник типов мест;
* «Места хранения» — справочник мест хранения;
* «Состояние мест» — просмотр занятости, свободного объёма, доступного веса и статуса мест хранения;
* «Остатки» — просмотр остатков товаров по местам хранения;
* «Операции» — расчёт места хранения.