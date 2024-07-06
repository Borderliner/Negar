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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.color.KalendarColorsShamsi
import com.himanshoe.kalendar.ui.component.header.KalendarHeader
import com.himanshoe.kalendar.ui.component.header.KalendarHeaderShamsi
import com.himanshoe.kalendar.ui.component.header.KalendarTextKonfig
import com.himanshoe.kalendar.ui.firey.DaySelectionMode
import com.himanshoe.kalendar.ui.firey.KalendarFirey
import com.himanshoe.kalendar.ui.firey.KalendarFireyShamsi
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import kotlinx.coroutines.launch
import meshki.studio.negarname.ui.app.AppState
import meshki.studio.negarname.R
import meshki.studio.negarname.entities.Tool
import meshki.studio.negarname.ui.components.PopupSection
import meshki.studio.negarname.ui.components.Toolbox
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.app.AppViewModel
import meshki.studio.negarname.ui.util.extensions.grgMonthToPersian
import org.koin.androidx.compose.koinViewModel
import saman.zamani.persiandate.PersianDate
import timber.log.Timber

@Composable
fun CalendarScreen() {
    val appViewModel = koinViewModel<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
    val vm = koinViewModel<CalendarViewModel>()
    val scope = rememberCoroutineScope()
    val goToDateTool = remember { mutableStateOf(Tool("goto")) }

    val dataState = vm.dataState.collectAsState()
    val zodiacState = vm.zodiacState.collectAsState()
    val uiState = vm.uiState.collectAsState()

    val headerMonthIndexSolar by remember { derivedStateOf { dataState.value.displayedSolar.shMonth.mod(12) } }
    val headerMonthIndexGreg by remember { derivedStateOf { dataState.value.displayedSolar.grgMonth.mod(12) } }

    // For better coloring purposes
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
        if (appState.value.isRtl) {
            KalendarFireyShamsi(
                currentDay = dataState.value.selectedSolar,
                displayedYear = dataState.value.displayedSolar.shYear,
                displayedMonth = dataState.value.displayedSolar.shMonth,
                kalendarColors = KalendarColorsShamsi.transparent(),
                daySelectionMode = DaySelectionMode.Single,
                onDayClick = { date, _ ->
                    scope.launch {
                        closeTool(goToDateTool)
                        vm.onEvent(CalendarEvent.SetSelectedSolar(date))
                    }
                },
                headerContent = { month, year ->
                    KalendarHeaderShamsi(
                        kalendarTextKonfig = KalendarTextKonfig(
                            kalendarTextColor = KalendarColorsShamsi.default().color[headerMonthIndexSolar].headerTextColor,
                            kalendarTextSize = 24.sp
                        ),
                        month = headerMonthSolar,
                        year = dataState.value.displayedSolar.shYear,
                        onPreviousClick = {
                            scope.launch {
                                closeTool(goToDateTool)

                                if (dataState.value.displayedSolar.shMonth == 1) {
                                    vm.onEvent(CalendarEvent.SetDisplayedSolarByValue(
                                        dataState.value.displayedSolar.shYear - 1,
                                        12
                                    ))
                                } else {
                                    vm.onEvent(CalendarEvent.SetDisplayedSolarByValue(
                                        dataState.value.displayedSolar.shYear,
                                        dataState.value.displayedSolar.shMonth - 1
                                    ))
                                }
                            }
                        },
                        onNextClick = {
                            scope.launch {
                                closeTool(goToDateTool)

                                if (dataState.value.displayedSolar.shMonth == 12) {
                                    vm.onEvent(CalendarEvent.SetDisplayedSolarByValue(
                                        dataState.value.displayedSolar.shYear + 1,
                                        1
                                    ))
                                } else {
                                    vm.onEvent(CalendarEvent.SetDisplayedSolarByValue(
                                        dataState.value.displayedSolar.shYear,
                                        dataState.value.displayedSolar.shMonth + 1
                                    ))
                                }
                            }
                        },
                        onDayReset = {
                            scope.launch {
                                closeTool(goToDateTool)
                                vm.onEvent(CalendarEvent.ResetAll)
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
                }
            )
        } else {
            KalendarFirey(
                currentDay = dataState.value.selectedSolar,
                displayedYear = dataState.value.displayedSolar.grgYear,
                displayedMonth = dataState.value.displayedSolar.grgMonth,
                daySelectionMode = DaySelectionMode.Single,
                kalendarColors = KalendarColors.transparent(),
                onDayClick = { date, _ ->
                    scope.launch {
                        closeTool(goToDateTool)
                        vm.onEvent(CalendarEvent.SetSelectedSolar(date))
                        Timber.tag("Calendar").i(date.toString())
                    }

                },
                headerContent = { _, _ ->
                    KalendarHeader(
                        kalendarTextKonfig = KalendarTextKonfig(
                            kalendarTextColor = KalendarColors.default().color[headerMonthIndexGreg].headerTextColor,
                            kalendarTextSize = 24.sp
                        ),
                        month = headerMonthGreg,
                        year = dataState.value.displayedSolar.grgYear,
                        onPreviousClick = {
                            scope.launch {
                                closeTool(goToDateTool)

                                if (dataState.value.displayedSolar.grgMonth == 1) {
                                    vm.onEvent(CalendarEvent.SetDisplayedGregorianByValue(
                                        dataState.value.displayedSolar.grgYear - 1,
                                        12
                                    ))
                                } else {
                                    vm.onEvent(CalendarEvent.SetDisplayedGregorianByValue(
                                        dataState.value.displayedSolar.grgYear,
                                        dataState.value.displayedSolar.grgMonth - 1
                                    ))
                                }
                            }
                        },
                        onNextClick = {
                            scope.launch {
                                closeTool(goToDateTool)

                                if (dataState.value.displayedSolar.grgMonth == 12) {
                                    vm.onEvent(CalendarEvent.SetDisplayedGregorianByValue(
                                        dataState.value.displayedSolar.grgYear + 1,
                                        1
                                    ))
                                } else {
                                    vm.onEvent(CalendarEvent.SetDisplayedGregorianByValue(
                                        dataState.value.displayedSolar.grgYear,
                                        dataState.value.displayedSolar.grgMonth + 1
                                    ))
                                }
                            }
                        },
                        onDayReset = {
                            scope.launch {
                                closeTool(goToDateTool)
                                vm.onEvent(CalendarEvent.ResetAll)
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
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .weight(1f, false),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "✝\uFE0F ")
                    Text(text = stringResource(R.string.gregorian))
                    Text(text = ": ${dataState.value.selectedSolar.grgDay} / ")
                    if (appState.value.isRtl) {
                        Text(text = "${grgMonthToPersian(dataState.value.selectedSolar.grgMonthName)} / ")
                    } else {
                        Text(text = "${dataState.value.selectedSolar.grgMonthName} / ")
                    }
                    Text(text = "${dataState.value.selectedSolar.grgYear}")
                }
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "☀\uFE0F ")
                    Text(text = stringResource(R.string.solar))
                    Text(text = ": ${dataState.value.selectedSolar.shDay} / ")
                    if (appState.value.isRtl) {
                        Text(text = "${dataState.value.selectedSolar.monthName} / ")
                    } else {
                        Text(text = "${dataState.value.selectedSolar.FinglishMonthName()} / ")
                    }
                    Text(text = "${dataState.value.selectedSolar.shYear}")
                }
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "\uD83D\uDD2E ")
                    Text(text = stringResource(R.string.zodiac))

                    if (appState.value.isRtl) {
                        Text(
                            text = ": ${zodiacState.value.zodiacSolar} "
                        )
                    } else {
                        Text(text = ": ${zodiacState.value.zodiacGreg} "
                        )
                    }
                    Text(
                        text = zodiacState.value.zodiacEmoji
                    )
                }
                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "\uD83C\uDC04 ")
                    Text(text = stringResource(R.string.chinese_year))
                    if (appState.value.isRtl) {
                        Text(text = ": ${zodiacState.value.chineseZodiacSolar} ")
                    } else {
                        Text(text = ": ${zodiacState.value.chineseZodiacGreg} "
                        )
                    }
                    Text(text = zodiacState.value.chineseZodiacEmoji)
                }

                Spacer(modifier = Modifier.height(spacing))
                Row {
                    Text(text = "\uD83D\uDCDC ")
                    val annotatedString = buildAnnotatedString {
                        if (appState.value.isRtl) {
                            pushStringAnnotation(tag = "URL", annotation = "https://www.time.ir/fa/event/list/0/${dataState.value.selectedSolar.shYear}/${dataState.value.selectedSolar.shMonth}")
                        } else {
                            pushStringAnnotation(tag = "URL", annotation = "https://www.onthisday.com/events/${dataState.value.selectedSolar.grgMonthName}/${dataState.value.selectedSolar.grgDay}")
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(stringResource(R.string.on_this_day))
                        }
                        pop()
                    }
                    val uriHandler = LocalUriHandler.current
                    ClickableText(text = annotatedString, style = MaterialTheme.typography.bodyLarge, onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.let {
                            uriHandler.openUri(it.item)
                        }
                    })
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
            if (appState.value.isRtl) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val yearWheelState = rememberFWheelPickerState(120)
                    val startYear = dataState.value.todaySolar.shYear - 120
                    val monthWheelState = rememberFWheelPickerState(dataState.value.todaySolar.shMonth - 1)
                    var dayCount by remember { mutableIntStateOf(dataState.value.todaySolar.monthDays) }
                    val dayWheelState = rememberFWheelPickerState(dataState.value.todaySolar.shDay - 1)

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

                                vm.onEvent(CalendarEvent.SetSelectedSolarByValue(
                                    yearWheelState.currentIndex + startYear,
                                    monthWheelState.currentIndex + 1,
                                    dayWheelState.currentIndex + 1
                                ))

                                vm.onEvent((CalendarEvent.SetDisplayedSolarByValue(
                                    yearWheelState.currentIndex + startYear,
                                    monthWheelState.currentIndex + 1
                                )))
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
                    val startYear = dataState.value.todaySolar.grgYear - 120
                    val monthWheelState = rememberFWheelPickerState(dataState.value.todaySolar.grgMonth - 1)
                    var dayCount by remember { mutableIntStateOf(dataState.value.todaySolar.grgMonthLength) }
                    val dayWheelState = rememberFWheelPickerState(dataState.value.todaySolar.grgDay - 1)

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

                                vm.onEvent(CalendarEvent.SetSelectedGregorianByValue(
                                    yearWheelState.currentIndex + startYear,
                                    monthWheelState.currentIndex + 1,
                                    dayWheelState.currentIndex + 1
                                ))

                                vm.onEvent((CalendarEvent.SetDisplayedGregorianByValue(
                                    yearWheelState.currentIndex + startYear,
                                    monthWheelState.currentIndex + 1
                                )))
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
