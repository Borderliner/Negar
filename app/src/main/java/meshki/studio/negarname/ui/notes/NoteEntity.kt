package meshki.studio.negarname.ui.notes

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName
import meshki.studio.negarname.services.alarm.AlarmEntity
import meshki.studio.negarname.entities.ErrorMessage
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.ui.theme.*
import java.util.Date

@Entity(tableName = "notes")
data class NoteEntity(
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
    val noteEntity: NoteEntity,
    @Relation(
        parentColumn = "note_id",
        entityColumn = "alarm_id",
        associateBy = Junction(NotesAlarmsCrossRef::class)
    )
    val alarmEntities: List<AlarmEntity>
)

sealed interface NotesStateInterface {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val noteEntities: List<NoteEntity>
    val orderBy: OrderBy
    val isSearchVisible: Boolean
    val isOrderVisible: Boolean
    val searchInput: String

    data class NoNotes(
        override val isLoading: Boolean = false,
        override val errorMessages: List<ErrorMessage> = emptyList(),
        override val noteEntities: List<NoteEntity> = emptyList(),
        override val orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
        override val isSearchVisible: Boolean = false,
        override val isOrderVisible: Boolean = false,
        override val searchInput: String = ""
    ) : NotesStateInterface

    data class HasNotes(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val noteEntities: List<NoteEntity>,
        override val orderBy: OrderBy,
        override val isSearchVisible: Boolean,
        override val isOrderVisible: Boolean,
        override val searchInput: String
    ) : NotesStateInterface
}

data class NotesState(
    val noteEntities: List<NoteEntity> = emptyList(),
    val orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
    val isOrderVisible: Boolean = false,
    val isSearchVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = ""
) {
    fun toUiState(): NotesStateInterface =
        if (noteEntities.isEmpty()) {
            NotesStateInterface.NoNotes()
        } else {
            NotesStateInterface.HasNotes(
                isLoading,
                errorMessages,
                noteEntities,
                orderBy,
                isSearchVisible,
                isOrderVisible,
                searchInput
            )
        }
}
