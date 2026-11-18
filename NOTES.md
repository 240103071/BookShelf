# BookShelf — project context
Android app, Kotlin + Jetpack Compose + Material 3. Package: kz.mitu.bookshelf
Architecture: MVVM. Repository (in-memory, StateFlow) -> ViewModel (StateFlow UI state) -> Composable screens.
Navigation: Navigation Compose with string routes, defined in navigation/Screen.kt.
No Hilt/DI for now: BookRepository is a Kotlin object (singleton).
ViewModels get route arguments via SavedStateHandle.

Rules:
- Keep changes limited to what the current task asks. Do not refactor unrelated files.
- Use reusable composables from ui/components instead of duplicating UI.
- All user-visible strings go to res/values/strings.xml.
- Use the version catalog (gradle/libs.versions.toml) for dependencies.

Workflow for every task:
1. Implement the task, touching only the files it needs.
2. Run ./gradlew assembleDebug and fix all compile errors until the build succeeds.
3. Show me a short summary of what changed and wait for my "ok".
4. After I say ok: git pull --rebase, git add the changed files, commit with a short
   clear message describing the change, then git push.
   Never use --force. Never change git config or author.