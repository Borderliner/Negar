package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.TodosDao
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.ui.todos.TodoEntity
import meshki.studio.negarname.entities.UiState
import meshki.studio.negarname.ui.todos.InvalidTodoException
import meshki.studio.negarname.ui.util.handleTryCatch

class TodosRepository(
    private val todosDao: TodosDao,
) {
    fun addTodo(todoEntity: TodoEntity): Long {
        if (todoEntity.text.isBlank()) {
            throw InvalidTodoException("Todo title is needed")
        }
        return todosDao.insert(todoEntity)
    }

    fun getTodos(): List<TodoEntity> {
        return todosDao.getAll()
    }

    fun getTodoById(id: Long): TodoEntity {
        return todosDao.getById(id)
    }

    fun findTodos(query: String): List<TodoEntity> {
        return todosDao.find(query)
    }

    fun updateTodo(todoEntity: TodoEntity) {
        return todosDao.update(todoEntity)
    }

    fun deleteTodo(todoEntity: TodoEntity) {
        return todosDao.delete(todoEntity)
    }

    fun pinTodo(todoEntity: TodoEntity) {
        return todosDao.update(todoEntity.copy(pinned = true))
    }

    fun unpinTodo(todoEntity: TodoEntity) {
        return todosDao.update(todoEntity.copy(pinned = false))
    }

    fun togglePinTodo(todoEntity: TodoEntity) {
        return todosDao.update(todoEntity.copy(pinned = !todoEntity.pinned))
    }

    fun checkTodo(todoEntity: TodoEntity) {
        return todosDao.update(todoEntity.copy(isCompleted = true))
    }

    fun uncheckTodo(todoEntity: TodoEntity) {
        return todosDao.update(todoEntity.copy(isCompleted = false))
    }

    fun toggleCheckTodo(todoEntity: TodoEntity) {
        return todosDao.update(todoEntity.copy(isCompleted = !todoEntity.isCompleted))
    }

    fun getTodosOrdered(orderBy: OrderBy): List<TodoEntity> {
        val todoEntities: List<TodoEntity> = todosDao.getAll()
        return if (orderBy.orderType == OrderType.Ascending) {
            when (orderBy.getType()) {
                is OrderBy.Title -> todoEntities.sortedBy { it.text.lowercase() }
                is OrderBy.Date -> todoEntities.sortedBy { it.dateModified }
                is OrderBy.Color -> todoEntities.sortedBy { it.color }
                is OrderBy.Completed -> todoEntities.sortedBy { it.isCompleted }
            }
        } else {
            when (orderBy.getType()) {
                is OrderBy.Title -> todoEntities.sortedByDescending { it.text.lowercase() }
                is OrderBy.Date -> todoEntities.sortedByDescending { it.dateModified }
                is OrderBy.Color -> todoEntities.sortedByDescending { it.color }
                is OrderBy.Completed -> todoEntities.sortedByDescending { it.isCompleted }
            }
        }
    }

    fun findTodosOrdered(query: String, orderBy: OrderBy): List<TodoEntity> {
        val todoEntities: List<TodoEntity> = todosDao.find(query)
        return if (orderBy.orderType == OrderType.Ascending) {
            when (orderBy.getType()) {
                is OrderBy.Title -> todoEntities.sortedBy { it.text.lowercase() }
                is OrderBy.Date -> todoEntities.sortedBy { it.dateModified }
                is OrderBy.Color -> todoEntities.sortedBy { it.color }
                is OrderBy.Completed -> todoEntities.sortedBy { it.isCompleted }
            }
        } else {
            when (orderBy.getType()) {
                is OrderBy.Title -> todoEntities.sortedByDescending { it.text.lowercase() }
                is OrderBy.Date -> todoEntities.sortedByDescending { it.dateModified }
                is OrderBy.Color -> todoEntities.sortedByDescending { it.color }
                is OrderBy.Completed -> todoEntities.sortedBy { it.isCompleted }
            }
        }
    }
}
