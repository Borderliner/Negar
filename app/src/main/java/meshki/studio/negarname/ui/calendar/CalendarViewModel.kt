package meshki.studio.negarname.ui.calendar

import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import com.himanshoe.kalendar.ui.firey.toPersianDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import meshki.studio.negarname.ui.util.Zodiac
import saman.zamani.persiandate.PersianDate
import java.util.Locale

class CalendarViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    suspend fun onEvent(event: CalendarEvent) {
        when (event) {
            is CalendarEvent.SetSolar -> {
                _uiState.update {
                    it.copy(selectedSolar = event.solar)
                }
            }

            is CalendarEvent.SetSolarByValue -> {
                _uiState.update {
                    it.copy(selectedSolar = PersianDate().initJalaliDate(event.year, event.month, event.day))
                }
            }

            is CalendarEvent.SetGregorian -> {
                _uiState.update {
                    it.copy(selectedSolar = event.localDate.toPersianDate())
                }
            }

            is CalendarEvent.SetGregorianByValue -> {
                _uiState.update {
                    it.copy(selectedSolar = PersianDate().initGrgDate(event.year, event.month, event.day))
                }
            }
        }
    }

    // Zodiac
    val zodiacNameGreg = derivedStateOf { Zodiac.calculateZodiac(
        uiState.value.selectedSolar.grgMonth,
        uiState.value.selectedSolar.grgDay
    ).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }}

    val zodiacNameSolar = derivedStateOf { Zodiac.calculateZodiacPersian(
        uiState.value.selectedSolar.grgMonth,
        uiState.value.selectedSolar.grgDay
    )}

    val zodiacEmoji = derivedStateOf { Zodiac.zodiacToEmoji(
        Zodiac.calculateZodiac(
            uiState.value.selectedSolar.grgMonth,
            uiState.value.selectedSolar.grgDay
        )
    )}

    val chineseZodiacNameGreg = derivedStateOf { Zodiac.calculateChineseYear(uiState.value.selectedSolar.grgYear)
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }}

    val chineseZodiacNameSolar = derivedStateOf { Zodiac.calculateChineseYearPersian(uiState.value.selectedSolar.grgYear) }

    val chineseZodiacEmoji = derivedStateOf { Zodiac.chineseYearToEmoji(uiState.value.selectedSolar.grgYear) }
}
