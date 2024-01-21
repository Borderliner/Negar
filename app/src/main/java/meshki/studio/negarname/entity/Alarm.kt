package meshki.studio.negarname.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("alarm_id")
    @ColumnInfo(name = "alarm_id", index = true)
    val id: Long = 0,

    @SerializedName("time")
    @ColumnInfo(name = "time")
    val time: Long = 0,

    @SerializedName("title")
    @ColumnInfo(name = "title")
    val title: String,

    @SerializedName("text")
    @ColumnInfo(name = "text")
    val text: String,

    @SerializedName("critical")
    @ColumnInfo(name = "critical")
    val critical: Boolean = false
)
