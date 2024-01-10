package meshki.studio.negarname.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.local.converters.DateConverter
import meshki.studio.negarname.data.local.converters.NoteConverter
import meshki.studio.negarname.data.local.converters.TodoConverter
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.data.local.dao.TodosDao
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.Todo

@Database(
    entities = [
        Note::class,
        Todo::class
    ],
    version = 10,
    exportSchema = false
)

@TypeConverters(
    value = [
        DateConverter::class,
        NoteConverter::class,
        TodoConverter::class
    ]
)

abstract class Database : RoomDatabase() {
    abstract fun getNotesDao(): NotesDao
    abstract fun getTodosDao(): TodosDao

    fun clearDatabase() {
        clearAllTables()
    }
}

