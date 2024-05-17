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
import meshki.studio.negarname.entities.UiStates
import meshki.studio.negarname.ui.util.handleTryCatch

interface NotesRepository {
    suspend fun addNote(noteEntity: NoteEntity): UiStates<Boolean>
    suspend fun updateNote(noteEntity: NoteEntity): UiStates<Boolean>
    suspend fun getNotes(): UiStates<Flow<List<NoteEntity>>>
    suspend fun getNotesOrdered(orderBy: OrderBy = OrderBy.Date(OrderType.Descending)): UiStates<Flow<List<NoteEntity>>>
    suspend fun getNoteById(id: Long): UiStates<Flow<NoteEntity>>
    suspend fun deleteNote(noteEntity: NoteEntity): UiStates<Boolean>
    suspend fun findNotes(query: String): UiStates<Flow<List<NoteEntity>>>
    suspend fun findNotesOrdered(
        query: String,
        orderBy: OrderBy = OrderBy.Date(OrderType.Descending)
    ): UiStates<Flow<List<NoteEntity>>>

    suspend fun pinNote(noteEntity: NoteEntity): UiStates<Boolean>
    suspend fun unpinNote(noteEntity: NoteEntity): UiStates<Boolean>
    suspend fun togglePinNote(noteEntity: NoteEntity): UiStates<Boolean>

    suspend fun getNoteAlarms(noteEntity: NoteEntity): UiStates<Flow<List<AlarmEntity>>>
    suspend fun getNoteAlarmsById(id: Long): UiStates<Flow<List<AlarmEntity>>>
    suspend fun getAlarmById(id: Long): UiStates<Flow<AlarmEntity>>
    suspend fun addAlarm(noteId: Long, alarmEntity: AlarmEntity): UiStates<Long>
    suspend fun deleteNoteAlarms(noteId: Long): UiStates<Boolean>
}

class NotesRepoImpl(
    private val notesDao: NotesDao,
    private val alarmsDao: AlarmsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotesRepository {
    override suspend fun addNote(noteEntity: NoteEntity): UiStates<Boolean> {
        return withContext(dispatcher) {
            if (noteEntity.title.isBlank() && noteEntity.text.isBlank()) {
                return@withContext UiStates.Error("Note title is needed")
            }
            if (noteEntity.id < 0) {
                return@withContext UiStates.Error("Note id cannot be zero.")
            }
            handleTryCatch {
                notesDao.insert(noteEntity)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun getNotes(): UiStates<Flow<List<NoteEntity>>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.getAll())
            }
        }
    }

    override suspend fun getNoteById(id: Long): UiStates<Flow<NoteEntity>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.getById(id))
            }
        }
    }

    override suspend fun findNotes(query: String): UiStates<Flow<List<NoteEntity>>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.find(query))
            }
        }
    }

    override suspend fun updateNote(noteEntity: NoteEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(noteEntity)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun deleteNote(noteEntity: NoteEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.delete(noteEntity)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun pinNote(noteEntity: NoteEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(noteEntity.copy(pinned = true))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun unpinNote(noteEntity: NoteEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(noteEntity.copy(pinned = false))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun togglePinNote(noteEntity: NoteEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(noteEntity.copy(pinned = !noteEntity.pinned))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun getNotesOrdered(orderBy: OrderBy): UiStates<Flow<List<NoteEntity>>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
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
                UiStates.Success(notes)
            }
        }
    }

    override suspend fun findNotesOrdered(
        query: String,
        orderBy: OrderBy,
    ): UiStates<Flow<List<NoteEntity>>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
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

                UiStates.Success(notes)
            }
        }
    }

    override suspend fun getNoteAlarms(noteEntity: NoteEntity): UiStates<Flow<List<AlarmEntity>>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(getNoteAlarmsById(noteEntity.id).data)
            }
        }
    }

    override suspend fun getNoteAlarmsById(id: Long): UiStates<Flow<List<AlarmEntity>>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(notesDao.getNoteAlarmsById(id).map {
                    it.alarmEntities
                })
            }
        }
    }

    override suspend fun getAlarmById(id: Long): UiStates<Flow<AlarmEntity>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(alarmsDao.getById(id))
            }
        }
    }

    override suspend fun addAlarm(noteId: Long, alarmEntity: AlarmEntity): UiStates<Long> {
        return withContext(dispatcher) {
            handleTryCatch {
                val id = alarmsDao.insert(alarmEntity)
                notesDao.insertNoteAlarm(
                    NotesAlarmsCrossRef(
                    noteId,
                    id
                )
                )
                UiStates.Success(id)
            }
        }
    }

    override suspend fun deleteNoteAlarms(noteId: Long): UiStates<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                notesDao.getNoteAlarmsById(noteId).collectLatest {
                    alarmsDao.delete(it.alarmEntities)
                    notesDao.deleteNoteAlarmsRef(noteId)
                }
                UiStates.Success(true)
            }
        }
    }
}
