package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.TodosDao
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.ui.todos.TodoEntity
import meshki.studio.negarname.entities.UiStates
import meshki.studio.negarname.ui.util.handleTryCatch

interface TodosRepository {
    suspend fun addTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun updateTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun getTodos(): UiStates<List<TodoEntity>>
    suspend fun getTodosOrdered(orderBy: OrderBy = OrderBy.Date(OrderType.Descending)): UiStates<List<TodoEntity>>
    suspend fun getTodoById(id: Long): UiStates<TodoEntity>
    suspend fun deleteTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun findTodos(query: String): UiStates<List<TodoEntity>>
    suspend fun findTodosOrdered(
        query: String,
        orderBy: OrderBy = OrderBy.Date(OrderType.Descending)
    ): UiStates<List<TodoEntity>>

    suspend fun pinTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun unpinTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun togglePinTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun checkTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun uncheckTodo(todoEntity: TodoEntity): UiStates<Boolean>
    suspend fun toggleCheckTodo(todoEntity: TodoEntity): UiStates<Boolean>
}

class TodosRepoImpl(
    private val todosDao: TodosDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TodosRepository {
    override suspend fun addTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(dispatcher) {
            if (todoEntity.text.isBlank()) {
                return@withContext UiStates.Error("Todo title is needed")
            }

            handleTryCatch {
                todosDao.insert(todoEntity)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun getTodos(): UiStates<List<TodoEntity>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = todosDao.getAll())
            }
        }
    }

    override suspend fun getTodoById(id: Long): UiStates<TodoEntity> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = todosDao.getById(id))
            }
        }
    }

    override suspend fun findTodos(query: String): UiStates<List<TodoEntity>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = todosDao.find(query))
            }
        }
    }

    override suspend fun updateTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todoEntity)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun deleteTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.delete(todoEntity)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun pinTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(pinned = true))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun unpinTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(pinned = false))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun togglePinTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(pinned = !todoEntity.pinned))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun checkTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(isCompleted = true))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun uncheckTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(isCompleted = false))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun toggleCheckTodo(todoEntity: TodoEntity): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(isCompleted = !todoEntity.isCompleted))
                UiStates.Success(true)
            }
        }
    }


    override suspend fun getTodosOrdered(orderBy: OrderBy): UiStates<List<TodoEntity>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                val todoEntities: List<TodoEntity> = todosDao.getAll()
                if (orderBy.orderType == OrderType.Ascending) {
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
                UiStates.Success(todoEntities)
            }
        }
    }

    override suspend fun findTodosOrdered(
        query: String,
        orderBy: OrderBy,
    ): UiStates<List<TodoEntity>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                val todoEntities: List<TodoEntity> = todosDao.find(query)
                if (orderBy.orderType == OrderType.Ascending) {
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
                UiStates.Success(todoEntities)
            }
        }
    }
}
