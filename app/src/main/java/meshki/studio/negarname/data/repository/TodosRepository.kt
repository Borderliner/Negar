package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.TodosDao
import meshki.studio.negarname.entities.OrderBy
import meshki.studio.negarname.entities.OrderType
import meshki.studio.negarname.ui.todos.TodoEntity
import meshki.studio.negarname.entities.UiState
import meshki.studio.negarname.ui.util.handleTryCatch

class TodosRepository(
    private val todosDao: TodosDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun addTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            if (todoEntity.text.isBlank()) {
                return@withContext UiState.Error("Todo title is needed")
            }

            handleTryCatch {
                todosDao.insert(todoEntity)
                UiState.Success(true)
            }
        }
    }

    suspend fun getTodos(): UiState<List<TodoEntity>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(data = todosDao.getAll())
            }
        }
    }

    suspend fun getTodoById(id: Long): UiState<TodoEntity> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(data = todosDao.getById(id))
            }
        }
    }

    suspend fun findTodos(query: String): UiState<List<TodoEntity>> {
        return withContext(dispatcher) {
            handleTryCatch {
                UiState.Success(data = todosDao.find(query))
            }
        }
    }

    suspend fun updateTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.update(todoEntity)
                UiState.Success(true)
            }
        }
    }

    suspend fun deleteTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.delete(todoEntity)
                UiState.Success(true)
            }
        }
    }

    suspend fun pinTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(pinned = true))
                UiState.Success(true)
            }
        }
    }

    suspend fun unpinTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(pinned = false))
                UiState.Success(true)
            }
        }
    }

    suspend fun togglePinTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(pinned = !todoEntity.pinned))
                UiState.Success(true)
            }
        }
    }

    suspend fun checkTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(isCompleted = true))
                UiState.Success(true)
            }
        }
    }

    suspend fun uncheckTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(isCompleted = false))
                UiState.Success(true)
            }
        }
    }

    suspend fun toggleCheckTodo(todoEntity: TodoEntity): UiState<Boolean> {
        return withContext(dispatcher) {
            handleTryCatch {
                todosDao.update(todoEntity.copy(isCompleted = !todoEntity.isCompleted))
                UiState.Success(true)
            }
        }
    }

    suspend fun getTodosOrdered(orderBy: OrderBy): UiState<List<TodoEntity>> {
        return withContext(dispatcher) {
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
                UiState.Success(todoEntities)
            }
        }
    }

    suspend fun findTodosOrdered(
        query: String,
        orderBy: OrderBy,
    ): UiState<List<TodoEntity>> {
        return withContext(dispatcher) {
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
                UiState.Success(todoEntities)
            }
        }
    }
}
