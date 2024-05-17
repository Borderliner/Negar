package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.AlarmsDao
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.services.alarm.AlarmEntity
import meshki.studio.negarname.ui.notes.NoteEntity
import meshki.studio.negarname.ui.notes.NotesAlarmsCrossRef
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.entities.UiState
import meshki.studio.negarname.ui.util.handleTryCatch
import meshki.studio.negarname.ui.util.handleTryCatchWithContext

class NotesRepository(
    private val notesDao: NotesDao,
    private val alarmsDao: AlarmsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun addNote(noteEntity: NoteEntity): UiState<Long> {
        return handleTryCatchWithContext(dispatcher) {
            if (noteEntity.title.isBlank() && noteEntity.text.isBlank()) {
                UiState.Error<Long>("Note title is needed")
            }
            if (noteEntity.id < 0) {
                UiState.Error<Long>("Note id cannot be zero.")
            }

            val numberOfNotesEntered = notesDao.insert(noteEntity)
            UiState.Success(numberOfNotesEntered)
        }
    }

    suspend fun getNotes(): UiState<Flow<List<NoteEntity>>> {
        return handleTryCatchWithContext(dispatcher) {
            UiState.Success(data = notesDao.getAll())
        }
    }

    suspend fun getNoteById(id: Long): UiState<Flow<NoteEntity>> {
        return handleTryCatchWithContext(dispatcher) {
            UiState.Success(data = notesDao.getById(id))
        }
    }

    suspend fun findNotes(query: String): UiState<Flow<List<NoteEntity>>> {
        return handleTryCatchWithContext(dispatcher) {
            UiState.Success(data = notesDao.find(query))
        }
    }

    suspend fun updateNote(noteEntity: NoteEntity): UiState<Boolean> {
        return handleTryCatchWithContext(dispatcher) {
            notesDao.update(noteEntity)
            UiState.Success(true)
        }
    }

    suspend fun deleteNote(noteEntity: NoteEntity): UiState<Boolean> {
        return handleTryCatchWithContext(dispatcher) {
            notesDao.delete(noteEntity)
            UiState.Success(true)
        }
    }

    suspend fun pinNote(noteEntity: NoteEntity): UiState<Boolean> {
        return handleTryCatchWithContext(dispatcher) {
            notesDao.update(noteEntity.copy(pinned = true))
            UiState.Success(true)
        }
    }

    suspend fun unpinNote(noteEntity: NoteEntity): UiState<Boolean> {
        return handleTryCatchWithContext(dispatcher) {
            notesDao.update(noteEntity.copy(pinned = false))
            UiState.Success(true)
        }
    }

    suspend fun togglePinNote(noteEntity: NoteEntity): UiState<Boolean> {
        return handleTryCatchWithContext(dispatcher) {
            notesDao.update(noteEntity.copy(pinned = !noteEntity.pinned))
            UiState.Success(true)
        }
    }

    suspend fun getNotesOrdered(orderBy: OrderBy): UiState<Flow<List<NoteEntity>>> {
        return handleTryCatchWithContext(dispatcher) {
            val notes = notesDao.getAll().map { notes ->
                if (orderBy.orderType == OrderType.Ascending) {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> notes.sortedBy { it.title.lowercase() }
                        is OrderBy.Date -> notes.sortedBy { it.dateModified }
                        is OrderBy.Color -> notes.sortedBy { it.color }
                        // Fallback to date
                        is OrderBy.Completed -> notes.sortedBy { it.dateModified }
                    }
                } else {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> notes.sortedByDescending { it.title.lowercase() }
                        is OrderBy.Date -> notes.sortedByDescending { it.dateModified }
                        is OrderBy.Color -> notes.sortedByDescending { it.color }
                        is OrderBy.Completed -> notes.sortedBy { it.dateModified }
                    }
                }
            }
            UiState.Success(notes)
        }
    }

    suspend fun findNotesOrdered(
        query: String,
        orderBy: OrderBy,
    ): UiState<Flow<List<NoteEntity>>> {
        return handleTryCatchWithContext(dispatcher) {
            val notes = notesDao.find(query).map { notes ->
                if (orderBy.orderType == OrderType.Ascending) {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> notes.sortedBy { it.title.lowercase() }
                        is OrderBy.Date -> notes.sortedBy { it.dateModified }
                        is OrderBy.Color -> notes.sortedBy { it.color }
                        is OrderBy.Completed -> notes.sortedBy { it.dateModified }
                    }
                } else {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> notes.sortedByDescending { it.title.lowercase() }
                        is OrderBy.Date -> notes.sortedByDescending { it.dateModified }
                        is OrderBy.Color -> notes.sortedByDescending { it.color }
                        is OrderBy.Completed -> notes.sortedBy { it.dateModified }
                    }
                }
            }

            UiState.Success(notes)
        }
    }

    suspend fun getNoteAlarms(noteEntity: NoteEntity): UiState<Flow<List<AlarmEntity>>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(getNoteAlarmsById(noteEntity.id).data)
            }
        }
    }

    suspend fun getNoteAlarmsById(id: Long): UiState<Flow<List<AlarmEntity>>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(notesDao.getNoteAlarmsById(id).map {
                    it.alarmEntities
                })
            }
        }
    }

    suspend fun getAlarmById(id: Long): UiState<Flow<AlarmEntity>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(alarmsDao.getById(id))
            }
        }
    }

    suspend fun addAlarm(noteId: Long, alarmEntity: AlarmEntity): UiState<Long> {
        return withContext(dispatcher) {
            handleTryCatch {
                val id = alarmsDao.insert(alarmEntity)
                notesDao.insertNoteAlarm(
                    NotesAlarmsCrossRef(
                    noteId,
                    id
                )
                )
                UiState.Success(id)
            }
        }
    }

    suspend fun deleteNoteAlarms(noteId: Long): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                notesDao.getNoteAlarmsById(noteId).collectLatest {
                    alarmsDao.delete(it.alarmEntities)
                    notesDao.deleteNoteAlarmsRef(noteId)
                }
                UiState.Success(true)
            }
        }
    }
}
