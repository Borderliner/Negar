package meshki.studio.negar

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import meshki.studio.negar.entity.ScreenEntity
import meshki.studio.negar.ui.screen.CalendarScreen
import meshki.studio.negar.ui.screen.EditNotesScreen
import meshki.studio.negar.ui.screen.EditTodosScreen
import meshki.studio.negar.ui.screen.NotesScreen
import meshki.studio.negar.ui.screen.SettingsScreen
import meshki.studio.negar.ui.screen.TodosScreen

@Composable
fun Navigation() {
    val navigationSpeed = 450
    val pageSpeed = 300

    val navController = rememberNavController()

    NavHost(navController, startDestination = ScreenEntity.Notes.route) {
        composable(
            ScreenEntity.Calendar.route,
            enterTransition = {
                if (initialState.destination.route == ScreenEntity.Settings.route) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(navigationSpeed))
                } else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(navigationSpeed))
                }
            },
            exitTransition = {
                if (targetState.destination.route == ScreenEntity.Settings.route) {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(navigationSpeed))
                } else {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(navigationSpeed))
                }
            }
        ) {
            CalendarScreen {
                navController.navigate(it)
            }
        }

        composable(
            ScreenEntity.Todos.route,
            enterTransition = {
                if (initialState.destination.route == ScreenEntity.Calendar.route) {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(navigationSpeed)
                    )
                } else if (initialState.destination.route?.contains("modify") == true) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(navigationSpeed))
                } else if (initialState.destination.route == ScreenEntity.Settings.route) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(navigationSpeed))
                }
                else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(navigationSpeed))
                }

            },
            exitTransition = {
                if (targetState.destination.route?.contains("modify") == true) {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(pageSpeed))
                } else {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(navigationSpeed))
                }
            },
            popEnterTransition = {
                if (initialState.destination.route?.contains("modify") == true) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
                } else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(navigationSpeed))
                }
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(navigationSpeed))
            }) {
            TodosScreen {
                navController.navigate(it)
            }

        }
        composable(
            ScreenEntity.Notes.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(navigationSpeed))
            },
            exitTransition = {
                if (targetState.destination.route?.contains("modify") == true) {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(pageSpeed))
                } else {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(navigationSpeed))
                }
            },
            popEnterTransition = {
                if (initialState.destination.route?.contains("modify") == true) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(pageSpeed))
                } else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(navigationSpeed))
                }
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(navigationSpeed))
            }) {
            NotesScreen {
                navController.navigate(it)
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
            EditNotesScreen(color = nav.arguments?.getInt("color") ?: -1) {
                navController.navigate(it)
            }
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
                navController.navigate(it)
            }
        }

        composable(
            ScreenEntity.Settings.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(pageSpeed))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(pageSpeed))
            }
        ) {
            SettingsScreen {
                navController.navigate(it)
            }
        }
    }
}
