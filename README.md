# BookShelf

A personal library app for Android. Users keep a list of their books, track what they want to read, are reading and have finished, rate books, and see reading statistics.

Built with **Kotlin** and **Jetpack Compose** (Material 3), using the **MVVM** architecture.

## Group members

| Name | Responsibility |
|---|---|
| Dinislam Kadyr | Data layer (Book model, BookRepository), ViewModels, navigation routes, unit tests |
| Adilzhan Baktybayev | UI layer: theme, reusable components, all screens, connecting screens to ViewModels and navigation |

## Main features

- **Book list** with search by title or author
- **Filter** by reading status (Want to read / Reading / Finished)
- **Sort** by title, rating or recently added
- **Book details**: change reading status, rate with stars, delete with confirmation
- **Add and edit** books with form validation
- **Statistics**: total books, books per status, pages read, average rating
- Light and dark theme

## Architecture (MVVM)

```
data/
  model/        Book, BookStatus, SortOption
  repository/   BookRepository (in-memory, StateFlow)
ui/
  theme/        colors, typography, shapes
  components/   reusable composables (BookCard, BookCover, RatingBar, SearchField, ...)
  screens/      home, detail, add (add/edit), stats: Screen + ViewModel for each
navigation/     Screen (routes), AppNavHost (NavHost)
```

- **Model**: `BookRepository` keeps the books in a `MutableStateFlow` and exposes a read-only `StateFlow`.
- **ViewModel**: each screen has a ViewModel that turns repository data into UI state (`StateFlow`) and handles user actions.
- **View**: screens are stateless composables. They receive state as parameters and send events back through callbacks. `AppNavHost` connects each screen to its ViewModel.

## Navigation

Navigation Compose with five destinations: `home`, `detail/{bookId}`, `add`, `edit/{bookId}`, `stats`.
The `bookId` argument (`Int`) is passed to the detail and edit screens; their ViewModels read it from `SavedStateHandle`.
Back navigation works with the top bar arrow and the system back button.

## How to run

1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an emulator or a device.

Unit tests: run the tests in `app/src/test` (or `./gradlew test`).
