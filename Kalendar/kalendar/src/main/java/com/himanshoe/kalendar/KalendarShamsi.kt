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

package com.himanshoe.kalendar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.color.KalendarColorsShamsi
import com.himanshoe.kalendar.ui.component.day.KalendarDayKonfig
import com.himanshoe.kalendar.ui.component.header.KalendarTextKonfig
import com.himanshoe.kalendar.ui.firey.DaySelectionMode
import com.himanshoe.kalendar.ui.firey.KalendarFirey
import com.himanshoe.kalendar.ui.firey.KalendarFireyShamsi
import com.himanshoe.kalendar.ui.firey.KalendarSelectedDayRange
import com.himanshoe.kalendar.ui.firey.KalendarSelectedDayRangeShamsi
import com.himanshoe.kalendar.ui.firey.RangeSelectionError
import com.himanshoe.kalendar.ui.oceanic.KalendarOceanic
import kotlinx.datetime.LocalDate
import saman.zamani.persiandate.PersianDate
import java.time.Month

/**
 * Composable function that represents a calendar component.
 *
 * @param currentDay The current selected day in the calendar.
 * @param kalendarType The type of calendar to be displayed.
 * @param modifier The modifier for styling or positioning the calendar.
 * @param showLabel Determines whether to show the labels for days of the week.
 * @param kalendarHeaderTextKonfig The configuration for the header text in the calendar.
 * @param kalendarColors The colors used for styling the calendar.
 * @param kalendarDayKonfig The configuration for each day cell in the calendar.
 * @param dayContent The content to be displayed inside each day cell of the calendar.
 * @param daySelectionMode The mode for selecting days in the calendar.
 * @param headerContent The content to be displayed in the header of the calendar.
 * @param onDayClick Callback function triggered when a day cell is clicked.
 * @param onRangeSelected Callback function triggered when a range of days is selected.
 * @param onErrorRangeSelected Callback function triggered when an error occurs during range selection.
 */
@Composable
fun KalendarShamsi(
    currentDay: PersianDate?,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    kalendarHeaderTextKonfig: KalendarTextKonfig? = null,
    kalendarColors: KalendarColorsShamsi = KalendarColorsShamsi.default(),
    kalendarDayKonfig: KalendarDayKonfig = KalendarDayKonfig.default(),
    dayContent: (@Composable (PersianDate) -> Unit)? = null,
    daySelectionMode: DaySelectionMode = DaySelectionMode.Single,
    headerContent: (@Composable (Int, Int) -> Unit)? = null,
    onDayClick: (PersianDate, List<KalendarEvent>) -> Unit = { _, _ -> },
    onRangeSelected: (KalendarSelectedDayRangeShamsi, List<KalendarEvent>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {},
    onNextMonthClick: (Int) -> Unit = { },
    onPreviousMonthClick: (Int) -> Unit = { },
    onDayResetClick: () -> Unit = { }
) {
    KalendarFireyShamsi(
        currentDay = currentDay,
        modifier = modifier,
        showLabel = showLabel,
        kalendarHeaderTextKonfig = kalendarHeaderTextKonfig,
        kalendarColors = kalendarColors,
        events = KalendarEvents(),
        kalendarDayKonfig = kalendarDayKonfig,
        onDayClick = onDayClick,
        dayContent = dayContent,
        headerContent = headerContent,
        daySelectionMode = daySelectionMode,
        onRangeSelected = onRangeSelected,
        onErrorRangeSelected = onErrorRangeSelected,
        onNextMonthClick = onNextMonthClick,
        onPreviousMonthClick = onPreviousMonthClick,
        onDayResetClick = onDayResetClick
    )
}
