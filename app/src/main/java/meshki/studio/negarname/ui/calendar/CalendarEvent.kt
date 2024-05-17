package meshki.studio.negarname.ui.calendar

import kotlinx.datetime.LocalDate
import saman.zamani.persiandate.PersianDate

sealed class CalendarEvent {
    data class SetSolar(val solar: PersianDate) : CalendarEvent()
    data class SetSolarByValue(val year: Int, val month: Int, val day: Int) : CalendarEvent()
    data class SetGregorian(val localDate: LocalDate) : CalendarEvent()
    data class SetGregorianByValue(val year: Int, val month: Int, val day: Int) : CalendarEvent()
}
