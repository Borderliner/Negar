package meshki.studio.negarname.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.local.dao.AppDao
import meshki.studio.negarname.entities.UiState
import meshki.studio.negarname.ui.util.handleTryCatch

class AppRepository(
    private val database: Database,
    private val appDao: AppDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun checkpoint(): UiState<Int> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(appDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)")))
            }
        }
    }

    suspend fun getDatabaseFilePath(): UiState<String> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(database.openHelper.writableDatabase.path)
            }
        }
    }
}
