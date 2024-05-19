package meshki.studio.negarname.ui.notes.vm

import android.app.AlarmManager
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import linc.com.amplituda.callback.AmplitudaErrorListener
import meshki.studio.negarname.R
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.entities.UiEvent
import meshki.studio.negarname.services.alarm.AlarmEntity
import meshki.studio.negarname.services.alarm.setAlarm
import meshki.studio.negarname.services.voice_recorder.VoiceRecorder
import meshki.studio.negarname.ui.notes.entities.EditNotesEvent
import meshki.studio.negarname.ui.notes.entities.NoteEntity
import meshki.studio.negarname.ui.notes.entities.NoteTextFieldState
import meshki.studio.negarname.ui.notes.entities.VoiceState
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class EditNotesViewModel(
    private val notesRepository: NotesRepository,
    val alarmService: AlarmManager?,
    savedStateHandle: SavedStateHandle,
    context: Context
) : ViewModel() {
    private val ctx = WeakReference(context)

    private val _noteEntityState = mutableStateOf(
        NoteEntity(color = NoteEntity.colors.random().toArgb(), title = "", text = "", drawing = "", voice = "")
    )
    val noteEntityState: State<NoteEntity> = _noteEntityState

    val _noteTitleState = mutableStateOf(NoteTextFieldState(hint = ctx.get()!!.getString(R.string.title)))
    val noteTitleState: State<NoteTextFieldState> = _noteTitleState

    val _noteTextState = mutableStateOf(NoteTextFieldState(hint = ctx.get()!!.getString(R.string.note_text_hint)))
    val noteTextState: State<NoteTextFieldState> = _noteTextState

    private val _isNoteModified = mutableStateOf(false)
    val isNoteModified: State<Boolean> = _isNoteModified

    val alarmEntities = mutableStateListOf<AlarmEntity>()
    private val _voiceState = mutableStateOf(VoiceState())
    val voiceState: State<VoiceState> = _voiceState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val recorder = mutableStateOf(VoiceRecorder(context, noteEntityState.value.id.toString()))

    override fun onCleared() {
        recorder.value.release()
        super.onCleared()
    }

    fun setNoteModified(flag: Boolean) {
        _isNoteModified.value = flag
    }

    init {
        savedStateHandle.get<Long>("id")?.let { noteId ->
            if (noteId > 0) {
                Timber.tag("EditViewModel").i("BEGIN")
                viewModelScope.launch {
                    notesRepository.getNoteById(noteId).collectLatest { note ->
                        Timber.tag("EditViewModel").i("$note")
                        _noteEntityState.value = note
                        _noteTitleState.value = noteTitleState.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteTextState.value = noteTextState.value.copy(
                            text = note.text,
                            isHintVisible = false
                        )

                        notesRepository.getNoteAlarmsById(note.id).collectLatest { noteAlarms ->
                            alarmEntities.addAll(noteAlarms)
                            Timber.tag("EditViewModel").i("Alarms added: $noteAlarms")
                            recorder.value.setPath(note.id.toString())
                            readVoiceToState("records/${note.id}.aac").collectLatest {
                                Timber.tag("EditViewModel").i("Voice added: $it")
                                setVoiceState(it)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun getAlarms(): Flow<List<AlarmEntity>> {
        return withContext(coroutineContext) {
            try {
                notesRepository.getNoteAlarms(noteEntityState.value)
            } catch (e: Exception) {
                Timber.tag("EditViewModel:Alarms").e(e)
                emptyFlow()
            }
        }
    }

    fun setVoiceDuration(duration: Long) {
        _voiceState.value = _voiceState.value.copy(
            duration = duration
        )
    }

    fun setVoiceAmplitudes(amps: List<Int>) {
        _voiceState.value = _voiceState.value.copy(
            amplitudes = amps
        )
    }

    fun setVoiceState(state: VoiceState) {
        _voiceState.value = _voiceState.value.copy(
            duration = state.duration,
            amplitudes = state.amplitudes
        )
    }

    suspend fun readVoiceToState(path: String): Flow<VoiceState> {
        return withContext(coroutineContext) {
                val duration = VoiceRecorder.getAudioFileDuration(
                    path,
                    ctx.get()!!
                )

                val audioPath = "${ctx.get()!!.filesDir.path}/$path"
                val amps = Amplituda(ctx.get()!!).processAudio(audioPath, Cache.withParams(Cache.REUSE))
                    .get(AmplitudaErrorListener {
                        Timber.tag("Amplituda").w(it)
                    })
                    .amplitudesAsList()

                if (duration > 0 && amps.size > 0) {
                    flow {
                        emit(
                            VoiceState(
                                duration,
                                amps
                            )
                        )
                    }
                } else {
                    emptyFlow()
                }
        }
    }

    suspend fun onEvent(event: EditNotesEvent) {
        when (event) {
            is EditNotesEvent.DrawingSaved -> {
                if (event.serializedPaths == "[]") {
                    _noteEntityState.value = _noteEntityState.value.copy(
                        drawing = ""
                    )
                } else {
                    _noteEntityState.value = _noteEntityState.value.copy(
                        drawing = event.serializedPaths
                    )
                }
            }

            is EditNotesEvent.DrawingRemoved -> {
                _noteEntityState.value = _noteEntityState.value.copy(
                    drawing = ""
                )
            }

            is EditNotesEvent.VoiceRemoved -> {
                viewModelScope.launch(coroutineContext) {
                    val path = ctx.get()!!.filesDir.path + File.separator + "records" + File.separator + noteEntityState.value.id + ".aac"
                    Timber.tag("BULLS").i(path)
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                    _voiceState.value = VoiceState()
                }
            }

            is EditNotesEvent.TitleEntered -> {
                if (event.value.isNotEmpty()) {
                    _noteTitleState.value = _noteTitleState.value.copy(
                        text = event.value,
                        isHintVisible = false
                    )
                } else {
                    _noteTitleState.value = _noteTitleState.value.copy(
                        text = "",
                        isHintVisible = true
                    )
                }
            }

            is EditNotesEvent.TitleFocusChanged -> {
                _noteTitleState.value = _noteTitleState.value.copy(
                    isHintVisible = !event.focusState.isFocused && _noteTitleState.value.text.isBlank()
                )
            }

            is EditNotesEvent.TextEntered -> {
                if (event.value.isNotEmpty()) {
                    _noteTextState.value = _noteTextState.value.copy(
                        text = event.value,
                        isHintVisible = false
                    )
                } else {
                    _noteTextState.value = _noteTextState.value.copy(
                        text = "",
                        isHintVisible = true
                    )
                }
            }

            is EditNotesEvent.TextFocusChanged -> {
                _noteTextState.value = _noteTextState.value.copy(
                    isHintVisible = !event.focusState.isFocused && _noteTextState.value.text.isBlank()
                )
            }

            is EditNotesEvent.ColorChanged -> {
                _noteEntityState.value = _noteEntityState.value.copy(
                    color = event.color
                )
            }

            is EditNotesEvent.NoteSaved -> {
                viewModelScope.launch {
                    try {
                        notesRepository.addNote(
                            NoteEntity(
                                id = if (noteEntityState.value.id >= 0) noteEntityState.value.id else 0,
                                color = noteEntityState.value.color,
                                title = noteEntityState.value.title,
                                text = noteEntityState.value.text,
                                dateCreated = Date(),
                                dateModified = Date(),
                                drawing = noteEntityState.value.drawing,
                                voice = noteEntityState.value.voice.ifEmpty { "" }
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
                                    val alarmEntity = AlarmEntity(
                                        time = cal.timeInMillis,
                                        title = event.alarmData.title,
                                        text = event.alarmData.text,
                                        critical = event.alarmData.critical
                                    )

                                    val idx: Long =
                                        notesRepository.addAlarm(noteEntityState.value.id, alarmEntity)
                                    Timber.tag("EditNotesViewModel")
                                        .i("Alarm set: ${cal.get(Calendar.DAY_OF_WEEK)}")

                                    if (idx > 0) {
                                        val alarmData = event.alarmData.copy(
                                            id = idx,
                                            time = cal.timeInMillis
                                        )
                                        result = setAlarm(ctx.get()!!, alarmData)
                                    }
                                }
                            }
                        } else {
                            val alarmEntity = AlarmEntity(
                                time = event.alarmData.time,
                                title = event.alarmData.title,
                                text = event.alarmData.text,
                                critical = event.alarmData.critical
                            )
                            val idx: Long =
                                notesRepository.addAlarm(noteEntityState.value.id, alarmEntity)
                            if (idx > 0) {
                                val alarmData = event.alarmData.copy(
                                    id = idx,
                                )
                                result = setAlarm(ctx.get()!!, alarmData)
                            }
                        }

                        if (result) {
                            alarmEntities.clear()
                            getAlarms().collectLatest {
                                alarmEntities.addAll(it)
                                Timber.tag("EditNotesViewModel").i("All Alarms: ${alarmEntities.toList()}")
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
                viewModelScope.launch(coroutineContext) {
                    try {
                        notesRepository.deleteNoteAlarms(noteEntityState.value.id)
                        alarmEntities.clear()
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