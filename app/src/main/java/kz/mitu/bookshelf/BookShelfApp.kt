package kz.mitu.bookshelf

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kz.mitu.bookshelf.data.local.BookDatabase
import kz.mitu.bookshelf.data.local.sampleBooks
import kz.mitu.bookshelf.data.local.toEntity
import kz.mitu.bookshelf.data.repository.BookRepository
import kz.mitu.bookshelf.data.repository.RoomBookRepository

class BookShelfApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: BookDatabase by lazy { BookDatabase.getInstance(this) }

    val bookRepository: BookRepository by lazy { RoomBookRepository(database.bookDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        seedDatabaseIfEmpty()
    }

    private fun seedDatabaseIfEmpty() {
        applicationScope.launch {
            val dao = database.bookDao()
            if (dao.count() == 0) {
                dao.insertAll(sampleBooks.map { it.copy(id = 0).toEntity() })
            }
        }
    }

    companion object {
        lateinit var instance: BookShelfApp
            private set
    }
}
