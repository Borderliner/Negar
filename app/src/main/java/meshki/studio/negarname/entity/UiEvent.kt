package meshki.studio.negarname.entity

sealed class UiEvent{
    data class showSnackBar(val message: String): UiEvent()
    object SavedNote: UiEvent()
    object SavedTodo: UiEvent()
}
