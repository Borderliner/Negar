package meshki.studio.negarname.ui.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import meshki.studio.negarname.entities.UiState
import meshki.studio.negarname.ui.util.Zodiac
import saman.zamani.persiandate.PersianDate
import java.util.Locale

class CalendarViewModel: ViewModel() {
    private val _dataState = MutableStateFlow(CalendarState())
    val dataState: StateFlow<CalendarState> = _dataState.asStateFlow()

    private val _zodiacState = MutableStateFlow(calculateZodiac())
    val zodiacState: StateFlow<ZodiacState> = _zodiacState.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val uiState: StateFlow<UiState<Boolean>> = _uiState.asStateFlow()

    init {
        // true means nothing
        _uiState.value = UiState.Success(true)
    }

    fun onEvent(event: CalendarEvent) {
        if (uiState.value is UiState.Success) {
            when (event) {
                is CalendarEvent.SetSelectedSolar -> {
                    _dataState.update {
                        it.copy(selectedSolar = event.solar)
                    }

                    _zodiacState.update {
                        calculateZodiac()
                    }
                }

                is CalendarEvent.SetDisplayedSolar -> {
                    _dataState.update {
                        it.copy(displayedSolar = event.solar)
                    }
                }

                is CalendarEvent.SetSelectedSolarByValue -> {
                    _dataState.update {
                        it.copy(selectedSolar = PersianDate().initJalaliDate(event.year, event.month, event.day))
                    }

                    _zodiacState.update {
                        calculateZodiac()
                    }
                }

                is CalendarEvent.SetDisplayedSolarByValue -> {
                    _dataState.update {
                        it.copy(displayedSolar = PersianDate().initJalaliDate(event.year, event.month, 1))
                    }
                }

                is CalendarEvent.SetSelectedGregorianByValue -> {
                    _dataState.update {
                        it.copy(selectedSolar = PersianDate().initGrgDate(event.year, event.month, event.day))
                    }

                    _zodiacState.update {
                        calculateZodiac()
                    }
                }

                is CalendarEvent.SetDisplayedGregorianByValue -> {
                    _dataState.update {
                        it.copy(displayedSolar = PersianDate().initGrgDate(event.year, event.month, 1))
                    }
                }

                is CalendarEvent.ResetAll -> {
                    _dataState.update { CalendarState() }
                }
            }
        }
    }

    private fun calculateZodiac(): ZodiacState {
        return ZodiacState(
            zodiacGreg = Zodiac.calculateZodiac(
                dataState.value.selectedSolar.grgMonth,
                dataState.value.selectedSolar.grgDay
            ).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            },
            zodiacSolar = Zodiac.calculateZodiacPersian(
                dataState.value.selectedSolar.grgMonth,
                dataState.value.selectedSolar.grgDay
            ),
            zodiacEmoji = Zodiac.zodiacToEmoji(
                Zodiac.calculateZodiac(
                    dataState.value.selectedSolar.grgMonth,
                    dataState.value.selectedSolar.grgDay
                )
            ),
            chineseZodiacGreg = Zodiac.calculateChineseYear(dataState.value.selectedSolar.grgYear)
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                },
            chineseZodiacSolar = Zodiac.calculateChineseYearPersian(dataState.value.selectedSolar.grgYear),
            chineseZodiacEmoji = Zodiac.chineseYearToEmoji(dataState.value.selectedSolar.grgYear)
        )
    }
}
