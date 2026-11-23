package kz.mitu.bookshelf.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.mitu.bookshelf.data.model.SortOption

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface SettingsRepository {
    val darkTheme: Flow<Boolean?>

    val sortOption: Flow<SortOption>

    suspend fun setDarkTheme(darkTheme: Boolean?)

    suspend fun setSortOption(sortOption: SortOption)

    suspend fun resetSettings()
}

class DataStoreSettingsRepository(context: Context) : SettingsRepository {
    private val dataStore = context.applicationContext.settingsDataStore

    override val darkTheme: Flow<Boolean?> =
        dataStore.data.map { prefs -> prefs[DARK_THEME] }

    override val sortOption: Flow<SortOption> =
        dataStore.data.map { prefs ->
            SortOption.entries.firstOrNull { it.name == prefs[SORT_OPTION] }
                ?: SortOption.RECENTLY_ADDED
        }

    override suspend fun setDarkTheme(darkTheme: Boolean?) {
        dataStore.edit { prefs ->
            if (darkTheme == null) prefs.remove(DARK_THEME) else prefs[DARK_THEME] = darkTheme
        }
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        dataStore.edit { prefs -> prefs[SORT_OPTION] = sortOption.name }
    }

    override suspend fun resetSettings() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    private companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val SORT_OPTION = stringPreferencesKey("sort_option")
    }
}
