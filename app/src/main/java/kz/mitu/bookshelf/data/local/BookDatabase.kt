package kz.mitu.bookshelf.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        private const val DATABASE_NAME = "bookshelf.db"

        @Volatile
        private var instance: BookDatabase? = null

        fun getInstance(context: Context): BookDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
    }
}
