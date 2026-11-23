# BookShelf

A personal library app for Android. Users keep a list of their books, track what they want to read, are reading and have finished, rate books, and see reading statistics.

Built with **Kotlin** and **Jetpack Compose**, using the **MVVM** architecture.

## Group members

| Name | Responsibility |
|---|---|
| Dinislam Kadyr | Data layer (Book model, BookRepository), ViewModels, navigation routes, unit tests. Local storage with Room and DataStore, Settings screen |
| Adilzhan Baktybayev | UI layer: theme, reusable components, all screens, connecting screens to ViewModels and navigation. Online book search with Retrofit (Open Library API) |

## Main features

- **Book list** with search by title or author
- **Filter** by reading status (Want to read / Reading / Finished)
- **Sort** by title, rating or recently added
- **Book details**: change reading status, rate with stars, delete with confirmation
- **Add and edit** books with form validation
- **Statistics**: total books, books per status, pages read, average rating
- Light and dark theme, saved in **Settings**
- Books and settings are **saved on the device** and stay after the app is closed
- **Find books online** (Open Library API) and add them to your library

## Architecture (MVVM)

```
data/
  model/        Book, BookStatus, SortOption
  local/        BookEntity, BookDao, BookDatabase (Room)
  repository/   BookRepository (interface), RoomBookRepository
  settings/     SettingsRepository (DataStore)
ui/
  theme/        colors, typography, shapes
  components/   reusable composables (BookCard, BookCover, RatingBar, SearchField, ...)
  screens/      home, detail, add (add/edit), stats, settings, online: Screen + ViewModel for each
navigation/     Screen (routes), AppNavHost (NavHost)
```

- **Model**: `BookRepository` reads and writes books in the Room database and exposes them as a `Flow`.
- **ViewModel**: each screen has a ViewModel that turns repository data into UI state (`StateFlow`) and handles user actions.
- **View**: screens are stateless composables. They receive state as parameters and send events back through callbacks. `AppNavHost` connects each screen to its ViewModel.

## Local storage (Room and DataStore)

- **Room**: `BookEntity` is the `books` table, `BookDao` has the CRUD queries, `BookDatabase` creates the database.
- **CRUD**: add a book (Create), list and details (Read), edit and change status or rating (Update), delete (Delete).
- `RoomBookRepository` implements `BookRepository`; ViewModels observe `Flow` from Room, so the UI updates automatically.
- **DataStore** saves the theme (light / dark / system) and the sort option. Both stay after restarting the app.

## Online search (REST API)

The app searches books in the [Open Library](https://openlibrary.org/developers/api) API and shows real data with covers.

```
data/remote/       OpenLibraryApi (Retrofit interface), RetrofitClient, dto/ (JSON → Kotlin data classes)
data/repository/   OnlineBookRepository: calls the API and maps DTOs to OnlineBook
ui/screens/online/ OnlineSearchViewModel (StateFlow) and OnlineSearchScreen
```

- **Retrofit + Gson** send `GET https://openlibrary.org/search.json?q=...` and convert the JSON into data classes.
- **Coroutines**: `searchBooks` is a `suspend` function, called from `viewModelScope`.
- **UI states**: `Loading` (progress indicator), `Success` (list of books), `Empty` (no results), `Error` (message + **Retry** button).
- **Coil** loads the cover images from `covers.openlibrary.org`.
- **Add to library** saves an online book to the local Room database.

## Navigation

Navigation Compose with destinations: `home`, `detail/{bookId}`, `add`, `edit/{bookId}`, `stats`, `settings`, `online_search`.
The `bookId` argument (`Int`) is passed to the detail and edit screens; their ViewModels read it from `SavedStateHandle`.
Back navigation works with the top bar arrow and the system back button.

## How to run

1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an emulator or a device.

Unit tests: run the tests in `app/src/test` (or `./gradlew test`).
