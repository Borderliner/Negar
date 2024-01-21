package meshki.studio.negarname.vm

import android.app.AlarmManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.entity.Alarm
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.NoteAndAlarm
import meshki.studio.negarname.entity.UiEvent
import meshki.studio.negarname.service.AlarmData
import meshki.studio.negarname.service.setAlarm
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.Date

sealed class EditNotesEvent {
    data class TitleEntered(val value: String) : EditNotesEvent()
    data class TextEntered(val value: String) : EditNotesEvent()
    data class TitleFocusChanged(val focusState: FocusState) : EditNotesEvent()
    data class TextFocusChanged(val focusState: FocusState) : EditNotesEvent()
    data class ColorChanged(val color: Int) : EditNotesEvent()
    data object NoteSaved : EditNotesEvent()
    data class DrawingSaved(val serializedPaths: String) : EditNotesEvent()
    data class SetAlarm(val alarmData: AlarmData) : EditNotesEvent()
    data class DeleteNoteAlarms(val noteId: Long) : EditNotesEvent()
}

class EditNotesViewModel(
    private val notesRepository: NotesRepository,
    val alarmService: AlarmManager?,
    savedStateHandle: SavedStateHandle,
    context: Context,
) : ViewModel() {
    private val _noteState = mutableStateOf(
        Note(color = Note.colors.random().toArgb(), title = "", text = "", drawing = "", voice = "")
    )
    val noteState: State<Note> = _noteState

    val alarms = mutableStateListOf<Alarm>()

    private val ctx = WeakReference(context)

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
                Timber.tag("EditViewModel").i("BEGIN")
                viewModelScope.launch {
                    notesRepository.getNoteById(noteId).data?.collectLatest { note ->
                        Timber.tag("EditViewModel").i("$note")
                        _noteState.value = note
                        isTitleHintVisible = note.title.isEmpty()
                        isTextHintVisible = note.title.isEmpty()
                        notesRepository.getNoteAlarmsById(note.id).data?.collectLatest {
                            alarms.addAll(it)
                            Timber.tag("EditViewModel").i("Alarms added: $it")
                        }
                    }
                }
            }
        }
    }

    suspend fun getAlarms(): Flow<List<Alarm>> {
        return try {
            notesRepository.getNoteAlarms(noteState.value).data!!
        } catch (e: Exception) {
            Timber.tag("EditViewModel:Alarms").e(e)
            return emptyFlow()
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
                                drawing = noteState.value.drawing,
                                voice = noteState.value.voice.ifEmpty { "" }
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

            is EditNotesEvent.SetAlarm -> {
                viewModelScope.launch {
                    try {
                        val week = event.alarmData.week
                        var result = true

                        if (event.alarmData.repeating) {
                            week.list.forEach {
                                if (it.value) {
                                    val cal = Calendar.getInstance()
                                    cal.timeInMillis = event.alarmData.time
                                    cal.set(Calendar.DAY_OF_WEEK, it.calendarIndex)
                                    val alarm = Alarm(
                                        time = cal.timeInMillis,
                                        title = event.alarmData.title,
                                        text = event.alarmData.text,
                                        critical = event.alarmData.critical
                                    )

                                    val idx: Long? =
                                        notesRepository.addAlarm(noteState.value.id, alarm).data
                                    Timber.tag("EditNotesViewModel")
                                        .i("Alarm set: ${cal.get(Calendar.DAY_OF_WEEK)}")

                                    if (idx != null && idx > 0) {
                                        val alarmData = event.alarmData.copy(
                                            id = idx,
                                            time = cal.timeInMillis
                                        )
                                        result = setAlarm(ctx.get()!!, alarmData)
                                    }
                                }
                            }
                        } else {
                            val alarm = Alarm(
                                time = event.alarmData.time,
                                title = event.alarmData.title,
                                text = event.alarmData.text,
                                critical = event.alarmData.critical
                            )
                            val idx: Long? =
                                notesRepository.addAlarm(noteState.value.id, alarm).data
                            if (idx != null && idx > 0) {
                                val alarmData = event.alarmData.copy(
                                    id = idx,
                                )
                                result = setAlarm(ctx.get()!!, alarmData)
                            }
                        }

                        if (result) {
                            alarms.clear()
                            getAlarms().collectLatest {
                                alarms.addAll(it)
                                Timber.tag("EditNotesViewModel").i("All Alarms: ${alarms.toList()}")
                            }
                        }
                    } catch (e: Exception) {
                        Timber.tag("EditNotesViewModel").e(e)
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't set alert: ${e.message}"
                            )
                        )
                    }
                }
            }

            is EditNotesEvent.DeleteNoteAlarms -> {
                viewModelScope.launch {
                    try {
                        notesRepository.deleteNoteAlarms(noteState.value.id)
                        alarms.clear()
                    } catch (e: Exception) {
                        Timber.tag("EditNotesViewModel").e(e)
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't set alert: ${e.message}"
                            )
                        )
                    }
                }
            }
        }
    }
}