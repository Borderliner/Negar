package meshki.studio.negar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import meshki.studio.negar.ui.theme.NegarTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel by viewModel()
            NegarTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Scaffold(
                        topBar = {  },
                        bottomBar = {  },
                        containerColor = Color.Transparent
                    ) { padding ->
                            Card(
                                colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = padding.calculateTopPadding() + 40.dp,
                                        bottom = padding.calculateBottomPadding(),
                                        start = 0.dp,
                                        end = 0.dp
                                    ),
                                elevation = CardDefaults.elevatedCardElevation(20.dp),
                                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomEnd = 0.dp, bottomStart = 0.dp),

                            ) {

                                Navigation()
                            }
                        }
                    }
            }
        }
    }
}