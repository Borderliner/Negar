package meshki.studio.negarname.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.entity.ScreenEntity
import meshki.studio.negarname.ui.element.ActionButton
import meshki.studio.negarname.ui.element.SingleNote
import meshki.studio.negarname.ui.element.NotesOrderSection
import meshki.studio.negarname.ui.element.NotesSearchSection
import meshki.studio.negarname.ui.element.Toolbox
import meshki.studio.negarname.util.LeftToRightLayout
import meshki.studio.negarname.util.RightToLeftLayout
import meshki.studio.negarname.vm.MainViewModel
import meshki.studio.negarname.vm.NotesEvent
import meshki.studio.negarname.vm.NotesViewModel
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import meshki.studio.negarname.entity.Tool
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun NotesScreen(navController: NavHostController) {
    val mainViewModel = koinInject<MainViewModel>()
    val viewModel = koinViewModel<NotesViewModel>()
    val snackbar = remember { SnackbarHostState() }
    if (mainViewModel.isRtl) {
        LeftToRightLayout {
            NotesScreenScaffold(navController, snackbar) {
                RightToLeftLayout {
                    NotesScreenMain(viewModel, navController, snackbar)
                }
            }
        }
    } else {
        RightToLeftLayout {
            NotesScreenScaffold(navController, snackbar) {
                LeftToRightLayout {
                    NotesScreenMain(viewModel, navController, snackbar)
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
) {
    val mainViewModel = koinInject<MainViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val orderTool = remember { mutableStateOf(Tool("order")) }
    val searchTool =  remember { mutableStateOf(Tool("search")) }

    val offsetAnimation = remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(searchTool.value.visibility.value, orderTool.value.visibility.value) {
        if (searchTool.value.visibility.value) {
            offsetAnimation.value.animateTo(100f,
                tween(280, 0, easing = FastOutSlowInEasing)
            )
        } else if (orderTool.value.visibility.value) {
            offsetAnimation.value.animateTo(110f,
                tween(280, 0, easing = FastOutSlowInEasing)
            )
        } else {
            offsetAnimation.value.animateTo(offsetAnimation.value.lowerBound ?: 0f,
                tween(300, 200, easing = FastOutSlowInEasing)
            )
        }
    }

    suspend fun openTool(tool: MutableState<Tool>, delay: Int = 0) {
        tool.value.visibility.value = true
        tool.value.animation.value.animateTo(
            tool.value.animation.value.upperBound ?: Float.MAX_VALUE,
            tween(320, delay, easing = FastOutSlowInEasing)
        )
    }

    suspend fun closeTool(tool: MutableState<Tool>) {
        tool.value.visibility.value = false
        // Animated hide current Tool
        tool.value.animation.value.snapTo(
            tool.value.animation.value.lowerBound ?: 0f,
        )
    }

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
                            scope.launch {
                                //onToolClicked(searchTool)
                                if (!searchTool.value.visibility.value) {
                                    closeTool(orderTool)
                                    openTool(searchTool)
                                } else {
                                    closeTool(searchTool)
                                    closeTool(orderTool)
                                }
                            }
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
                            scope.launch {
                                //onToolClicked(orderTool)
                                if (!orderTool.value.visibility.value) {
                                    closeTool(searchTool)
                                    openTool(orderTool)
                                } else {
                                    closeTool(orderTool)
                                    closeTool(searchTool)
                                }
                            }
                        },
                    ) {
                        Icon(
                            modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
                            imageVector = ImageVector.vectorResource(R.drawable.vec_sort),
                            contentDescription = "Order"
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
                    contentPadding = PaddingValues(bottom = 65.dp),
                    modifier = Modifier.offset(
                        0.dp, offsetAnimation.value.value.dp
                    )
                ) {
                    items(uiState.notes) { note ->
                        SingleNote(
                            note = note,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth(),
                            isRtl = mainViewModel.isRtl,
                            onTap = {
                                println("Note ID: $note.id, Color: $note.color")
                                navController.navigate(
                                    ScreenEntity.EditNotes.route +
                                            "?id=${note.id}&color=${note.color}"
                                )
                            },
                            onDelete = {
                                scope.launch {
                                    viewModel.onEvent(NotesEvent.NoteDeleted(note))
                                    val result =
                                        snackbar.showSnackbar(
                                            message = "یادداشت پاک شد",
                                            actionLabel = "بازیابی"
                                        )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.onEvent(NotesEvent.NoteRestored)
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
                                    viewModel.onEvent(NotesEvent.NotePinToggled(note))
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

    Toolbox(
        orderTool.value.visibility.value,
        orderTool.value.animation.value
    ) {
        NotesOrderSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 50.dp,
            caretOffset = 0.91f,
            color = MaterialTheme.colorScheme.secondaryContainer,
            orderBy = uiState.orderBy
        ) {
            viewModel.viewModelScope.launch {
                viewModel.onEvent(NotesEvent.NotesOrdered(it))
            }
        }
    }

    Toolbox(
        searchTool.value.visibility.value,
        searchTool.value.animation.value
    ) {
        NotesSearchSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 50.dp,
            offsetPercent = 0.78f,
            color = MaterialTheme.colorScheme.secondaryContainer,
            onTextChange = {
                viewModel.viewModelScope.launch {
                    viewModel.onEvent(
                        NotesEvent.NoteQueried(
                            it,
                            uiState.orderBy
                        )
                    )
                }
            }
        )
    }
}
