package meshki.studio.negarname.vm

import android.app.AlarmManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.UiEvent
import java.util.Date

sealed class EditNotesEvent {
    data class TitleEntered(val value: String) : EditNotesEvent()
    data class TextEntered(val value: String) : EditNotesEvent()
    data class TitleFocusChanged(val focusState: FocusState) : EditNotesEvent()
    data class TextFocusChanged(val focusState: FocusState) : EditNotesEvent()
    data class ColorChanged(val color: Int) : EditNotesEvent()
    data object NoteSaved : EditNotesEvent()
    data class DrawingSaved(val serializedPaths: String) : EditNotesEvent()
}

class EditNotesViewModel(
    private val notesRepository: NotesRepository,
    val alarmService: AlarmManager?,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _noteState = mutableStateOf(
        Note(color = Note.colors.random().toArgb(), title = "", text = "", drawing = "")
    )
    val noteState: State<Note> = _noteState

    private val _isNoteModified = mutableStateOf(false)
    var isNoteModified
        get() = _isNoteModified.value
        set(value) {
            _isNoteModified.value = value
        }

    private val _isTextHintVisible = mutableStateOf(false)
    var isTextHintVisible
        get() = _isTextHintVisible.value
        set(value) {
            _isTextHintVisible.value = value
        }

    private val _isTitleHintVisible = mutableStateOf(false)
    var isTitleHintVisible
        get() = _isTitleHintVisible.value
        set(value) {
            _isTitleHintVisible.value = value
        }

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Long>("id")?.let { noteId ->
            if (noteId > 0) {
                viewModelScope.launch {
                    notesRepository.getNoteById(noteId).data?.also { noteFlow ->
                        noteFlow.collectLatest { note ->
                            _noteState.value = note
                            isTitleHintVisible = note.title.isEmpty()
                            isTextHintVisible = note.title.isEmpty()
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: EditNotesEvent) {
        when (event) {
            is EditNotesEvent.DrawingSaved -> {
                _noteState.value = _noteState.value.copy(
                    drawing = event.serializedPaths
                )
            }
            is EditNotesEvent.TitleEntered -> {
                _noteState.value = _noteState.value.copy(
                    title = event.value
                )
            }

            is EditNotesEvent.TitleFocusChanged -> {
//                _noteTitle.value = noteTitle.value.copy(
//                    isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank()
//                )
            }

            is EditNotesEvent.TextEntered -> {
                _noteState.value = _noteState.value.copy(
                    text = event.value
                )
            }

            is EditNotesEvent.TextFocusChanged -> {
                //
            }

            is EditNotesEvent.ColorChanged -> {
                _noteState.value = _noteState.value.copy(
                    color = event.color
                )
            }

            is EditNotesEvent.NoteSaved -> {
                viewModelScope.launch {
                    try {
                        notesRepository.addNote(
                            Note(
                                id = if (noteState.value.id >= 0) noteState.value.id else 0,
                                color = noteState.value.color,
                                title = noteState.value.title,
                                text = noteState.value.text,
                                dateCreated = Date(),
                                dateModified = Date(),
                                drawing = noteState.value.drawing
                            )
                        )
                        _eventFlow.emit(UiEvent.NoteSaved)
                    } catch (e: Exception) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
        }
    }
}