package meshki.studio.negarname.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.entity.ScreenEntity
import meshki.studio.negarname.ui.element.ActionButton
import meshki.studio.negarname.ui.element.SingleNote
import meshki.studio.negarname.util.LeftToRightLayout
import meshki.studio.negarname.util.RightToLeftLayout
import meshki.studio.negarname.vm.MainViewModel
import meshki.studio.negarname.vm.NotesEvent
import meshki.studio.negarname.vm.NotesViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun NotesScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val viewModel = koinViewModel<NotesViewModel>()
    val snackbar = remember { SnackbarHostState() }
    if (mainViewModel.isRtl) {
        LeftToRightLayout {
            NotesScreenScaffold(navController, snackbar) {
                RightToLeftLayout {
                    NotesScreenMain(viewModel, navController, snackbar, mainViewModel)
                }
            }
        }
    } else {
        RightToLeftLayout {
            NotesScreenScaffold(navController, snackbar) {
                LeftToRightLayout {
                    NotesScreenMain(viewModel, navController, snackbar, mainViewModel)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesScreenScaffold(
    navController: NavHostController,
    snackbar: SnackbarHostState,
    content: @Composable () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ActionButton(
                text = stringResource(R.string.write),
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.vec_ink_pen),
                        contentDescription = "Add Note"
                    )
                },
                modifier = Modifier,
                onClick = {
                    navController.navigate(ScreenEntity.EditNotes.route)
                },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        content()
    }
}

@Composable
fun NotesScreenMain(
    viewModel: NotesViewModel,
    navController: NavHostController,
    snackbar: SnackbarHostState,
    mainViewModel: MainViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.notes),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Column {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    IconButton(
                        onClick = {
                            //onClickSearchButton()
                        },
                    ) {
                        Icon(
                            modifier = Modifier.scale(scaleX = 1f, scaleY = 1f),
                            imageVector = ImageVector.vectorResource(R.drawable.vec_search),
                            contentDescription = "Search"
                        )
                    }
                    IconButton(
                        onClick = {
                            //onClickSortButton()
                        },
                    ) {
                        Icon(
                            modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
                            imageVector = ImageVector.vectorResource(R.drawable.vec_sort),
                            contentDescription = "Sort"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(Color.Transparent),
            shape = RoundedCornerShape(
                topStart = 10.dp,
                topEnd = 10.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            if (uiState.notes.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 65.dp)
                ) {
                    items(uiState.notes) { note ->
                        SingleNote(
                            isRtl = mainViewModel.isRtl,
                            note = note,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        ScreenEntity.EditNotes.route +
                                                "?id=${note.id}&color=${note.color}"
                                    )
                                },
                            onDelete = {
                                scope.launch {
                                    viewModel.onEvent(NotesEvent.DeleteNote(note))
                                    val result =
                                        snackbar.showSnackbar(
                                            message = "یادداشت پاک شد",
                                            actionLabel = "بازیابی"
                                        )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            },
                            onPin = {
                                if (!note.pinned) {
                                    scope.launch {
                                        snackbar.showSnackbar(
                                            message = "یادداشت سوزن شد.",
                                        )
                                    }
                                }

                                scope.launch {
                                    viewModel.onEvent(NotesEvent.TogglePinNote(note))
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.no_notes))
                }
            }
        }
    }
}
