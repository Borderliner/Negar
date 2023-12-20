package meshki.studio.negarname.util

import meshki.studio.negarname.entity.UiStates
import java.lang.Exception

inline fun <T> handleTryCatch(task: () -> UiStates<T>): UiStates<T> {
    return try {
        task.invoke()
    } catch (ex: Exception) {
        UiStates.Error(message = ex.message)
    }
}