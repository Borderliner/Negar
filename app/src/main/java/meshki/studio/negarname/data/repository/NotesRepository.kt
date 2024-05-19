package meshki.studio.negarname.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.AlarmsDao
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.services.alarm.AlarmEntity
import meshki.studio.negarname.ui.notes.entities.InvalidNoteException
import meshki.studio.negarname.ui.notes.entities.NoteEntity
import meshki.studio.negarname.ui.notes.entities.NotesAlarmsCrossRef
import kotlin.coroutines.CoroutineContext

class NotesRepository(
    private val notesDao: NotesDao,
    private val alarmsDao: AlarmsDao,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {
    suspend fun addNote(noteEntity: NoteEntity): Long {
        if (noteEntity.title.isBlank() && noteEntity.text.isBlank()) {
            throw InvalidNoteException("Note title is needed")
        }
        if (noteEntity.id < 0) {
            throw InvalidNoteException("Note id cannot be zero.")
        }

        return withContext(coroutineContext) {
            notesDao.insert(noteEntity)
        }
    }

    suspend fun getNotes(): Flow<List<NoteEntity>> {
        return withContext(coroutineContext) {
            notesDao.getAll()
        }
    }

    suspend fun getNoteById(id: Long): Flow<NoteEntity> {
        return withContext(coroutineContext) {
            notesDao.getById(id)
        }
    }

    suspend fun findNotes(query: String): Flow<List<NoteEntity>> {
        return withContext(coroutineContext) {
            notesDao.find(query)
        }
    }

    suspend fun updateNote(noteEntity: NoteEntity) {
        return withContext(coroutineContext) {
            notesDao.update(noteEntity)
        }
    }

    suspend fun deleteNote(noteEntity: NoteEntity) {
        return withContext(coroutineContext) {
            notesDao.delete(noteEntity)
        }
    }

    suspend fun pinNote(noteEntity: NoteEntity) {
        return withContext(coroutineContext) {
            notesDao.update(noteEntity.copy(pinned = true))
        }
    }

    suspend fun unpinNote(noteEntity: NoteEntity) {
        return withContext(coroutineContext) {
            notesDao.update(noteEntity.copy(pinned = false))
        }
    }

    suspend fun togglePinNote(noteEntity: NoteEntity) {
        return withContext(coroutineContext) {
            notesDao.update(noteEntity.copy(pinned = !noteEntity.pinned))
        }
    }

    suspend fun getNotesOrdered(orderBy: OrderBy): Flow<List<NoteEntity>> {
        return withContext(coroutineContext) {
            notesDao.getAll().map { notes ->
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
        }
    }

    suspend fun findNotesOrdered(query: String, orderBy: OrderBy): Flow<List<NoteEntity>> {
        return withContext(coroutineContext) {
            notesDao.find(query).map { notes ->
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
        }
    }

    suspend fun getNoteAlarms(noteEntity: NoteEntity): Flow<List<AlarmEntity>> {
        return withContext(coroutineContext) {
            getNoteAlarmsById(noteEntity.id)
        }
    }

    suspend fun getNoteAlarmsById(id: Long): Flow<List<AlarmEntity>> {
        return withContext(coroutineContext) {
            notesDao.getNoteAlarmsById(id).map {
                it.alarmEntities
            }
        }
    }

    suspend fun getAlarmById(id: Long): Flow<AlarmEntity> {
        return withContext(coroutineContext) {
            alarmsDao.getById(id)
        }
    }

    suspend fun addAlarm(noteId: Long, alarmEntity: AlarmEntity): Long {
        return withContext(coroutineContext) {
            val id = alarmsDao.insert(alarmEntity)
            notesDao.insertNoteAlarm(NotesAlarmsCrossRef(noteId, id))
            return@withContext id
        }
    }

    suspend fun deleteNoteAlarms(noteId: Long) {
        return withContext(coroutineContext) {
            notesDao.getNoteAlarmsById(noteId).collectLatest {
                alarmsDao.delete(it.alarmEntities)
                notesDao.deleteNoteAlarmsRef(noteId)
            }
        }
    }
}
