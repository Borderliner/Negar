package meshki.studio.negarname.ui.screen

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarShamsi
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.ui.firey.toPersianDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.Calendar

@Composable
fun CalendarScreen(navigateTo: (route: String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentDate = remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()).toPersianDate()) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KalendarShamsi(
                currentDay =  currentDate.value,
                onDayClick = { date, _ ->
                    currentDate.value = date
                }
            )
        } else {
            Text(text = "Your Android version is too low.")
        }
    }
}
