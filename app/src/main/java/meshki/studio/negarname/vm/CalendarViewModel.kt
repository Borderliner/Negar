package meshki.studio.negarname.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.himanshoe.kalendar.ui.component.day.toLocalDate
import com.himanshoe.kalendar.ui.firey.toPersianDate
import kotlinx.datetime.LocalDate
import meshki.studio.negarname.util.Zodiac
import saman.zamani.persiandate.PersianDate
import java.util.Locale

class CalendarViewModel: ViewModel() {
    val todaySolar = PersianDate.today()
    val todayGreg = todaySolar.toLocalDate()

    private val _selectedSolar = mutableStateOf(todaySolar)
    val selectedSolar: State<PersianDate> = _selectedSolar

    val zodiacNameGreg = derivedStateOf { Zodiac.calculateZodiac(
        selectedSolar.value.grgMonth,
        selectedSolar.value.grgDay
    ).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }}

    val zodiacNameSolar = derivedStateOf { Zodiac.calculateZodiacPersian(
        selectedSolar.value.grgMonth,
        selectedSolar.value.grgDay,
    )}

    val zodiacEmoji = derivedStateOf { Zodiac.zodiacToEmoji(
        Zodiac.calculateZodiac(
            selectedSolar.value.grgMonth,
            selectedSolar.value.grgDay,
        )
    )}

    val chineseZodiacNameGreg = derivedStateOf { Zodiac.calculateChineseYear(selectedSolar.value.grgYear)
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }}

    val chineseZodiacNameSolar = derivedStateOf { Zodiac.calculateChineseYearPersian(selectedSolar.value.grgYear) }

    val chineseZodiacEmoji = derivedStateOf { Zodiac.chineseYearToEmoji(selectedSolar.value.grgYear) }

    fun setSolar(date: PersianDate) {
        _selectedSolar.value = date
    }
    fun setSolarByValue(year: Int, month: Int, day: Int) {
        _selectedSolar.value = PersianDate().initJalaliDate(year, month, day)
    }

    fun setGreg(date: LocalDate) {
        _selectedSolar.value = date.toPersianDate()
    }
    fun setGregByValue(year: Int, month: Int, day: Int) {
        _selectedSolar.value = PersianDate().initGrgDate(year, month, day)
    }
}
