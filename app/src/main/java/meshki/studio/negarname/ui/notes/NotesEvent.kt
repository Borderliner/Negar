package meshki.studio.negarname.ui.notes

import meshki.studio.negarname.entities.OrderBy

sealed class NotesEvent {
    data class NotesOrdered(val orderBy: OrderBy) : NotesEvent()
    data class NoteDeleted(val noteEntity: NoteEntity) : NotesEvent()
    data class NotePinToggled(val noteEntity: NoteEntity) : NotesEvent()
    data class NoteQueried(val query: String, val orderBy: OrderBy) : NotesEvent()
    data object NoteRestored : NotesEvent()
    data object OrderToggled : NotesEvent()
    data object SearchToggled : NotesEvent()
}
