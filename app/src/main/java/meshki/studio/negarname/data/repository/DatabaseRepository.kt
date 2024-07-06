package meshki.studio.negarname.data.repository

import androidx.annotation.WorkerThread
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.local.dao.DatabaseDao

class DatabaseRepository(
    private val database: Database,
    private val databaseDao: DatabaseDao,
) {
    @WorkerThread
    fun checkpoint(): Int {
        return databaseDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    }

    fun getDatabaseFilePath(): String? {
        return database.openHelper.writableDatabase.path
    }
}
