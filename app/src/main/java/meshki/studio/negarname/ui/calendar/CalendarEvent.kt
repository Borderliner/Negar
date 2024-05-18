package meshki.studio.negarname.ui.calendar

// import kotlinx.datetime.LocalDate
import saman.zamani.persiandate.PersianDate

sealed class CalendarEvent {
    data class SetSelectedSolar(val solar: PersianDate) : CalendarEvent()
    data class SetDisplayedSolar(val solar: PersianDate) : CalendarEvent()
    data class SetSelectedSolarByValue(val year: Int, val month: Int, val day: Int) : CalendarEvent()
    data class SetDisplayedSolarByValue(val year: Int, val month: Int) : CalendarEvent()

    // data class SetGregorian(val localDate: LocalDate) : CalendarEvent()
    data class SetSelectedGregorianByValue(val year: Int, val month: Int, val day: Int) : CalendarEvent()
    data class SetDisplayedGregorianByValue(val year: Int, val month: Int) : CalendarEvent()

    data object ResetAll: CalendarEvent()
}
