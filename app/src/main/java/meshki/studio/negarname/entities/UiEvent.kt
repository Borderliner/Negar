package meshki.studio.negarname.entities

sealed class UiEvent{
    data class ShowSnackBar(val message: String): UiEvent()
    data object NoteSaved: UiEvent()
    data object TodoSaved: UiEvent()
}
