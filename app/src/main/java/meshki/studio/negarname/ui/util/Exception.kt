package meshki.studio.negarname.ui.util

import kotlinx.coroutines.withContext
import meshki.studio.negarname.entities.UiState
import timber.log.Timber
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

inline fun <T> handleTryCatch(task: () -> T): T? {
    return try {
        task.invoke()
    } catch (ex: Exception) {
        Timber.tag("Action Handler").e(ex)
        null
    }
}

suspend inline fun <T> handleTryCatchWithContext(context: CoroutineContext, crossinline task: () -> T): T? {
    return withContext(context) {
        return@withContext try {
            task.invoke()
        } catch (ex: Exception) {
            Timber.tag("Action Handler w/ Context").e(ex)
            null
        }
    }
}
