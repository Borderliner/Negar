package meshki.studio.negarname.data.local.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface AppDao {
    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}
