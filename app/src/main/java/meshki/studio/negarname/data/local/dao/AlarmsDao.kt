package meshki.studio.negarname.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import meshki.studio.negarname.services.alarm.AlarmEntity

@Dao
interface AlarmsDao : BaseDao<AlarmEntity> {
    @Query("SELECT * FROM alarms ORDER BY alarm_id DESC")
    fun getAll(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE alarm_id = :id ORDER BY alarm_id DESC")
    fun getById(id: Long): Flow<AlarmEntity>
}
