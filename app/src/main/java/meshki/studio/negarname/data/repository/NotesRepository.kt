package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import meshki.studio.negarname.data.local.dao.AlarmsDao
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.services.alarm.AlarmEntity
import meshki.studio.negarname.ui.notes.InvalidNoteException
import meshki.studio.negarname.ui.notes.NoteEntity
import meshki.studio.negarname.ui.notes.NotesAlarmsCrossRef

class NotesRepository(
    private val notesDao: NotesDao,
    private val alarmsDao: AlarmsDao,
) {
    fun addNote(noteEntity: NoteEntity): Long {
        if (noteEntity.title.isBlank() && noteEntity.text.isBlank()) {
            throw InvalidNoteException("Note title is needed")
        }
        if (noteEntity.id < 0) {
            throw InvalidNoteException("Note id cannot be zero.")
        }

        return notesDao.insert(noteEntity)
    }

    fun getNotes(): Flow<List<NoteEntity>> {
        return notesDao.getAll()
    }

    fun getNoteById(id: Long): Flow<NoteEntity> {
        return notesDao.getById(id)
    }

    fun findNotes(query: String): Flow<List<NoteEntity>> {
        return notesDao.find(query)
    }

    fun updateNote(noteEntity: NoteEntity) {
        return notesDao.update(noteEntity)
    }

    fun deleteNote(noteEntity: NoteEntity) {
        return notesDao.delete(noteEntity)
    }

    fun pinNote(noteEntity: NoteEntity) {
        return notesDao.update(noteEntity.copy(pinned = true))
    }

    fun unpinNote(noteEntity: NoteEntity) {
        return notesDao.update(noteEntity.copy(pinned = false))
    }

    fun togglePinNote(noteEntity: NoteEntity) {
        return notesDao.update(noteEntity.copy(pinned = !noteEntity.pinned))
    }

    fun getNotesOrdered(orderBy: OrderBy): Flow<List<NoteEntity>> {
        return notesDao.getAll().map { notes ->
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

    fun findNotesOrdered(query: String, orderBy: OrderBy): Flow<List<NoteEntity>> {
        return notesDao.find(query).map { notes ->
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

    fun getNoteAlarms(noteEntity: NoteEntity): Flow<List<AlarmEntity>> {
        return getNoteAlarmsById(noteEntity.id)
    }

    fun getNoteAlarmsById(id: Long): Flow<List<AlarmEntity>> {
        return notesDao.getNoteAlarmsById(id).map {
            it.alarmEntities
        }
    }

    fun getAlarmById(id: Long): Flow<AlarmEntity> {
        return alarmsDao.getById(id)
    }

    fun addAlarm(noteId: Long, alarmEntity: AlarmEntity): Long {
        val id = alarmsDao.insert(alarmEntity)
        notesDao.insertNoteAlarm(NotesAlarmsCrossRef(
            noteId,
            id
        ))
        return id
    }

    suspend fun deleteNoteAlarms(noteId: Long) {
        return notesDao.getNoteAlarmsById(noteId).collectLatest {
            alarmsDao.delete(it.alarmEntities)
            notesDao.deleteNoteAlarmsRef(noteId)
        }
    }
}
