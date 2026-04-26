Vse good?
Мобильное Android-приложение «Дневник чувств». Позволяет отслеживать эмоциональное состояние, категоризировать записи по контексту события, просматривать аналитику настроения в виде графиков и экспортировать историю в CSV/PDF.

Функциональность
- Регистрация и авторизация — регистрация (email, имя, пароль) и вход с JWT-токеном, восстановление пароля по email
- Управление записями настроения — создание, редактирование и удаление записей с выбором эмоции, даты, текстовой заметки и категории
- Категории (контекст события) — Семья, Работа, Друзья, Здоровье, Хобби, Финансы, Отдых, Учёба
- Эмоции — Радость, Грусть, Злость, Спокойствие, Тревога, Усталость, Вдохновение, Любовь, Страх и другие
- Хронология — список записей в хронологическом порядке с пагинацией (до 10 000 записей без зависаний UI)
- Фильтрация по периоду — сегодня, неделя, месяц, всё время; фильтры по категории и эмоции
- Аналитика и статистика — частота эмоций, доминантная эмоция по категориям, cross-tab «категория × эмоция»
- Графики — визуализация статистики с помощью MPAndroidChart (столбчатые и круговые диаграммы)
- Экспорт — выгрузка истории записей в CSV и PDF
- Уведомления — локальные push-напоминания сделать запись настроения
- Управление аккаунтом — просмотр профиля, выход из аккаунта

Стек технологий

Клиентская часть:
- Язык: Kotlin
- UI: Jetpack Compose + Material Design 3
- Архитектура: MVVM
- Состояние: StateFlow + RxJava 3 / RxAndroid
- Навигация: Compose Navigation
- Сеть: Retrofit 2 + OkHttp + Gson
- Графики: MPAndroidChart

Серверная часть:
- Язык: C# / .NET Core
- Фреймворк: ASP.NET Core (REST API)
- БД: PostgreSQL, ORM: Entity Framework Core
- Безопасность: JWT (HS256), PBKDF2-SHA256 (пароли), AES-256-CBC (PII-данные)
- Инфраструктура: Docker

Структура проекта

app/src/main/java/com/example/feel_journal/
├── data/
│   ├── model/            # Доменные data-классы (User, Emotion, Category, EmotionEntry, Notification)
│   └── remote/
│       ├── api/          # Retrofit API-интерфейсы (6 шт)
│       ├── dto/          # Request/Response DTO
│       ├── interceptor/  # AuthInterceptor (JWT-заголовок)
│       └── ApiClient     # Retrofit singleton
├── ui/
│   ├── state/            # UiState sealed classes
│   └── viewmodel/       # ViewModels (6 шт)

server/
├── Controllers/          # ASP.NET Core контроллеры (6 шт)
├── Models/               # EF Core entities (5 шт)
├── Data/                 # AppDbContext (Fluent API конфигурация)
├── Services/             # AuthService
├── Security/             # JwtService, PasswordHasher, AesEncryptor
├── DTOs/                 # Request/Response модели
├── database/             # SQL-скрипты создания таблиц и сид-данных
└── docs/                 # Документация (OpenAPI, DocFX)


Требования
- Android 7.0 (API 24) и выше
- Интернет-соединение для синхронизации с сервером
- ~30 МБ свободного места

Сборка

./gradlew assembleDebug

APK будет находиться в app/build/outputs/apk/debug/.

Настройка сервера
По умолчанию приложение подключается к http://10.0.2.2:5000/ (localhost эмулятора). URL сервера можно изменить в ApiClient.kt (поле `BASE_URL`).

API-эндпоинты

| Метод | Путь | Описание |
|-------|------|----------|
| POST | /api/auth/register | Регистрация |
| POST | /api/auth/login | Авторизация |
| POST | /api/auth/recovery | Запрос восстановления пароля |
| POST | /api/auth/reset-password | Сброс пароля |
| GET | /api/auth/me | Текущий пользователь |
| GET | /api/entries | Список записей (пагинация + фильтры) |
| GET | /api/entries/{id} | Запись по ID |
| POST | /api/entries | Создать запись |
| PUT | /api/entries/{id} | Обновить запись |
| DELETE | /api/entries/{id} | Удалить запись |
| GET | /api/categories | Все категории |
| POST | /api/categories | Создать категорию |
| PUT | /api/categories/{id} | Обновить категорию |
| DELETE | /api/categories/{id} | Удалить категорию |
| GET | /api/emotions | Все эмоции |
| GET | /api/emotions/{id} | Эмоция по ID |
| GET | /api/notifications | Уведомления пользователя |
| POST | /api/notifications | Создать уведомление |
| PUT | /api/notifications/{id} | Обновить уведомление |
| PATCH | /api/notifications/{id}/read | Пометить прочитанным |
| DELETE | /api/notifications/{id} | Удалить уведомление |
| GET | /api/analytics/mood | Анализ настроения |
| GET | /api/analytics/export | Экспорт CSV/PDF |

Все запросы (кроме регистрации, авторизации и восстановления пароля) требуют заголовок Authorization: Bearer <token>. Формат данных — JSON.
