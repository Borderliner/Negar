package meshki.studio.negarname.ui.notes

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.entities.Tool
import meshki.studio.negarname.ui.app.AppEvent
import meshki.studio.negarname.ui.app.AppViewModel
import meshki.studio.negarname.ui.components.ActionButton
import meshki.studio.negarname.ui.components.SearchField
import meshki.studio.negarname.ui.components.SortField
import meshki.studio.negarname.ui.components.Toolbox
import meshki.studio.negarname.ui.navigation.ScreenEntity
import meshki.studio.negarname.ui.notes.entities.NoteEntity
import meshki.studio.negarname.ui.notes.entities.NotesEvent
import meshki.studio.negarname.ui.notes.vm.NotesViewModel
import meshki.studio.negarname.ui.util.LeftToRightLayout
import meshki.studio.negarname.ui.util.RightToLeftLayout
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun NotesScreen(navController: NavHostController) {
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
    appViewModel.onEvent(AppEvent.SetBottomBarVisible(true))

    if (appState.value.isRtl) {
        LeftToRightLayout {
            NotesScreenScaffold(navController) {
                RightToLeftLayout {
                    NotesScreenMain(navController)
                }
            }
        }
    } else {
        RightToLeftLayout {
            NotesScreenScaffold(navController) {
                LeftToRightLayout {
                    NotesScreenMain(navController)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesScreenScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            if (appState.value.isRtl) {
                LeftToRightLayout {
                    SnackbarHost(appViewModel.snackbarHost)
                }
            } else {
                RightToLeftLayout {
                    SnackbarHost(appViewModel.snackbarHost)
                }
            }
        },
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
                isBottomBarVisible = appState.value.isBottomBarVisible
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        content()
    }
}

@Composable
fun NotesScreenMain(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
    val ctx = LocalContext.current
    
    val vm = koinViewModel<NotesViewModel>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val orderTool = remember { mutableStateOf(Tool("order")) }
    val searchTool = remember { mutableStateOf(Tool("search")) }

    var isRowView by rememberSaveable { mutableStateOf(true) }

    val offsetAnimation = remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(searchTool.value.visibility.value, orderTool.value.visibility.value) {
        if (searchTool.value.visibility.value) {
            offsetAnimation.value.animateTo(
                100f,
                tween(280, 0, easing = FastOutSlowInEasing)
            )
        } else if (orderTool.value.visibility.value) {
            offsetAnimation.value.animateTo(
                110f,
                tween(280, 0, easing = FastOutSlowInEasing)
            )
        } else {
            offsetAnimation.value.animateTo(
                offsetAnimation.value.lowerBound ?: 0f,
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

    @Composable
    fun createSingleNote(note: NoteEntity) {
        return SingleNote(
            noteEntity = note,
            modifier = Modifier.fillMaxWidth(),
            isRtl = appState.value.isRtl,
            onTap = {
                navController.navigate(
                    ScreenEntity.EditNotes.route +
                            "?id=${note.id}&color=${note.color}"
                )
            },
            onDelete = {
                scope.launch {
                    vm.onEvent(NotesEvent.NoteDeleted(note))
                    val result =
                        appViewModel.showSnackbar(
                            label = getString(ctx, R.string.restore),
                            message = getString(ctx, R.string.note_removed),
                            duration = SnackbarDuration.Long
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        vm.onEvent(NotesEvent.NoteRestored)
                    }
                }
            },
            onPin = {
                if (!note.pinned) {
                    scope.launch {
                        appViewModel.showSnackbar(
                            message = getString(ctx, R.string.restore),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                scope.launch {
                    vm.onEvent(NotesEvent.NotePinToggled(note))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                        modifier = Modifier.zIndex(16f),
                        onClick = {
                            scope.launch {
                                isRowView = !isRowView
                            }
                        },
                    ) {
                        Icon(
                            modifier = Modifier.scale(scaleX = 1f, scaleY = 1f),
                            imageVector = ImageVector.vectorResource(
                                if (isRowView) {
                                    R.drawable.vec_row
                                } else {
                                    R.drawable.vec_grid
                                }
                            ),
                            contentDescription = stringResource(R.string.view)
                        )
                    }
                    IconButton(
                        modifier = Modifier.zIndex(16f),
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
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                    IconButton(
                        modifier = Modifier.zIndex(6f),
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
                            contentDescription = stringResource(R.string.order)
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
            if (uiState.noteEntities.isNotEmpty()) {
                if (isRowView) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 190.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .offset(0.dp, offsetAnimation.value.value.dp)
                    ) {
                        itemsIndexed(uiState.noteEntities) { _, note ->
                            createSingleNote(note)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    val lazyGridState = rememberLazyStaggeredGridState()
                    LazyVerticalStaggeredGrid(
                        contentPadding = PaddingValues(bottom = 65.dp),
                        columns = StaggeredGridCells.Fixed(2),
                        state = lazyGridState,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalItemSpacing = 10.dp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .offset(0.dp, offsetAnimation.value.value.dp)
                    ) {
                        itemsIndexed(uiState.noteEntities) { _, note ->
                            createSingleNote(note)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-90).dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.no_notes))
                }
            }
        }
    }

    Toolbox(
        orderTool.value.visibility,
        orderTool.value.animation,
        animateFromUp = true
    ) {
        SortField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 50.dp,
            caretOffset = 0.91f,
            color = MaterialTheme.colorScheme.secondaryContainer,
            orderBy = uiState.orderBy
        ) {
            vm.viewModelScope.launch {
                vm.onEvent(NotesEvent.NotesOrdered(it))
            }
        }
    }

    Toolbox(
        searchTool.value.visibility,
        searchTool.value.animation,
        animateFromUp = true
    ) {
        SearchField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 50.dp,
            offsetPercent = 0.78f,
            color = MaterialTheme.colorScheme.secondaryContainer,
            onTextChange = {
                vm.viewModelScope.launch {
                    vm.onEvent(
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
