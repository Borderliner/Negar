package meshki.studio.negarname.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.local.dao.AlarmsDao
import meshki.studio.negarname.data.local.dao.DatabaseDao
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.data.local.dao.TodosDao
import meshki.studio.negarname.data.repository.DatabaseRepository
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.data.repository.TodosRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val DATABASE_NAME = "app_database"
val databaseModule = module {
    single { provideRoomDatabase(androidContext()) }

    single { provideDatabaseDao(get()) }
    single { provideNotesDao(get()) }
    single { provideTodosDao(get()) }
    single { provideAlarmsDao(get()) }

    factory { DatabaseRepository(get(), get()) }
    factory { NotesRepository(get(), get(), get()) }
    factory { TodosRepository(get()) }

}

fun provideRoomDatabase(context: Context): Database {
    return Room.databaseBuilder(context, Database::class.java, DATABASE_NAME)
        .addCallback(object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                Database.onCreate(scope = scope, database = database)
//            }
        })
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .fallbackToDestructiveMigration()
        .build()
}

private fun provideDatabaseDao(db: Database): DatabaseDao {
    return db.getDatabaseDao()
}

private fun provideNotesDao(db: Database): NotesDao {
    return db.getNotesDao()
}

private fun provideTodosDao(db: Database): TodosDao {
    return db.getTodosDao()
}

private fun provideAlarmsDao(db: Database): AlarmsDao {
    return db.getAlarmsDao()
}

