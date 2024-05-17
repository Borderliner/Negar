package meshki.studio.negarname.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import meshki.studio.negarname.ui.notes.NoteEntity
import meshki.studio.negarname.ui.notes.NoteAndAlarm
import meshki.studio.negarname.ui.notes.NotesAlarmsCrossRef

@Dao
interface NotesDao : BaseDao<NoteEntity> {
    @Query("SELECT * FROM notes ORDER BY note_id DESC")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE note_id = :id ORDER BY note_id DESC")
    fun getById(id: Long): Flow<NoteEntity>

    @Query("SELECT * " +
            "FROM notes " +
            "WHERE instr(LOWER(title), LOWER(:query)) > 0 OR instr(LOWER(text), LOWER(:query)) > 0 " +
            "ORDER BY note_id DESC")
    fun find(query: String): Flow<List<NoteEntity>>

    @Transaction
    @Query("SELECT * FROM notes")
    fun getNoteAlarms(): Flow<List<NoteAndAlarm>>

    @Transaction
    @Query("SELECT * FROM notes WHERE note_id = :noteId")
    fun getNoteAlarmsById(noteId: Long): Flow<NoteAndAlarm>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNoteAlarm(join: NotesAlarmsCrossRef)

    @Query("DELETE FROM notes_alarms_ref WHERE note_id = :noteId")
    fun deleteNoteAlarmsRef(noteId: Long): Int
}
