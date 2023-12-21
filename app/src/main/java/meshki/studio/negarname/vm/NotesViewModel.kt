package meshki.studio.negarname.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.entity.ErrorMessage
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.OrderBy
import meshki.studio.negarname.entity.OrderType

sealed interface NotesStateInterface {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val notes: List<Note>
    val orderBy: OrderBy
    val isSearchVisible: Boolean
    val isOrderSectionVisible: Boolean
    val searchInput: String

    data class NoNotes(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val notes: List<Note>,
        override val orderBy: OrderBy,
        override val isSearchVisible: Boolean,
        override val isOrderSectionVisible: Boolean,
        override val searchInput: String
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
            NotesStateInterface.NoNotes(
                isLoading,
                errorMessages,
                notes,
                orderBy,
                isSearchVisible,
                isOrderSectionVisible,
                searchInput
            )
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

sealed class NotesEvent {
    data class Order(val orderBy: OrderBy) : NotesEvent()
    data class DeleteNote(val note: Note) : NotesEvent()
    data class TogglePinNote(val note: Note) : NotesEvent()
    data class Query(val string: String, val orderBy: OrderBy) : NotesEvent()
    data object RestoreNote : NotesEvent()
    data object ToggleOrderSection : NotesEvent()
    data object ToggleSearchSection : NotesEvent()
}

data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val state = MutableStateFlow(NotesState(isLoading = true))
    val uiState = state.map(NotesState::toUiState).stateIn(
        viewModelScope, SharingStarted.Eagerly, state.value.toUiState()
    )
    private val _notesState = mutableStateOf(NotesState())
    val notesState: State<NotesState> = _notesState

    private var deletedNote: Note? = null
    private var getNoteJob: Job? = null

    init {
        viewModelScope.launch {
            getNotes(OrderBy.Date(OrderType.Descending))
        }
    }

    suspend fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.orderBy::class == event.orderBy::class &&
                    state.value.orderBy.orderType == event.orderBy.orderType
                ) {
                    // if u pressed same button again which was already checked
                    // we don't need to change anything
                    return
                }
                getNotes(event.orderBy)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    // save deleted note reference
                    notesRepository.deleteNote(event.note)
                    deletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    notesRepository.addNote(deletedNote ?: return@launch)
                    deletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                state.update {
                    it.copy(
                        isOrderSectionVisible = !state.value.isOrderSectionVisible,
                        isSearchVisible = false
                    )
                }
            }

            is NotesEvent.ToggleSearchSection -> {
                state.update {
                    it.copy(
                        isSearchVisible = !state.value.isSearchVisible,
                        isOrderSectionVisible = false
                    )
                }
            }

            is NotesEvent.Query -> {
                getNotes(event.orderBy, event.string)
            }

            is NotesEvent.TogglePinNote -> {
                viewModelScope.launch {
                    if (event.note.pinned) {
                        notesRepository.unpinNote(event.note)
                    } else {
                        notesRepository.pinNote(event.note)
                    }
                }
            }
        }
    }

    private suspend fun findNotesUnordered(query: String) {
        getNoteJob?.cancel()
        getNoteJob = notesRepository.findNotes(query).data?.onEach { foundNotes ->
            if (query.trim().isNotBlank()) {
                state.update {
                    it.copy(notes = foundNotes)
                }
            }
        }?.launchIn(viewModelScope)
    }

    private suspend fun findNotesOrdered(orderBy: OrderBy, query: String) {
        getNoteJob?.cancel()
        getNoteJob = notesRepository.findNotesOrdered(
            query,
            orderBy as OrderBy.Date
        ).data?.onEach { foundNotes ->
            state.update {
                it.copy(notes = foundNotes, orderBy = orderBy)
            }
        }?.launchIn(viewModelScope)
    }

    private suspend fun getNotesOrdered(orderBy: OrderBy) {
        getNoteJob?.cancel()
        getNoteJob = notesRepository.getNotesOrdered(orderBy).data?.onEach { allNotes ->
            state.update { it ->
                it.copy(notes = allNotes.sortedBy { !it.pinned })
            }
        }?.launchIn(viewModelScope)
    }

    private suspend fun getNotes(orderBy: OrderBy, query: String = String()) {
        if (query.isNotBlank()) {
            findNotesOrdered(orderBy, query)
        } else {
            getNotesOrdered(orderBy)
        }
    }
}
