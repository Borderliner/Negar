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

sealed interface NotesStateInterface {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val notes: List<Note>
    val orderBy: OrderBy
    val isSearchVisible: Boolean
    val isOrderSectionVisible: Boolean
    val searchInput: String

    data class NoNotes(
        override val isLoading: Boolean = false,
        override val errorMessages: List<ErrorMessage> = emptyList(),
        override val notes: List<Note> = emptyList(),
        override val orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
        override val isSearchVisible: Boolean = false,
        override val isOrderSectionVisible: Boolean = false,
        override val searchInput: String = ""
    ) : NotesStateInterface

    data class HasNotes(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val notes: List<Note>,
        override val orderBy: OrderBy,
        override val isSearchVisible: Boolean,
        override val isOrderSectionVisible: Boolean,
        override val searchInput: String
    ) : NotesStateInterface
}

data class NotesState(
    val notes: List<Note> = emptyList(),
    val orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
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
                isOrderSectionVisible,
                searchInput
            )
        }
}

class InvalidNoteException(message: String) : Exception(message)
