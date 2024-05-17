package meshki.studio.negarname.ui.calendar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarShamsi
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.color.KalendarColorsShamsi
import com.himanshoe.kalendar.ui.component.day.toLocalDate
import com.himanshoe.kalendar.ui.component.header.KalendarHeader
import com.himanshoe.kalendar.ui.component.header.KalendarHeaderShamsi
import com.himanshoe.kalendar.ui.component.header.KalendarTextKonfig
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import meshki.studio.negarname.ui.app.AppState
import meshki.studio.negarname.R
import meshki.studio.negarname.entities.Tool
import meshki.studio.negarname.ui.components.PopupSection
import meshki.studio.negarname.ui.components.Toolbox
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.app.AppViewModel
import org.koin.androidx.compose.koinViewModel
import saman.zamani.persiandate.PersianDate
import timber.log.Timber

@Composable
fun CalendarScreen(appState: AppState) {
    val appViewModel = koinViewModel<AppViewModel>()
    val vm = koinViewModel<CalendarViewModel>()
    val scope = rememberCoroutineScope()
    val goToDateTool = remember { mutableStateOf(Tool("goto")) }

    val uiState = vm.uiState.collectAsState()

    var displayedMonthSolar by remember { mutableIntStateOf(uiState.value.selectedSolar.shMonth) }
    var displayedYearSolar by remember { mutableIntStateOf(uiState.value.selectedSolar.shYear) }

    var displayedMonthGreg by remember { mutableIntStateOf(uiState.value.selectedSolar.grgMonth) }
    var displayedYearGreg by remember { mutableIntStateOf(uiState.value.selectedSolar.grgYear) }

    val headerMonthIndexSolar by remember { derivedStateOf { displayedMonthSolar.mod(12) } }
    val headerMonthIndexGreg by remember { derivedStateOf { displayedMonthGreg.mod(12) } }

    val headerMonthSolar by remember { derivedStateOf {
        if (headerMonthIndexSolar == 0)
            12
        else
            headerMonthIndexSolar
    }}
    val headerMonthGreg by remember { derivedStateOf {
        if (headerMonthIndexGreg == 0)
            12
        else
            headerMonthIndexGreg
    }}

    suspend fun openTool(tool: MutableState<Tool>, delay: Int = 0) {
        tool.value.visibility.value = true
        tool.value.animation.value.animateTo(
            tool.value.animation.value.upperBound ?: Float.MAX_VALUE,
            tween(320, delay, easing = FastOutSlowInEasing)
        )
    }

    suspend fun closeTool(tool: MutableState<Tool>) {
        tool.value.visibility.value = false
        // Animated hide current Tool
        tool.value.animation.value.snapTo(
            tool.value.animation.value.lowerBound ?: 0f,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (appViewModel.isRtl) {
            KalendarShamsi(
                headerContent = { month, year ->
                    KalendarHeaderShamsi(
                        kalendarTextKonfig = KalendarTextKonfig(
                            kalendarTextColor = KalendarColorsShamsi.default().color[headerMonthIndexSolar].headerTextColor,
                            kalendarTextSize = 24.sp
                        ),
                        month = headerMonthSolar,
                        year = displayedYearSolar,
                        onPreviousClick = {
                            scope.launch { closeTool(goToDateTool) }
                            if (displayedMonthSolar == 1) {
                                displayedYearSolar =- 1
                                displayedMonthSolar = 12
                            } else {
                                displayedMonthSolar -= 1
                            }
                        },
                        onNextClick = {
                            scope.launch { closeTool(goToDateTool) }
                            if (displayedMonthSolar == 12) {
                                displayedYearSolar += 1
                                displayedMonthSolar = 1
                            } else {
                                displayedMonthSolar += 1
                            }
                        },
                        onDayReset = {
                            scope.launch {
                                closeTool(goToDateTool)
                                vm.onEvent(CalendarEvent.SetSolar(uiState.value.todaySolar))
                                displayedMonthSolar = uiState.value.selectedSolar.shMonth
                                displayedYearSolar = uiState.value.selectedSolar.shYear
                            }
                        },
                        onGoToDay = {
                            scope.launch {
                                if (goToDateTool.value.visibility.value) {
                                    closeTool(goToDateTool)
                                } else {
                                    openTool(goToDateTool)
                                }
                            }
                        }
                    )
                },
                kalendarColors = KalendarColorsShamsi.transparent(),
                currentDay = uiState.value.selectedSolar,
                onDayClick = { date, _ ->
                    scope.launch {
                        closeTool(goToDateTool)
                        vm.onEvent(CalendarEvent.SetSolar(date))
                        Timber.tag("Calendar").i(date.toString())
                    }
                },
            )
        } else {
            Kalendar(
                headerContent = { month, year ->
                    KalendarHeader(
                        kalendarTextKonfig = KalendarTextKonfig(
                            kalendarTextColor = KalendarColors.default().color[headerMonthIndexGreg].headerTextColor,
                            kalendarTextSize = 24.sp
                        ),
                        month = Month(headerMonthGreg),
                        year = displayedYearGreg,
                        onPreviousClick = {
                            scope.launch { closeTool(goToDateTool) }
                            if (displayedMonthGreg == 1) {
                                displayedYearGreg =- 1
                                displayedMonthGreg = 12
                            } else {
                                displayedMonthGreg -= 1
                            }
                        },
                        onNextClick = {
                            scope.launch { closeTool(goToDateTool) }
                            if (displayedMonthGreg == 12) {
                                displayedYearGreg += 1
                                displayedMonthGreg = 1
                            } else {
                                displayedMonthGreg += 1
                            }
                        },
                        onDayReset = {
                            scope.launch {
                                closeTool(goToDateTool)
                                vm.onEvent(CalendarEvent.SetSolar(uiState.value.todaySolar))
                                displayedMonthGreg = uiState.value.selectedSolar.grgMonth
                                displayedYearGreg = uiState.value.selectedSolar.grgYear
                            }
                        },
                        onGoToDay = {
                            scope.launch {
                                if (goToDateTool.value.visibility.value) {
                                    closeTool(goToDateTool)
                                } else {
                                    openTool(goToDateTool)
                                }
                            }
                        }
                    )
                },
                kalendarColors = KalendarColors.transparent(),
                currentDay = uiState.value.selectedSolar.toLocalDate(),
                kalendarType = KalendarType.Firey,
                onDayClick = { date, _ ->
                    scope.launch {
                        closeTool(goToDateTool)
                        vm.onEvent(CalendarEvent.SetGregorian(date))
                        Timber.tag("Calendar").i(date.toString())
                    }

                },
                onDayResetClick = {
                    scope.launch {
                        vm.onEvent(CalendarEvent.SetGregorian(uiState.value.todayGreg))
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .size(4.dp)
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp)
                .padding(bottom = 125.dp, top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            val spacing = 6.dp
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 24.dp, y = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "✝\uFE0F ")
                    Text(text = stringResource(R.string.gregorian))
                    Text(text = ": ${uiState.value.selectedSolar.grgDay} / ")
                    Text(text = "${uiState.value.selectedSolar.grgMonthName} / ")
                    Text(text = "${uiState.value.selectedSolar.grgYear}")
                }
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "☀\uFE0F ")
                    Text(text = stringResource(R.string.solar))
                    Text(text = ": ${uiState.value.selectedSolar.shDay} / ")
                    if (appViewModel.isRtl) {
                        Text(text = "${uiState.value.selectedSolar.monthName} / ")
                    } else {
                        Text(text = "${uiState.value.selectedSolar.FinglishMonthName()} / ")
                    }
                    Text(text = "${uiState.value.selectedSolar.shYear}")
                }
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "\uD83D\uDD2E ")
                    Text(text = stringResource(R.string.zodiac))

                    if (appViewModel.isRtl) {
                        Text(
                            text = ": ${vm.zodiacNameSolar.value} "
                        )
                    } else {
                        Text(text = ": ${vm.zodiacNameGreg.value} "
                        )
                    }
                    Text(
                        text = vm.zodiacEmoji.value
                    )
                }
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "\uD83C\uDC04 ")
                    Text(text = stringResource(R.string.chinese_year))
                    if (appViewModel.isRtl) {
                        Text(text = ": ${vm.chineseZodiacNameSolar.value} ")
                    } else {
                        Text(text = ": ${vm.chineseZodiacNameGreg.value} "
                        )
                    }
                    Text(text = vm.chineseZodiacEmoji.value)
                }
            }
        }
    }

    Toolbox(
        goToDateTool.value.visibility,
        goToDateTool.value.animation
    ) {
        PopupSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 63.dp,
            offsetPercent = 0.23f,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            if (appViewModel.isRtl) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val yearWheelState = rememberFWheelPickerState(120)
                    val startYear = uiState.value.todaySolar.shYear - 120
                    val monthWheelState = rememberFWheelPickerState(uiState.value.todaySolar.shMonth - 1)
                    var dayCount by remember { mutableIntStateOf(uiState.value.todaySolar.monthDays) }
                    val dayWheelState = rememberFWheelPickerState(uiState.value.todaySolar.shDay - 1)

                    Text(
                        modifier = Modifier.padding(vertical = 6.dp),
                        text = stringResource(R.string.go_to_date),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FVerticalWheelPicker(
                            state = yearWheelState,
                            modifier = Modifier.width(75.dp),
                            // Specified item count.
                            count = 240,
                        ) { index ->
                            val year = startYear + index
                            Text(year.toString())
                        }

                        FVerticalWheelPicker(
                            state = monthWheelState,
                            modifier = Modifier.width(90.dp),
                            // Specified item count.
                            count = 12,
                        ) { index ->
                            val date = PersianDate().apply {
                                this.shMonth = index + 1
                            }
                            Text(date.monthName)
                        }

                        LaunchedEffect(monthWheelState.currentIndex) {
                            val date = PersianDate().apply {
                                this.shMonth = monthWheelState.currentIndex + 1
                            }
                            dayCount = date.monthDays
                        }
                        FVerticalWheelPicker(
                            state = dayWheelState,
                            modifier = Modifier.width(60.dp),
                            // Specified item count.
                            count = dayCount,
                        ) { index ->
                            Text(index.inc().toString())
                        }
                    }

                    ElevatedButton(
                        modifier = Modifier.padding(bottom = 8.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            PastelGreen,
                            Color.Black.copy(0.9f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = {
                            scope.launch {
                                closeTool(goToDateTool)
                                vm.onEvent(CalendarEvent.SetSolarByValue(
                                    yearWheelState.currentIndex + startYear,
                                    monthWheelState.currentIndex + 1,
                                    dayWheelState.currentIndex + 1
                                ))

                                vm.onEvent(CalendarEvent.SetGregorian(uiState.value.selectedSolar.toLocalDate()))

                                displayedYearSolar = uiState.value.selectedSolar.shYear
                                displayedMonthSolar = uiState.value.selectedSolar.shMonth
                            }
                        }) {
                        Icon(
                            painterResource(R.drawable.vec_done),
                            //modifier = Modifier.background(Color.Black),
                            contentDescription = stringResource(R.string.save),
                            tint = Color.Black.copy(0.9f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val yearWheelState = rememberFWheelPickerState(120)
                    val startYear = uiState.value.todaySolar.grgYear - 120
                    val monthWheelState = rememberFWheelPickerState(uiState.value.todaySolar.grgMonth - 1)
                    var dayCount by remember { mutableIntStateOf(uiState.value.todaySolar.grgMonthLength) }
                    val dayWheelState = rememberFWheelPickerState(uiState.value.todaySolar.grgDay - 1)

                    Text(
                        modifier = Modifier.padding(vertical = 6.dp),
                        text = stringResource(R.string.go_to_date),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FVerticalWheelPicker(
                            state = yearWheelState,
                            modifier = Modifier.width(75.dp),
                            // Specified item count.
                            count = 240,
                        ) { index ->
                            val year = startYear + index
                            Text(year.toString())
                        }

                        FVerticalWheelPicker(
                            state = monthWheelState,
                            modifier = Modifier.width(90.dp),
                            // Specified item count.
                            count = 12,
                        ) { index ->
                            val date = PersianDate().apply {
                                this.grgMonth = index + 1
                            }
                            Text(date.grgMonthName)
                        }

                        LaunchedEffect(monthWheelState.currentIndex) {
                            val date = PersianDate().apply {
                                this.grgMonth = monthWheelState.currentIndex + 1
                            }
                            dayCount = date.grgMonthLength
                        }
                        FVerticalWheelPicker(
                            state = dayWheelState,
                            modifier = Modifier.width(60.dp),
                            // Specified item count.
                            count = dayCount,
                        ) { index ->
                            Text(index.inc().toString())
                        }
                    }

                    ElevatedButton(
                        modifier = Modifier.padding(bottom = 8.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            PastelGreen,
                            Color.Black.copy(0.9f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = {
                            scope.launch {
                                closeTool(goToDateTool)
                                vm.onEvent(CalendarEvent.SetGregorianByValue(
                                    yearWheelState.currentIndex + startYear,
                                    monthWheelState.currentIndex + 1,
                                    dayWheelState.currentIndex + 1
                                ))

                                displayedYearSolar = uiState.value.selectedSolar.shYear
                                displayedMonthSolar = uiState.value.selectedSolar.shMonth

                                displayedYearGreg = uiState.value.selectedSolar.grgYear
                                displayedMonthGreg = uiState.value.selectedSolar.grgMonth
                            }
                        }) {
                        Icon(
                            painterResource(R.drawable.vec_done),
                            //modifier = Modifier.background(Color.Black),
                            contentDescription = stringResource(R.string.save),
                            tint = Color.Black.copy(0.9f)
                        )
                    }
                }
            }
        }
    }
}
