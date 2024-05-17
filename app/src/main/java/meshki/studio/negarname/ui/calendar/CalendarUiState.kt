package meshki.studio.negarname.ui.calendar

import com.himanshoe.kalendar.ui.component.day.toLocalDate
import kotlinx.datetime.LocalDate
import saman.zamani.persiandate.PersianDate

data class CalendarUiState(
    val todaySolar: PersianDate = PersianDate(),
    val todayGreg: LocalDate = todaySolar.toLocalDate(),
    val selectedSolar: PersianDate = todaySolar
)
