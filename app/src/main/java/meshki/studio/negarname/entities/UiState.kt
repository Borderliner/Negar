package meshki.studio.negarname.entities

sealed class UiState<T>(
    val data: T? = null,
    val errorMessage: String? = null,
    val loading: Boolean = false
) {
    class None<T>() : UiState<T>()
    class Loading<T>(loading: Boolean = true) : UiState<T>(loading = loading)
    class Success<T>(data: T?) : UiState<T>(data = data, loading = false)
    class Error<T>(errorMessage: String?) : UiState<T>(errorMessage = errorMessage, loading = false)
}
