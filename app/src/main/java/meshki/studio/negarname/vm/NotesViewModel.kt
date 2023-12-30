package meshki.studio.negarname.vm

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.NotesState
import meshki.studio.negarname.entity.OrderBy
import meshki.studio.negarname.entity.OrderType
import meshki.studio.negarname.entity.Tool

sealed class NotesEvent {
    data class NotesOrdered(val orderBy: OrderBy) : NotesEvent()
    data class NoteDeleted(val note: Note) : NotesEvent()
    data class NotePinToggled(val note: Note) : NotesEvent()
    data class NoteQueried(val query: String, val orderBy: OrderBy) : NotesEvent()
    data object NoteRestored : NotesEvent()
    data object OrderToggled : NotesEvent()
    data object SearchToggled : NotesEvent()
}

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val state = MutableStateFlow(NotesState(isLoading = true))
    val uiState = state.map(NotesState::toUiState).stateIn(
        viewModelScope, SharingStarted.Eagerly, state.value.toUiState()
    )
    private val _notesState = mutableStateOf(NotesState())
    val notesState: State<NotesState> = _notesState

    val orderTool = mutableStateOf(Tool("order"))
    val searchTool =  mutableStateOf(Tool("search"))

    val toolbox = mutableStateListOf(orderTool, searchTool)

    private var deletedNote: Note? = null
    private var noteJob: Job? = null

    init {
        viewModelScope.launch {
            getNotesOrdered(OrderBy.Date(OrderType.Descending))
        }
    }

    suspend fun onEvent(event: NotesEvent) {
        println(event)
        when (event) {
            is NotesEvent.NotesOrdered -> {
                if (state.value.orderBy::class == event.orderBy::class &&
                    state.value.orderBy.orderType == event.orderBy.orderType
                ) {
                    // if u pressed same button again which was already checked
                    // we don't need to change anything
                    return
                }
                getNotesOrdered(event.orderBy)
            }

            is NotesEvent.NoteDeleted -> {
                viewModelScope.launch {
                    // save deleted note reference
                    notesRepository.deleteNote(event.note)
                    deletedNote = event.note
                }
            }

            is NotesEvent.NoteRestored -> {
                viewModelScope.launch {
                    notesRepository.addNote(deletedNote ?: return@launch)
                    deletedNote = null
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
                findNotesOrdered(event.query, event.orderBy)
            }

            is NotesEvent.NotePinToggled -> {
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

    private suspend fun findNotesOrdered(query: String, orderBy: OrderBy) {
        noteJob?.cancel()
        noteJob = notesRepository.findNotesOrdered(
            query,
            orderBy
        ).data?.onEach { notes ->
            state.update {
                it.copy(notes = notes, orderBy = orderBy)
            }
        }?.launchIn(viewModelScope)
    }

    private suspend fun getNotesOrdered(orderBy: OrderBy) {
        noteJob?.cancel()
        noteJob = notesRepository.getNotesOrdered(orderBy).data?.onEach { notes ->
            println("Notes ordered: $notes")
            state.update {
                it.copy(notes = notes, orderBy = orderBy)
            }
        }?.launchIn(viewModelScope)
    }

    suspend fun onToolClicked(id: String) {
        try {
            val currentTool = toolbox.find { it.value.id == id }!!
            if (currentTool.value.visibility.value) {
                // Instantly close other tools
                toolbox.forEach {
                    if (it.value.id != id) {
                        it.value.animation.value.snapTo(
                            it.value.animation.value.lowerBound ?: 0f
                        )
                        it.value.visibility.value = false
                    } else {
                        // Animated hide current Tool
                        currentTool.value.animation.value.animateTo(
                            currentTool.value.animation.value.lowerBound ?: 0f,
                            tween(320, 20, easing = FastOutSlowInEasing)
                        )
                        currentTool.value.visibility.value = false
                    }
                }
            } else {
                // Instantly hide other tools
                toolbox.forEach {
                    if (it.value.id != id) {
                        it.value.animation.value.snapTo(
                            it.value.animation.value.lowerBound ?: 0f
                        )
                        it.value.visibility.value = false
                    } else {
                        // Animated show current Tool
                        currentTool.value.animation.value.animateTo(
                            currentTool.value.animation.value.upperBound ?: Float.MAX_VALUE,
                            tween(400, 0, easing = FastOutSlowInEasing)
                        )
                        currentTool.value.visibility.value = true
                    }
                }
            }
        } catch (err: Error) {
            error(err)
        }
    }
}
