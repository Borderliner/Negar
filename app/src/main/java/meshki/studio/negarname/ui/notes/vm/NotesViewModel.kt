package meshki.studio.negarname.ui.notes.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.ui.notes.entities.NoteEntity
import meshki.studio.negarname.ui.notes.entities.NotesEvent
import meshki.studio.negarname.ui.notes.entities.NotesState
import kotlin.coroutines.CoroutineContext

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ViewModel() {
    private val state = MutableStateFlow(NotesState(isLoading = true))
    val uiState = state.map(NotesState::toUiState).stateIn(
        viewModelScope, SharingStarted.Eagerly, state.value.toUiState()
    )
    private val _notesState = mutableStateOf(NotesState())
    val notesState: State<NotesState> = _notesState

    private var deletedNoteEntity: NoteEntity? = null
    private var noteJob: Job? = null

    init {
        viewModelScope.launch(coroutineContext) {
            getNotesOrdered(OrderBy.Date(OrderType.Descending))
        }
    }

    suspend fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.NotesOrdered -> {
                if (state.value.orderBy::class == event.orderBy::class &&
                    state.value.orderBy.orderType == event.orderBy.orderType
                ) {
                    // if u pressed same button again which was already checked
                    // we don't need to change anything
                    return
                }
                viewModelScope.launch(coroutineContext) {
                    getNotesOrdered(event.orderBy)
                }
            }

            is NotesEvent.NoteDeleted -> {
                viewModelScope.launch(coroutineContext) {
                    // save deleted note reference
                    notesRepository.deleteNote(event.noteEntity)
                    deletedNoteEntity = event.noteEntity
                }
            }

            is NotesEvent.NoteRestored -> {
                viewModelScope.launch(coroutineContext) {
                    notesRepository.addNote(deletedNoteEntity ?: return@launch)
                    deletedNoteEntity = null
                }
            }

            is NotesEvent.OrderToggled -> {
                state.update {
                    it.copy(
                        isOrderVisible = !state.value.isOrderVisible,
                        isSearchVisible = false
                    )
                }
            }

            is NotesEvent.SearchToggled -> {
                state.update {
                    it.copy(
                        isSearchVisible = !state.value.isSearchVisible,
                        isOrderVisible = false
                    )
                }
            }

            is NotesEvent.NoteQueried -> {
                viewModelScope.launch(coroutineContext) {
                    findNotesOrdered(event.query, event.orderBy)
                }
            }

            is NotesEvent.NotePinToggled -> {
                viewModelScope.launch(coroutineContext) {
                    if (event.noteEntity.pinned) {
                        notesRepository.unpinNote(event.noteEntity)
                    } else {
                        notesRepository.pinNote(event.noteEntity)
                    }
                }
            }
        }
    }

    private suspend fun findNotesOrdered(query: String, orderBy: OrderBy) {
        noteJob?.cancel()
        noteJob = notesRepository.findNotesOrdered(
            query,
            orderBy
        ).onEach { notes ->
            state.update {
                it.copy(noteEntities = notes, orderBy = orderBy)
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun getNotesOrdered(orderBy: OrderBy) {
        noteJob?.cancel()
        noteJob = notesRepository.getNotesOrdered(orderBy).onEach { notes ->
            println("Notes ordered: $notes")
            state.update {
                it.copy(noteEntities = notes, orderBy = orderBy)
            }
        }.launchIn(viewModelScope)
    }
}
