package meshki.studio.negarname.ui.screen

import android.os.Build
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarShamsi
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.color.KalendarColorsShamsi
import com.himanshoe.kalendar.ui.component.day.toLocalDate
import com.himanshoe.kalendar.ui.firey.toPersianDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import meshki.studio.negarname.R
import meshki.studio.negarname.vm.MainViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun CalendarScreen(navigateTo: (route: String) -> Unit) {
    val mainViewModel = koinViewModel<MainViewModel>()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentDateShamsi = remember {
            mutableStateOf(
                Clock.System.todayIn(TimeZone.currentSystemDefault()).toPersianDate()
            )
        }
        val currentDate = remember {
            mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mainViewModel.isRtl) {
                KalendarShamsi(
                    kalendarColors = KalendarColorsShamsi.transparent(),
                    currentDay = currentDateShamsi.value,
                    onDayClick = { date, _ ->
                        currentDateShamsi.value = date
                        currentDate.value = date.toLocalDate()
                    },
                )
            } else {
                Kalendar(currentDay = currentDate.value, kalendarType = KalendarType.Firey, onDayClick = { date, _ ->
                    currentDate.value = date
                    currentDateShamsi.value = date.toPersianDate()
                })
            }
        } else {
            Text(text = "Your Android version is too low.")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .size(8.dp)
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(text = stringResource(R.string.gregorian))
                    Text(text = ": ${currentDateShamsi.value.grgDay} / ")
                    Text(text = "${currentDateShamsi.value.grgMonthName} / ")
                    Text(text = "${currentDateShamsi.value.grgYear}")
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(text = stringResource(R.string.jalali))
                    Text(text = ": ${currentDateShamsi.value.shDay} / ")
                    Text(text = "${currentDateShamsi.value.monthName} / ")
                    Text(text = "${currentDateShamsi.value.shYear}")
                }
            }
        }
    }
}
