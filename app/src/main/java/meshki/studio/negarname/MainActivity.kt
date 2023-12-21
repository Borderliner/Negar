package meshki.studio.negarname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import meshki.studio.negarname.ui.element.BottomBar
import meshki.studio.negarname.ui.element.TopBar
import meshki.studio.negarname.ui.theme.NegarTheme
import meshki.studio.negarname.util.LeftToRightLayout
import meshki.studio.negarname.util.RightToLeftLayout
import meshki.studio.negarname.vm.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel: MainViewModel by viewModel()
        enableEdgeToEdge()

        setContent {
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
                        if (mainViewModel.isRtl) {
                            RightToLeftLayout {
                                MainScreenScaffold(navController, mainViewModel)
                            }
                        } else {
                            LeftToRightLayout {
                                MainScreenScaffold(navController, mainViewModel)
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun MainScreenScaffold(navController: NavHostController, mainViewModel: MainViewModel) {
    Scaffold(
        topBar = { TopBar(mainViewModel) },
        bottomBar = { BottomBar(navController) },
        containerColor = Color.Transparent,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Box(
            Modifier.padding(it)
        ) {
            Divider(color = Color.Gray.copy(0.4f), thickness = 1.dp)
            if (mainViewModel.isReady) {
                Navigation(navController, mainViewModel)
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Loading...")
                }
            }
        }
    }
}