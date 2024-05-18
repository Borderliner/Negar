package meshki.studio.negarname.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.local.dao.AppDao

class AppRepository(
    private val database: Database,
    private val appDao: AppDao,
) {
    fun checkpoint(): Int {
        return appDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    }

    fun getDatabaseFilePath(): String? {
        return database.openHelper.writableDatabase.path
    }
}
