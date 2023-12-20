package meshki.studio.negarname.data.local.dao

import androidx.room.*
import meshki.studio.negarname.entity.Note

@Dao
interface NotesDao : BaseDao<Note> {

    @Query("SELECT * FROM Notes ORDER BY id DESC")
    fun getAll(): List<Note>

    @Query("SELECT * FROM Notes WHERE id = :id ORDER BY id DESC")
    fun getById(id: Long): Note

    @Query("SELECT * " +
            "FROM Notes " +
            "WHERE instr(LOWER(title), LOWER(:query)) > 0 OR instr(LOWER(text), LOWER(:query)) > 0 " +
            "ORDER BY id DESC")
    fun find(query: String): List<Note>
}
