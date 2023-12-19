package meshki.studio.negar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import meshki.studio.negar.ui.theme.NegarTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val mainViewModel: MainViewModel by viewModel()
            NegarTheme {
                Scaffold(
                    topBar = {  },
                    bottomBar = {  },
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Card(


                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp),
                            elevation = CardDefaults.elevatedCardElevation()
                        ) {
                            Text(text = mainViewModel.isReady.value.toString())
                        }
                    }
                }
            }
        }
    }
}
