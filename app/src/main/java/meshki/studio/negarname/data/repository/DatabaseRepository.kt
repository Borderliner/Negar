package meshki.studio.negarname.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.local.dao.DatabaseDao

class DatabaseRepository(
    private val database: Database,
    private val databaseDao: DatabaseDao,
) {
    fun checkpoint() {
        databaseDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full);"))
    }

    fun getDatabaseFilePath(): String? {
        return database.openHelper.writableDatabase.path
    }
}
