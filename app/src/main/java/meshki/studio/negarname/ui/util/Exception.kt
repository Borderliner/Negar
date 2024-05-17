package meshki.studio.negarname.ui.util

import kotlinx.coroutines.withContext
import meshki.studio.negarname.entities.UiState
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

inline fun <T> handleTryCatch(task: () -> UiState<T>): UiState<T> {
    return try {
        task.invoke()
    } catch (ex: Exception) {
        UiState.Error(errorMessage = ex.message)
    }
}

suspend inline fun <T> handleTryCatchWithContext(context: CoroutineContext, crossinline task: () -> UiState<T>): UiState<T> {
    return withContext(context) {
        return@withContext try {
            task.invoke()
        } catch (ex: Exception) {
            UiState.Error(errorMessage = ex.message)
        }
    }
}
