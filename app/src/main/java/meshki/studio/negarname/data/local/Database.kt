package meshki.studio.negarname.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import meshki.studio.negarname.data.local.converters.DateConverter
import meshki.studio.negarname.data.local.converters.NoteConverter
import meshki.studio.negarname.data.local.converters.TodoConverter
import meshki.studio.negarname.data.local.dao.AlarmsDao
import meshki.studio.negarname.data.local.dao.AppDao
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.data.local.dao.TodosDao
import meshki.studio.negarname.entity.Alarm
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.NotesAlarmsCrossRef
import meshki.studio.negarname.entity.Todo

@Database(
    entities = [
        Note::class,
        Todo::class,
        Alarm::class,
        NotesAlarmsCrossRef::class
    ],
    version = 19,
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
    abstract fun getAppDao(): AppDao
    abstract fun getNotesDao(): NotesDao
    abstract fun getTodosDao(): TodosDao
    abstract fun getAlarmsDao(): AlarmsDao

    fun clearDatabase() {
        clearAllTables()
    }
}

