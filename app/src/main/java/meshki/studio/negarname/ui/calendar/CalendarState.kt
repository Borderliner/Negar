package meshki.studio.negarname.ui.calendar

import saman.zamani.persiandate.PersianDate

data class CalendarState(
    val todaySolar: PersianDate = PersianDate(),
    val displayedSolar: PersianDate = PersianDate(),
    val selectedSolar: PersianDate = PersianDate()
)
