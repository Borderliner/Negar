package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.NotesDao
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.OrderBy
import meshki.studio.negarname.entity.OrderType
import meshki.studio.negarname.entity.UiStates
import meshki.studio.negarname.util.handleTryCatch

interface NotesRepository {
    suspend fun addNote(note: Note): UiStates<Boolean>
    suspend fun updateNote(note: Note): UiStates<Boolean>
    suspend fun getNotes(): UiStates<List<Note>>
    suspend fun getNotesOrdered(orderBy: OrderBy = OrderBy.Date(OrderType.Descending)): UiStates<List<Note>>
    suspend fun getNoteById(id: Long): UiStates<Note>
    suspend fun deleteNote(note: Note): UiStates<Boolean>
    suspend fun findNotes(query: String): UiStates<List<Note>>
    suspend fun findNotesOrdered(
        query: String,
        orderBy: OrderBy = OrderBy.Date(OrderType.Descending)
    ): UiStates<List<Note>>

    suspend fun pinNote(note: Note): UiStates<Boolean>
    suspend fun unpinNote(note: Note): UiStates<Boolean>
    suspend fun togglePinNote(note: Note): UiStates<Boolean>
}

class NotesRepoImpl(
    private val notesDao: NotesDao,
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

    override suspend fun getNotes(): UiStates<List<Note>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.getAll())
            }
        }
    }

    override suspend fun getNoteById(id: Long): UiStates<Note> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = notesDao.getById(id))
            }
        }
    }

    override suspend fun findNotes(query: String): UiStates<List<Note>> {
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

    override suspend fun getNotesOrdered(orderBy: OrderBy): UiStates<List<Note>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                val notes: List<Note> = notesDao.getAll()
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
                UiStates.Success(notes)
            }
        }
    }

    override suspend fun findNotesOrdered(
        query: String,
        orderBy: OrderBy,
    ): UiStates<List<Note>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                val notes: List<Note> = notesDao.find(query)
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
                UiStates.Success(notes)
            }
        }
    }
}
