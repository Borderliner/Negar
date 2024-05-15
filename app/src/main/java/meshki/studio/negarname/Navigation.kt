package meshki.studio.negarname

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument

import meshki.studio.negarname.entity.ScreenEntity
import meshki.studio.negarname.ui.screen.CalendarScreen
import meshki.studio.negarname.ui.screen.EditNotesScreen
import meshki.studio.negarname.ui.screen.EditTodosScreen
import meshki.studio.negarname.ui.screen.NotesScreen
import meshki.studio.negarname.ui.screen.SettingsScreen
import meshki.studio.negarname.ui.screen.TodosScreen

@Composable
fun Navigation(appState: AppState) {
    val navigationSpeed = 450
    val pageSpeed = 300

    NavHost(appState.navController, startDestination = ScreenEntity.Notes.route) {
        composable(
            ScreenEntity.Calendar.route,
            enterTransition = {
                if (initialState.destination.route == ScreenEntity.Settings.route) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
                } else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(navigationSpeed))
                }
            },
            exitTransition = {
                if (targetState.destination.route == ScreenEntity.Settings.route) {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(navigationSpeed))
                } else {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
                }
            }
        ) {
            CalendarScreen()
        }

        composable(
            ScreenEntity.Notes.route,
            enterTransition = {
                if (initialState.destination.route == ScreenEntity.Calendar.route) {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(navigationSpeed)
                    )
                } else {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(navigationSpeed)
                    )
                }
            },
            exitTransition = {
                if (targetState.destination.route?.contains("edit") == true) {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(pageSpeed)
                    )
                } else if (targetState.destination.route == ScreenEntity.Calendar.route) {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(pageSpeed)
                    )
                }
                else {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
                }
            },
            popEnterTransition = {
                if (initialState.destination.route?.contains("edit") == true) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
                } else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
                }
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
            }) {
            NotesScreen(appState)
        }

        composable(
            ScreenEntity.Todos.route,
            enterTransition = {
                if (initialState.destination.route == ScreenEntity.Calendar.route) {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(navigationSpeed)
                    )
                } else if (initialState.destination.route?.contains("modify") == true) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(navigationSpeed))
                } else if (initialState.destination.route == ScreenEntity.Settings.route) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
                }
                else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
                }

            },
            exitTransition = {
                if (targetState.destination.route?.contains("modify") == true) {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(pageSpeed))
                } else {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(navigationSpeed))
                }
            },
            popEnterTransition = {
                if (initialState.destination.route?.contains("modify") == true) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
                } else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
                }
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(navigationSpeed))
            }) {
            TodosScreen {
                appState.navController.navigate(it)
            }

        }

        composable(
            route = ScreenEntity.EditNotes.route +
                    "?id={id}&color={color}",
            arguments = listOf(
                navArgument(
                    name = "id"
                ){
                    type = NavType.LongType
                    defaultValue = -1
                },
                navArgument(
                    name = "color"
                ){
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(pageSpeed))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(pageSpeed))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
            }
        ) { nav ->
            EditNotesScreen(color = nav.arguments?.getInt("color") ?: -1, appState)
        }

        composable(
            route = ScreenEntity.EditTodos.route +
                    "?id={id}&color={color}",
            arguments = listOf(
                navArgument(
                    name = "id"
                ){
                    type = NavType.LongType
                    defaultValue = -1
                },
                navArgument(
                    name = "color"
                ){
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(pageSpeed))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(pageSpeed))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
            }
        ) { nav ->
            EditTodosScreen(color = nav.arguments?.getInt("color") ?: -1) {
                appState.navController.navigate(it)
            }
        }

        composable(
            ScreenEntity.Settings.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(pageSpeed))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(pageSpeed))
            }
        ) {
            SettingsScreen {
                appState.navController.navigate(it)
            }
        }
    }
}
