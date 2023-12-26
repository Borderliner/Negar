package meshki.studio.negarname.entity

sealed class UiEvent{
    data class ShowSnackBar(val message: String): UiEvent()
    data object NoteSaved: UiEvent()
    data object TodoSaved: UiEvent()
}
