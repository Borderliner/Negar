package meshki.studio.negarname.ui.todos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import meshki.studio.negarname.ui.theme.*
import java.util.Date

@Entity(tableName = "todos")

data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Long,

    @SerializedName("text")
    @ColumnInfo(name = "text")
    val text: String,

    @SerializedName("color")
    @ColumnInfo(name = "color")
    val color: Int,

    @SerializedName("pinned")
    @ColumnInfo(name = "pinned")
    val pinned: Boolean = false,

    @SerializedName("is_completed")
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,

    @SerializedName("date_created")
    @ColumnInfo(name = "date_created")
    val dateCreated: Date,

    @SerializedName("date_modified")
    @ColumnInfo(name = "date_modified")
    val dateModified: Date
) {
    companion object {
        val todoColors = listOf(PastelGreen, PastelBlue, PastelPink, PastelYellow, PastelOrange)
    }
}

class InvalidTodoException(message: String): Exception(message)
