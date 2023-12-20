package meshki.studio.negarname.data.local.dao

import androidx.room.*
import meshki.studio.negarname.entity.Todo

@Dao
interface TodosDao : BaseDao<Todo> {

    @Query("SELECT * FROM Todos ORDER BY id DESC")
    fun getAll(): List<Todo>

    @Query("SELECT * FROM Todos WHERE id = :id ORDER BY id DESC")
    fun getById(id: Long): Todo

    @Query("SELECT * " +
            "FROM Todos " +
            "WHERE instr(LOWER(text), LOWER(:query)) > 0 " +
            "ORDER BY id DESC")
    fun find(query: String): List<Todo>
}
