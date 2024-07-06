package meshki.studio.negarname.data.local.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DatabaseDao {
    @RawQuery
    fun checkpoint(query: SupportSQLiteQuery): Int
}
