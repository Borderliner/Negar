package meshki.studio.negarname.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName
import meshki.studio.negarname.ui.theme.*
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("note_id")
    @ColumnInfo(name = "note_id", index = true)
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

    @SerializedName("voice")
    @ColumnInfo(name = "voice")
    val voice: String,

    @SerializedName("drawing")
    @ColumnInfo(name = "drawing")
    val drawing: String,

    @SerializedName("pinned")
    @ColumnInfo(name = "pinned")
    val pinned: Boolean = false,

    @SerializedName("date_created")
    @ColumnInfo(name = "date_created")
    val dateCreated: Date = Date(),

    @SerializedName("date_modified")
    @ColumnInfo(name = "date_modified")
    val dateModified: Date = Date()
) {
    companion object {
        val colors = listOf(
            PastelGreen,
            PastelBlue,
            PastelPink,
            PastelYellow,
            PastelOrange,
            PastelLavender,
            PastelLime,
            PastelPurple
        )
    }
}

@Entity(
    tableName = "notes_alarms_ref",
    primaryKeys = ["note_id", "alarm_id"],
)
data class NotesAlarmsCrossRef (
    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,
    @ColumnInfo(name = "alarm_id", index = true)
    val alarmId: Long
)

data class NoteAndAlarm (
    @Embedded
    val note: Note,
    @Relation(
        parentColumn = "note_id",
        entityColumn = "alarm_id",
        associateBy = Junction(NotesAlarmsCrossRef::class)
    )
    val alarms: List<Alarm>
)

sealed interface NotesStateInterface {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val notes: List<Note>
    val orderBy: OrderBy
    val isSearchVisible: Boolean
    val isOrderVisible: Boolean
    val searchInput: String

    data class NoNotes(
        override val isLoading: Boolean = false,
        override val errorMessages: List<ErrorMessage> = emptyList(),
        override val notes: List<Note> = emptyList(),
        override val orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
        override val isSearchVisible: Boolean = false,
        override val isOrderVisible: Boolean = false,
        override val searchInput: String = ""
    ) : NotesStateInterface

    data class HasNotes(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val notes: List<Note>,
        override val orderBy: OrderBy,
        override val isSearchVisible: Boolean,
        override val isOrderVisible: Boolean,
        override val searchInput: String
    ) : NotesStateInterface
}

data class NotesState(
    val notes: List<Note> = emptyList(),
    val orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
    val isOrderVisible: Boolean = false,
    val isSearchVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = ""
) {
    fun toUiState(): NotesStateInterface =
        if (notes.isEmpty()) {
            NotesStateInterface.NoNotes()
        } else {
            NotesStateInterface.HasNotes(
                isLoading,
                errorMessages,
                notes,
                orderBy,
                isSearchVisible,
                isOrderVisible,
                searchInput
            )
        }
}

class InvalidNoteException(message: String) : Exception(message)
