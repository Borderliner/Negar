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

package com.himanshoe.kalendar.ui.component.day

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.himanshoe.kalendar.KalendarEvent
import com.himanshoe.kalendar.KalendarEvents
import com.himanshoe.kalendar.color.KalendarColor
import com.himanshoe.kalendar.color.KalendarColorShamsi
import com.himanshoe.kalendar.ui.component.day.modifier.FULL_ALPHA
import com.himanshoe.kalendar.ui.component.day.modifier.TOWNED_DOWN_ALPHA
import com.himanshoe.kalendar.ui.component.day.modifier.circleLayout
import com.himanshoe.kalendar.ui.component.day.modifier.dayBackgroundColor
import com.himanshoe.kalendar.ui.component.indicator.KalendarIndicator
import com.himanshoe.kalendar.ui.firey.KalendarSelectedDayRange
import com.himanshoe.kalendar.ui.firey.KalendarSelectedDayRangeShamsi
import com.himanshoe.kalendar.util.MultiplePreviews
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import saman.zamani.persiandate.PersianDate
import java.time.format.DateTimeFormatter

/**
 * A composable representing a single day in the Kalendar.
 *
 * @param date The date corresponding to the day.
 * @param kalendarColors The colors used for styling the Kalendar.
 * @param onDayClick The callback function invoked when the day is clicked.
 * @param selectedRange The selected date range in the Kalendar.
 * @param modifier The modifier to be applied to the composable.
 * @param selectedDate The currently selected date.
 * @param kalendarEvents The events associated with the Kalendar.
 * @param kalendarDayKonfig The configuration for the Kalendar day.
 */
@Composable
fun KalendarDayShamsi(
    date: PersianDate,
    kalendarColors: KalendarColorShamsi,
    onDayClick: (PersianDate, List<KalendarEvent>) -> Unit,
    selectedRange: KalendarSelectedDayRangeShamsi?,
    modifier: Modifier = Modifier,
    selectedDate: PersianDate = date,
    kalendarEvents: KalendarEvents = KalendarEvents(),
    kalendarDayKonfig: KalendarDayKonfig = KalendarDayKonfig.default(),
) {
    val selected = selectedDate.isEqualTo(date)
    val today = PersianDate()
    val isToday = date.isEqualTo(today)

    Column(
        modifier = modifier
            .border(
                border = getBorder(isToday, MaterialTheme.colorScheme.onPrimaryContainer, selected),
                shape = CircleShape
            )
            .clip(shape = CircleShape)
            .clickable { onDayClick(date, kalendarEvents.events) }
            .dayBackgroundColorShamsi(
                selected,
                listOf(
                    MaterialTheme.colorScheme.inversePrimary,
                    MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.7f),
                    MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.4f),
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                ),
                kalendarColors.dayBackgroundColor,
                date,
                selectedRange,
            )
            .circleLayout()
            .wrapContentSize()
            .defaultMinSize(kalendarDayKonfig.size),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = date.shDay.toString(),
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            fontSize = kalendarDayKonfig.textSize,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
        )
        Row {
            kalendarEvents.events
                .filter { it.date.isPersianDateEqual(date) }
                .take(3)
                .fastForEachIndexed { index, _ ->
                    Row {
                        KalendarIndicator(
                            modifier = Modifier,
                            index = index,
                            size = kalendarDayKonfig.size,
                            color = kalendarColors.headerTextColor
                        )
                    }
                }
        }
    }
}

@MultiplePreviews
@Composable
private fun KalendarDayShamsiPreview() {
    val date = PersianDate()
    val previous = date.subDay()
    val events = (0..5).map {
        KalendarEvent(
            date = date.toLocalDate(),
            eventName = it.toString(),
        )
    }
    Row {
        KalendarDayShamsi(
            date = date,
            kalendarColors = KalendarColorShamsi.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            kalendarEvents = KalendarEvents(events),
            selectedRange = null
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        KalendarDayShamsi(
            date = date.addDay(),
            kalendarColors = KalendarColorShamsi.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            kalendarEvents = KalendarEvents(events),
            selectedRange = null
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        KalendarDayShamsi(
            date = date,
            kalendarColors = KalendarColorShamsi.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = date,
            kalendarEvents = KalendarEvents(events),
            selectedRange = null
        )
    }
}

fun Modifier.dayBackgroundColorShamsi(
    selected: Boolean,
    selectedColors: List<Color>,
    color: Color,
    date: PersianDate,
    selectedRange: KalendarSelectedDayRangeShamsi?
): Modifier {
    val inRange = if (selectedRange != null) {
        date.after(selectedRange.start) && date.before(selectedRange.end)
    } else false

    val backgroundColor = when {
        selectedRange != null && date.after(selectedRange.start) && date.before(selectedRange.end) -> {
            val alpha = if (inRange) FULL_ALPHA else TOWNED_DOWN_ALPHA
            color.copy(alpha = alpha)
        }

        else -> Color.Transparent
    }

    val brush = if (selected) {
        Brush.linearGradient(
            selectedColors,
            tileMode = TileMode.Repeated
        )
    } else {
        null
    }

    return if (brush == null) {
        this.then(
            background(backgroundColor)
        )
    } else {
        this.then(
            background(brush)
        )
    }
}

fun LocalDate.isPersianDateEqual(shamsi: PersianDate): Boolean {
    return this.year == shamsi.grgYear && this.monthNumber == shamsi.grgMonth && this.dayOfMonth == shamsi.grgDay
}

fun PersianDate.toLocalDate(): LocalDate {
    val month = if (this.grgMonth > 9) this.grgMonth else "0" + this.grgMonth
    val day = if (this.grgDay > 9) this.grgDay else "0" + this.grgDay
    return LocalDate.parse("${this.grgYear}-$month-$day")
}

fun PersianDate.isEqualTo(other: PersianDate): Boolean {
    return this.shYear == other.shYear
            && this.shMonth == other.shMonth
            && this.shDay == other.shDay
}
