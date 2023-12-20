package meshki.studio.negar

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.navigation.compose.rememberNavController
import meshki.studio.negar.ui.element.BottomBar
import meshki.studio.negar.ui.element.TopBar
import meshki.studio.negar.ui.theme.NegarTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel by viewModel()
            val navController = rememberNavController()
            NegarTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Card(
                        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(
                                top = 15.dp,
                                bottom = 0.dp,
                                start = 0.dp,
                                end = 0.dp
                            ),
                        elevation = CardDefaults.elevatedCardElevation(20.dp),
                        shape = RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        ),

                        ) {
                        RightToLeftLayout {
                            Scaffold(
                                topBar = { TopBar() },
                                bottomBar = { BottomBar(navController) },
                                containerColor = Color.Transparent,
                                modifier = Modifier.navigationBarsPadding()
                            ) {
                                Box(
                                    Modifier.padding(it)
                                ) {
                                    Divider(color = Color.Gray.copy(0.4f), thickness = 1.dp)
                                    Navigation(navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
