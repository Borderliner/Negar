package meshki.studio.negarname.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.data.local.dao.TodosDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val DbName = "app_database"
val dbModule = module {
    single { provideRoomDatabase(androidContext(), get()) }
    single { provideNotesDao(get()) }
    single { provideTodosDao(get()) }
}

fun provideRoomDatabase(context: Context, scope: CoroutineScope): Database {
    val database: Database?
    database = Room.databaseBuilder(context, Database::class.java, DbName)
        .addCallback(object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                Database.onCreate(scope = scope, database = database)
//            }
        })
        .fallbackToDestructiveMigration()
        .build()
    return database
}

private fun provideNotesDao(db: Database): NotesDao {
    return db.getNotesDao()
}

private fun provideTodosDao(db: Database): TodosDao {
    return db.getTodosDao()
}