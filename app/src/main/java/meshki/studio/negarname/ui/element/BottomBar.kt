package meshki.studio.negarname.ui.element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import meshki.studio.negarname.entity.ScreenEntity

@Composable
fun BottomBar(navController: NavHostController) {
    val screenItems = listOf(
        ScreenEntity.Calendar,
        ScreenEntity.Notes,
        ScreenEntity.Todos,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .padding(horizontal = 16.dp)
            .navigationBarsPadding(),
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = Color.Transparent,
    ) {
        OutlinedCard(
            elevation = CardDefaults.elevatedCardElevation(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            border = BorderStroke(
                1.dp,
                Color.Gray
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                screenItems.forEach { screen ->
                    if (currentRoute != null) {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(id = screen.icon),
                                    contentDescription = screen.title + " Icon",
                                    //tint = MaterialTheme.colorScheme.primary0
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(screen.title.toInt()),
                                    //color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            alwaysShowLabel = true,
                            selected = currentRoute.contains(screen.route)
                                    || currentRoute.contains(ScreenEntity.EditNotes.route) && screen.route == ScreenEntity.Notes.route
                                    || currentRoute.contains(ScreenEntity.EditTodos.route) && screen.route == ScreenEntity.Todos.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {

                                        navController.graph.startDestinationRoute?.let {
                                            popUpTo(it) {
                                                saveState = true
                                            }
                                        }
                                        restoreState = false
                                    }
                                }
                            }
                        )
                    }
                }

            }
        }
    }
}