package meshki.studio.negarname.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import meshki.studio.negarname.ui.theme.*
import java.util.Date

@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @SerializedName("title")
    @ColumnInfo(name = "title")
    val title: String,

    @SerializedName("text")
    @ColumnInfo(name = "text")
    val text: String,

    @SerializedName("color")
    @ColumnInfo(name = "color")
    val color: Int,

    @SerializedName("pinned")
    @ColumnInfo(name = "pinned")
    val pinned: Boolean = false,

    @SerializedName("date_created")
    @ColumnInfo(name = "date_created")
    val dateCreated: Date,

    @SerializedName("date_modified")
    @ColumnInfo(name = "date_modified")
    val dateModified: Date
) {
    companion object {
        val colors = listOf(PastelGreen, PastelBlue, PastelPink, PastelYellow, PastelOrange)
    }
}

class InvalidNoteException(message: String): Exception(message)
