package meshki.studio.negarname.ui.screen

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarType
import kotlinx.datetime.LocalDate
import java.util.Calendar

@Composable
fun CalendarScreen(navigateTo: (route: String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val cal = Calendar.getInstance()
            Kalendar(
                currentDay = LocalDate(
                    year = cal.get(Calendar.YEAR),
                    monthNumber = cal.get(Calendar.MONTH) + 1,
                    dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
                ), kalendarType = KalendarType.Firey
            )
        } else {
            Text(text = "Your Android version is too low.")
        }
    }
}
