package meshki.studio.negarname.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.local.dao.AppDao
import meshki.studio.negarname.entity.UiStates
import meshki.studio.negarname.util.handleTryCatch

interface AppRepository {
    suspend fun checkpoint(): UiStates<Int>
    suspend fun getDatabaseFilePath(): UiStates<String>
}

class AppRepositoryImpl(
    private val database: Database,
    private val appDao: AppDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): AppRepository {
    override suspend fun checkpoint(): UiStates<Int> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(appDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)")))
            }
        }
    }

    override suspend fun getDatabaseFilePath(): UiStates<String> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(database.openHelper.writableDatabase.path)
            }
        }
    }
}