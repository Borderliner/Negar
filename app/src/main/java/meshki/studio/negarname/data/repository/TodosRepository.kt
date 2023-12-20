package meshki.studio.negarname.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.local.dao.TodosDao
import meshki.studio.negarname.entity.OrderBy
import meshki.studio.negarname.entity.OrderType
import meshki.studio.negarname.entity.Todo
import meshki.studio.negarname.entity.UiStates
import meshki.studio.negarname.util.handleTryCatch

interface TodosRepository {
    suspend fun addTodo(todo: Todo): UiStates<Boolean>
    suspend fun updateTodo(todo: Todo): UiStates<Boolean>
    suspend fun getTodos(): UiStates<List<Todo>>
    suspend fun getTodosOrdered(orderBy: OrderBy = OrderBy.Date(OrderType.Descending)): UiStates<List<Todo>>
    suspend fun getTodoById(id: Long): UiStates<Todo>
    suspend fun deleteTodo(todo: Todo): UiStates<Boolean>
    suspend fun findTodos(query: String): UiStates<List<Todo>>
    suspend fun findTodosOrdered(
        query: String,
        orderBy: OrderBy = OrderBy.Date(OrderType.Descending)
    ): UiStates<List<Todo>>

    suspend fun pinTodo(todo: Todo): UiStates<Boolean>
    suspend fun unpinTodo(todo: Todo): UiStates<Boolean>
    suspend fun togglePinTodo(todo: Todo): UiStates<Boolean>
    suspend fun checkTodo(todo: Todo): UiStates<Boolean>
    suspend fun uncheckTodo(todo: Todo): UiStates<Boolean>
    suspend fun toggleCheckTodo(todo: Todo): UiStates<Boolean>
}

class TodosRepoImpl(
    private val todosDao: TodosDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TodosRepository {
    override suspend fun addTodo(todo: Todo): UiStates<Boolean> {
        return withContext(dispatcher) {
            if (todo.text.isBlank()) {
                return@withContext UiStates.Error("Todo title is needed")
            }

            handleTryCatch {
                todosDao.insert(todo)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun getTodos(): UiStates<List<Todo>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = todosDao.getAll())
            }
        }
    }

    override suspend fun getTodoById(id: Long): UiStates<Todo> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = todosDao.getById(id))
            }
        }
    }

    override suspend fun findTodos(query: String): UiStates<List<Todo>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                UiStates.Success(data = todosDao.find(query))
            }
        }
    }

    override suspend fun updateTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todo)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun deleteTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.delete(todo)
                UiStates.Success(true)
            }
        }
    }

    override suspend fun pinTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todo.copy(pinned = true))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun unpinTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todo.copy(pinned = false))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun togglePinTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todo.copy(pinned = !todo.pinned))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun checkTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todo.copy(isCompleted = true))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun uncheckTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todo.copy(isCompleted = false))
                UiStates.Success(true)
            }
        }
    }

    override suspend fun toggleCheckTodo(todo: Todo): UiStates<Boolean> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                todosDao.update(todo.copy(isCompleted = !todo.isCompleted))
                UiStates.Success(true)
            }
        }
    }


    override suspend fun getTodosOrdered(orderBy: OrderBy): UiStates<List<Todo>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                val todos: List<Todo> = todosDao.getAll()
                if (orderBy.orderType == OrderType.Ascending) {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> todos.sortedBy { it.text.lowercase() }
                        is OrderBy.Date -> todos.sortedBy { it.dateModified }
                        is OrderBy.Color -> todos.sortedBy { it.color }
                        is OrderBy.Completed -> todos.sortedBy { it.isCompleted }
                    }
                } else {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> todos.sortedByDescending { it.text.lowercase() }
                        is OrderBy.Date -> todos.sortedByDescending { it.dateModified }
                        is OrderBy.Color -> todos.sortedByDescending { it.color }
                        is OrderBy.Completed -> todos.sortedByDescending { it.isCompleted }
                    }
                }
                UiStates.Success(todos)
            }
        }
    }

    override suspend fun findTodosOrdered(
        query: String,
        orderBy: OrderBy,
    ): UiStates<List<Todo>> {
        return withContext(Dispatchers.IO) {
            handleTryCatch {
                val todos: List<Todo> = todosDao.find(query)
                if (orderBy.orderType == OrderType.Ascending) {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> todos.sortedBy { it.text.lowercase() }
                        is OrderBy.Date -> todos.sortedBy { it.dateModified }
                        is OrderBy.Color -> todos.sortedBy { it.color }
                        is OrderBy.Completed -> todos.sortedBy { it.isCompleted }
                    }
                } else {
                    when (orderBy.getType()) {
                        is OrderBy.Title -> todos.sortedByDescending { it.text.lowercase() }
                        is OrderBy.Date -> todos.sortedByDescending { it.dateModified }
                        is OrderBy.Color -> todos.sortedByDescending { it.color }
                        is OrderBy.Completed -> todos.sortedBy { it.isCompleted }
                    }
                }
                UiStates.Success(todos)
            }
        }
    }
}
