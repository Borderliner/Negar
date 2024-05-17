package meshki.studio.negarname.ui.navigation

import androidx.annotation.DrawableRes
import meshki.studio.negarname.R

sealed class ScreenEntity(var route: String, var title: String, @DrawableRes var icon: Int) {
    object Calendar: ScreenEntity("calendar", R.string.calendar.toString(), R.drawable.vec_calendar)
    object Notes: ScreenEntity("notes", R.string.notes.toString(),    R.drawable.vec_note_stack)
    object Todos: ScreenEntity("todos", R.string.todos.toString(),    R.drawable.vec_checklist)
    object Settings: ScreenEntity("settings", R.string.settings.toString(), R.drawable.vec_tune)
    object EditNotes: ScreenEntity("edit_notes", R.string.edit_notes.toString(), R.drawable.vec_edit_note)
    object EditTodos: ScreenEntity("edit_todos", R.string.edit_todos.toString(), R.drawable.vec_edit_todo)
}
