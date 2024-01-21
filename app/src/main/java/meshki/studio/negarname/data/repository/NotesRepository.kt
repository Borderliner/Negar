package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.AlarmsDao
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.entity.Alarm
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.NoteAndAlarm
import meshki.studio.negarname.entity.NotesAlarmsCrossRef
import meshki.studio.negarname.entity.OrderBy
import meshki.studio.negarname.entity.OrderType
import meshki.studio.negarname.entity.UiStates
import meshki.studio.negarname.util.handleTryCatch
import timber.log.Timber

interface NotesRepository {
    suspend fun addNote(note: Note): UiStates<Boolean>
    suspend fun updateNote(note: Note): UiStates<Boolean>
    suspend fun getNotes(): UiStates<Flow<List<Note>>>
    suspend fun getNotesOrdered(orderBy: OrderBy = OrderBy.Date(OrderType.Descending)): UiStates<Flow<List<Note>>>
    suspend fun getNoteById(id: Long): UiStates<Flow<Note>>
    suspend fun deleteNote(note: Note): UiStates<Boolean>
    suspend fun findNotes(query: String): UiStates<Flow<List<Note>>>
    suspend fun findNotesOrdered(
        query: String,
        orderBy: OrderBy = OrderBy.Date(OrderType.Descending)
    ): UiStates<Flow<List<Note>>>

    suspend fun pinNote(note: Note): UiStates<Boolean>
    suspend fun unpinNote(note: Note): UiStates<Boolean>
    suspend fun togglePinNote(note: Note): UiStates<Boolean>

    suspend fun getNoteAlarms(note: Note): UiStates<Flow<List<Alarm>>>
    suspend fun getNoteAlarmsById(id: Long): UiStates<Flow<List<Alarm>>>
    suspend fun getAlarmById(id: Long): UiStates<Flow<Alarm>>
    suspend fun addAlarm(noteId: Long, alarm: Alarm): UiStates<Long>
    suspend fun deleteNoteAlarms(noteId: Long): UiStates<Boolean>
}

class NotesRepoImpl(
    private val notesDao: NotesDao,
    private val alarmsDao: AlarmsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotesRepository {
    override suspend fun addNote(note: Note): UiStates<Boolean> {
        return withContext(dispatcher) {
            if (note.title.isBlank() && note.text.isBlank()) {
                return@withContext UiStates.Error("Note title is needed")
            }
            if (note.id < 0) {
                return@withContext UiStates.Error("Note id cannot be zero.")
            }
            handleTryCatch {
                notesDao.insert(note)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun getNotes(): UiStates<Flow<List<Note>>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.getAll())
            }
        }
    }

    override suspend fun getNoteById(id: Long): UiStates<Flow<Note>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.getById(id))
            }
        }
    }

    override suspend fun findNotes(query: String): UiStates<Flow<List<Note>>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.find(query))
            }
        }
    }

    override suspend fun updateNote(note: Note): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(note)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun deleteNote(note: Note): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.delete(note)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun pinNote(note: Note): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(note.copy(pinned = true))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun unpinNote(note: Note): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(note.copy(pinned = false))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun togglePinNote(note: Note): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                notesDao.update(note.copy(pinned = !note.pinned))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun getNotesOrdered(orderBy: OrderBy): UiStates<Flow<List<Note>>> {
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
    ): UiStates<Flow<List<Note>>> {
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

    override suspend fun getNoteAlarms(note: Note): UiStates<Flow<List<Alarm>>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(getNoteAlarmsById(note.id).data)
            }
        }
    }

    override suspend fun getNoteAlarmsById(id: Long): UiStates<Flow<List<Alarm>>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(notesDao.getNoteAlarmsById(id).map {
                    it.alarms
                })
            }
        }
    }

    override suspend fun getAlarmById(id: Long): UiStates<Flow<Alarm>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiStates.Success(alarmsDao.getById(id))
            }
        }
    }

    override suspend fun addAlarm(noteId: Long, alarm: Alarm): UiStates<Long> {
        return withContext(dispatcher) {
            handleTryCatch {
                val id = alarmsDao.insert(alarm)
                notesDao.insertNoteAlarm(NotesAlarmsCrossRef(
                    noteId,
                    id
                ))
                UiStates.Success(id)
            }
        }
    }

    override suspend fun deleteNoteAlarms(noteId: Long): UiStates<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                notesDao.getNoteAlarmsById(noteId).collectLatest {
                    alarmsDao.delete(it.alarms)
                    notesDao.deleteNoteAlarmsRef(noteId)
                }
                UiStates.Success(true)
            }
        }
    }
}
