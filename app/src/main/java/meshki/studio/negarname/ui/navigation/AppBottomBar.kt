package meshki.studio.negarname.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

@Composable
fun AppBottomBar(navController: NavHostController) {
    val screenItems = listOf(
        ScreenEntity.Calendar,
        ScreenEntity.Notes,
        ScreenEntity.Todos,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val opacity = 0.93f

    NavigationBar(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 8.dp),
            //.navigationBarsPadding(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        OutlinedCard(
            elevation = CardDefaults.cardElevation(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = opacity)),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                screenItems.forEach { screen ->
                    if (currentRoute != null) {
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.65f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.65f),
                            ),
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.icon),
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