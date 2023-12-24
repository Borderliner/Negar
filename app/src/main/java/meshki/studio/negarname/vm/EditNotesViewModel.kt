package meshki.studio.negarname.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.UiEvent
import java.util.Date

sealed class EditNotesEvent{
    data class EnteredTitle(val value: String) : EditNotesEvent()
    data class EnteredContent(val value: String): EditNotesEvent()
    data class ChangeTitleFocus(val focusState: FocusState): EditNotesEvent()
    data class ChangeContentFocus(val focusState: FocusState): EditNotesEvent()
    data class ChangeColor(val color: Int): EditNotesEvent()
    data object SavedNote: EditNotesEvent()
}
class EditNotesViewModel (
    private val notesRepository: NotesRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            "",
            ""
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        RichTextState()
    )
    val noteContent: State<RichTextState> = _noteContent

    private val _noteColor = mutableIntStateOf(Note.colors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    var noteModified = mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Long = -1

    init {
        savedStateHandle.get<Long>("id")?.let{ noteId ->
            if(noteId > 0) {
                viewModelScope.launch {
                    notesRepository.getNoteById(noteId).data?.also { noteFlow ->
                        noteFlow.collectLatest { note ->
                            currentNoteId = note.id
                            _noteTitle.value = noteTitle.value.copy(
                                text = note.title,
                                isHintVisible = false
                            )
                            _noteContent.value.setMarkdown(note.text)
                            _noteColor.intValue = note.color
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: EditNotesEvent){
        when(event){
            is EditNotesEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value
                )
            }
            is EditNotesEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank()
                )
            }
            is EditNotesEvent.EnteredContent -> {
                //
            }
            is EditNotesEvent.ChangeContentFocus -> {
                //
            }
            is EditNotesEvent.ChangeColor -> {
                _noteColor.intValue = event.color
            }
            is EditNotesEvent.SavedNote -> {
                viewModelScope.launch {
                    try {
                        println("Adding note")
                        notesRepository.addNote(
                            Note(
                                id = if (currentNoteId >= 0) currentNoteId else 0,
                                title = noteTitle.value.text,
                                text = noteContent.value.toMarkdown(),
                                dateCreated = Date(),
                                dateModified = Date(),
                                color = noteColor.value
                            )
                        )
                        _eventFlow.emit(UiEvent.SavedNote)
                    } catch (e: Exception){
                        _eventFlow.emit(
                            UiEvent.showSnackBar(
                            message = e.message ?: "Couldn't save note"
                        ))
                    }
                }
            }
        }
    }
}