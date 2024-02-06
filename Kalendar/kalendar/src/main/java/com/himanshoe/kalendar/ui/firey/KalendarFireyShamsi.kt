/*
 * Copyright 2023 Kalendar Contributors (https://www.himanshoe.com). All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.himanshoe.kalendar.ui.firey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.himanshoe.kalendar.KalendarEvent
import com.himanshoe.kalendar.KalendarEvents
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.color.KalendarColorsShamsi
import com.himanshoe.kalendar.ui.component.day.KalendarDay
import com.himanshoe.kalendar.ui.component.day.KalendarDayKonfig
import com.himanshoe.kalendar.ui.component.day.KalendarDayShamsi
import com.himanshoe.kalendar.ui.component.day.toLocalDate
import com.himanshoe.kalendar.ui.component.header.KalendarHeaderShamsi
import com.himanshoe.kalendar.ui.component.header.KalendarTextKonfig
import com.himanshoe.kalendar.ui.oceanic.util.getNext7Dates
import com.himanshoe.kalendar.ui.oceanic.util.isLeapYear
import com.himanshoe.kalendar.util.MultiplePreviews
import com.himanshoe.kalendar.util.onDayClicked
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.todayIn
import saman.zamani.persiandate.PersianDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale


/**
 * Internal composable function representing the Kalendar component.
 *
 * @param currentDay The current selected day in the Kalendar.
 * @param daySelectionMode The day selection mode in the Kalendar.
 * @param modifier The modifier for styling or positioning the Kalendar.
 * @param showLabel Determines whether to show labels in the Kalendar.
 * @param kalendarHeaderTextKonfig The configuration for the Kalendar header text.
 * @param kalendarColors The colors configuration for the Kalendar.
 * @param events The events associated with the Kalendar.
 * @param kalendarDayKonfig The configuration for each day in the Kalendar.
 * @param dayContent Custom content for rendering each day in the Kalendar.
 * @param headerContent Custom content for rendering the header of the Kalendar.
 * @param onDayClick Callback invoked when a day is clicked.
 * @param onRangeSelected Callback invoked when a range of days is selected.
 * @param onErrorRangeSelected Callback invoked when an error occurs during range selection.
 */
@Composable
internal fun KalendarFireyShamsi(
    currentDay: PersianDate?,
    daySelectionMode: DaySelectionMode,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    kalendarHeaderTextKonfig: KalendarTextKonfig? = null,
    kalendarColors: KalendarColorsShamsi = KalendarColorsShamsi.default(),
    events: KalendarEvents = KalendarEvents(),
    kalendarDayKonfig: KalendarDayKonfig = KalendarDayKonfig.default(),
    dayContent: (@Composable (PersianDate) -> Unit)? = null,
    headerContent: (@Composable (Int, Int) -> Unit)? = null,
    onDayClick: (PersianDate, List<KalendarEvent>) -> Unit = { _, _ -> },
    onRangeSelected: (KalendarSelectedDayRangeShamsi, List<KalendarEvent>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {},
    onNextMonthClick: (Int) -> Unit = { },
    onPreviousMonthClick: (Int) -> Unit = { }
) {
    val persianWeekDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    val todayShamsi = currentDay ?: PersianDate.today()

    val selectedRange = remember { mutableStateOf<KalendarSelectedDayRangeShamsi?>(null) }
    val selectedDateShamsi = remember { mutableStateOf(todayShamsi) }
    val displayedMonthShamsi = remember { mutableStateOf(todayShamsi.shMonth) }
    val displayedYearShamsi = remember { mutableStateOf(todayShamsi.shYear) }
    val currentMonthShamsi = if(displayedMonthShamsi.value.mod(12) == 0) 12 else displayedMonthShamsi.value.mod(12)
    val currentYearShamsi = displayedYearShamsi.value

    val currentMonthIndexShamsi = displayedMonthShamsi.value.mod(12)

    val defaultHeaderColor = KalendarTextKonfig.default(
        color = kalendarColors.color[currentMonthIndexShamsi].headerTextColor,
    )
    val newHeaderTextKonfig = kalendarHeaderTextKonfig ?: defaultHeaderColor

    val startDayOfMonth = PersianDate().initJalaliDate(currentYearShamsi, currentMonthShamsi, 1)
    val daysInMonth = startDayOfMonth.monthDays

    val daysIterator = remember { mutableStateOf((getInitialDayOfMonth(startDayOfMonth.dayOfWeek())..daysInMonth).toList()) }
    println(daysIterator)

    LaunchedEffect(displayedMonthShamsi.value, displayedYearShamsi.value) {
        daysIterator.value = (getInitialDayOfMonth(startDayOfMonth.dayOfWeek())..daysInMonth).toList()
    }

    Column(
        modifier = modifier
            .background(
                color = kalendarColors.color[currentMonthIndexShamsi].backgroundColor
            )
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(all = 8.dp)
    ) {
        if (headerContent != null) {
            headerContent(currentMonthShamsi, currentYearShamsi)
        } else {
            KalendarHeaderShamsi(
                month = currentMonthShamsi,
                year = currentYearShamsi,
                kalendarTextKonfig = newHeaderTextKonfig,
                onPreviousClick = {
                    displayedYearShamsi.value -= if (currentMonthShamsi == 1) 1 else 0
                    displayedMonthShamsi.value -= 1
                    onPreviousMonthClick(displayedMonthShamsi.value)
                },
                onNextClick = {
                    displayedYearShamsi.value += if (currentMonthShamsi == 12) 1 else 0
                    displayedMonthShamsi.value += 1
                    onNextMonthClick(displayedMonthShamsi.value)
                },
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Fixed(7),
            content = {
                if (showLabel) {
                    items(persianWeekDays) { item ->
                        Text(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = kalendarDayKonfig.textSize,
                            text = item,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                items(daysIterator.value) {
                    if (it in 1..daysInMonth) {
                        val day = PersianDate().initJalaliDate(currentYearShamsi, currentMonthShamsi, it)
                        if (dayContent != null) {
                            dayContent(day)
                        } else {
                            KalendarDayShamsi(
                                date = day,
                                selectedDate = selectedDateShamsi.value,
                                kalendarColors = kalendarColors.color[currentMonthIndexShamsi],
                                kalendarEvents = events,
                                kalendarDayKonfig = kalendarDayKonfig,
                                selectedRange = selectedRange.value,
                                onDayClick = { clickedDate, event ->
                                    onDayClickedShamsi(
                                        clickedDate,
                                        event,
                                        daySelectionMode,
                                        selectedRange,
                                        onRangeSelected = { range, events ->
                                            if (range.end < range.start) {
                                                onErrorRangeSelected(RangeSelectionError.EndIsBeforeStart)
                                            } else {
                                                onRangeSelected(range, events)
                                            }
                                        },
                                        onDayClick = { newDate, clickedDateEvent ->
                                            selectedDateShamsi.value = newDate
                                            onDayClick(newDate, clickedDateEvent)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}

/**
 * Calculates the offset to determine the first day of the month based on the provided first day of the month.
 *
 * @param firstDayOfMonth The first day of the month.
 * @return The offset value representing the first day of the month.
 */
private fun getFirstDayOfMonth(firstDayOfMonth: DayOfWeek) = -(firstDayOfMonth.value).minus(2)

/**
 * Calculates a LocalDate object based on the provided day, current month, and current year.
 *
 * @param day The day of the month.
 * @param currentMonth The current month.
 * @param currentYear The current year.
 * @return The LocalDate object representing the specified day, month, and year.
 */

@Composable
@MultiplePreviews
private fun KalendarFireyPreview() {
    KalendarFirey(
        currentDay = Clock.System.todayIn(
            TimeZone.currentSystemDefault()
        ),
        kalendarHeaderTextKonfig = KalendarTextKonfig.previewDefault(),
        daySelectionMode = DaySelectionMode.Range
    )
}

fun LocalDate.toPersianDate(): PersianDate {
    return PersianDate(this.atStartOfDayIn(TimeZone.currentSystemDefault()).toJavaInstant().toEpochMilli())
}

private fun getInitialDayOfMonth(firstDayOfMonth: Int) = -(firstDayOfMonth).minus(1)

@Immutable
data class KalendarSelectedDayRangeShamsi(
    val start: PersianDate,
    val end: PersianDate
) {
    /**
     * Checks if the selected day range is empty (start date is after end date).
     * @return True if the range is empty, false otherwise.
     */
    fun isEmpty() = start > end

    /**
     * Checks if the selected day range contains a single date (start and end dates are the same).
     * @return True if the range contains a single date, false otherwise.
     */
    fun isSingleDate() = start == end
}


internal fun onDayClickedShamsi(
    date: PersianDate,
    events: List<KalendarEvent>,
    daySelectionMode: DaySelectionMode,
    selectedRange: MutableState<KalendarSelectedDayRangeShamsi?>,
    onRangeSelected: (KalendarSelectedDayRangeShamsi, List<KalendarEvent>) -> Unit = { _, _ -> },
    onDayClick: (PersianDate, List<KalendarEvent>) -> Unit = { _, _ -> }
) {
    when (daySelectionMode) {
        DaySelectionMode.Single -> {
            onDayClick(date, events)
        }

        DaySelectionMode.Range -> {
            val range = selectedRange.value
            selectedRange.value = when {
                range?.isEmpty() != false -> KalendarSelectedDayRangeShamsi(start = date, end = date)
                range.isSingleDate() -> KalendarSelectedDayRangeShamsi(start = range.start, end = date)
                else -> KalendarSelectedDayRangeShamsi(start = date, end = date)
            }

            selectedRange.value?.let { rangeDates ->
                val selectedEvents = events
                    .filter {
                        val shamsi = it.date.toPersianDate()
                        shamsi.after(rangeDates.start) && shamsi.before(rangeDates.end)
                    }
                    .toList()

                onRangeSelected(rangeDates, selectedEvents)
            }
        }
    }
}
