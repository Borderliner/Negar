package meshki.studio.negarname.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import meshki.studio.negarname.entity.Alarm
import meshki.studio.negarname.entity.NoteAndAlarm

@Dao
interface AlarmsDao : BaseDao<Alarm> {
    @Query("SELECT * FROM alarms ORDER BY alarm_id DESC")
    fun getAll(): Flow<List<Alarm>>

    @Query("SELECT * FROM alarms WHERE alarm_id = :id ORDER BY alarm_id DESC")
    fun getById(id: Long): Flow<Alarm>
}
