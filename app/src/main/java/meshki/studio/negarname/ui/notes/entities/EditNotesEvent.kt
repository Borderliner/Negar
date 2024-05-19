package meshki.studio.negarname.ui.notes.entities

import androidx.compose.ui.focus.FocusState
import meshki.studio.negarname.services.alarm.AlarmData

sealed class EditNotesEvent {
    data class TitleEntered(val value: String) : EditNotesEvent()
    data class TextEntered(val value: String) : EditNotesEvent()
    data class TitleFocusChanged(val focusState: FocusState) : EditNotesEvent()
    data class TextFocusChanged(val focusState: FocusState) : EditNotesEvent()
    data class ColorChanged(val color: Int) : EditNotesEvent()
    data object NoteSaved : EditNotesEvent()
    data class DrawingSaved(val serializedPaths: String) : EditNotesEvent()
    data object DrawingRemoved : EditNotesEvent()
    data object VoiceRemoved : EditNotesEvent()
    data class SetAlarm(val alarmData: AlarmData) : EditNotesEvent()
    data class DeleteNoteAlarms(val noteId: Long) : EditNotesEvent()
}
