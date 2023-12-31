package meshki.studio.negarname.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.element.BackPressHandler
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.Tool
import meshki.studio.negarname.entity.UiEvent
import meshki.studio.negarname.ui.element.ActionButton
import meshki.studio.negarname.ui.element.HintedTextField
import meshki.studio.negarname.ui.element.PopupSection
import meshki.studio.negarname.ui.element.Toolbox
import meshki.studio.negarname.ui.theme.RoundedShapes
import meshki.studio.negarname.util.LeftToRightLayout
import meshki.studio.negarname.util.RightToLeftLayout
import meshki.studio.negarname.vm.EditNotesEvent
import meshki.studio.negarname.vm.EditNotesViewModel
import meshki.studio.negarname.vm.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun EditNotesScreen(color: Int, navController: NavHostController) {
    val mainViewModel = koinInject<MainViewModel>()
    val viewModel = koinViewModel<EditNotesViewModel>()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(color) {
        if (color > 0) {
            viewModel.viewModelScope.launch {
                viewModel.onEvent(EditNotesEvent.ColorChanged(color))
            }
        }
    }
    if (mainViewModel.isRtl) {
        LeftToRightLayout {
            EditNotesScreenScaffold(viewModel, snackbar) {
                RightToLeftLayout {
                    EditNotesScreenMain(viewModel, navController, snackbar)
                }
            }
        }
    } else {
        RightToLeftLayout {
            EditNotesScreenScaffold(viewModel, snackbar) {
                LeftToRightLayout {
                    EditNotesScreenMain(viewModel, navController, snackbar)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditNotesScreenScaffold(
    viewModel: EditNotesViewModel,
    snackbar: SnackbarHostState,
    content: @Composable () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ActionButton(
                text = stringResource(R.string.save),
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.vec_done),
                        contentDescription = stringResource(R.string.save)
                    )
                },
                modifier = Modifier,
                onClick = {
                    viewModel.onEvent(EditNotesEvent.NoteSaved)
                    viewModel.isNoteModified = false
                },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        content()
    }
}

@Composable
fun EditNotesScreenMain(
    viewModel: EditNotesViewModel,
    navController: NavHostController,
    snackbar: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val noteState = viewModel.noteState
    val noteColorsArgb = Note.colors.map {
        it.toArgb()
    }
    val colorPaletteState = rememberLazyListState()
    var workInProgressAlertVisible by remember { mutableStateOf(false) }

    val colorTool = remember { mutableStateOf(Tool("color")) }
    val offsetAnimation = remember { mutableStateOf(Animatable(0f)) }

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

    LaunchedEffect(colorTool.value.visibility.value) {
        if (colorTool.value.visibility.value) {
            offsetAnimation.value.animateTo(
                80f,
                tween(280, 0, easing = FastOutSlowInEasing)
            )
        } else {
            offsetAnimation.value.animateTo(
                offsetAnimation.value.lowerBound ?: 0f,
                tween(300, 200, easing = FastOutSlowInEasing)
            )
        }
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackbar.showSnackbar(
                        message = event.message
                    )
                }

                is UiEvent.NoteSaved -> {
                    navController.navigateUp()
                }

                else -> {}
            }
        }
    }

    BackPressHandler {
        if ((noteState.value.title.isNotEmpty() || noteState.value.text.isNotEmpty()) && viewModel.isNoteModified) {
            workInProgressAlertVisible = true
        } else {
            navController.navigateUp()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(noteState.value.color))
                    .clickable {
                        scope.launch {
                            if (colorTool.value.visibility.value) {
                                closeTool(colorTool)
                            } else {
                                openTool(colorTool)
                            }
                        }
                    },
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(R.drawable.water_drop),
                        //modifier = Modifier.background(Color.Black),
                        contentDescription = "",
                        tint = Color.Black.copy(0.9f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(8.dp)
                .offset(0.dp, offsetAnimation.value.value.dp)
        ) {
            HintedTextField(
                modifier = Modifier
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onBackground.copy(0.6f),
                        RoundedShapes.large
                    )
                    .padding(8.dp)
                    .padding(bottom = 4.dp),
                text = noteState.value.title,
                hint = stringResource(R.string.title),
                hintColor = MaterialTheme.colorScheme.onBackground.copy(0.55f),
                onValueChange = {
                    viewModel.onEvent(EditNotesEvent.TitleEntered(it))
                    viewModel.isNoteModified = true
                },
                onFocusChange = {
                    viewModel.isTitleHintVisible = !it.isFocused && noteState.value.title.isBlank()
                },
                isHintVisible = viewModel.isTitleHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.height(12.dp))

//            RichTextEditor(
//                state = contentState,
//                modifier = Modifier
//                    .fillMaxSize(),
//                singleLine = false,
//                colors = RichTextEditorDefaults.outlinedRichTextEditorColors(
//                    containerColor = Color.Transparent,
//                    textColor = MaterialTheme.colorScheme.onBackground,
//                    cursorColor = MaterialTheme.colorScheme.primary,
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent,
//                    errorBorderColor = Color.Transparent,
//                    disabledBorderColor = Color.Transparent
//                ),
//                placeholder = { Text(stringResource(R.string.note_text_hint)) },
//
//                )
            HintedTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .defaultMinSize(minHeight = 100.dp)
                    .padding(horizontal = 8.dp),
                text = noteState.value.text,
                hint = stringResource(R.string.note_text_hint),
                hintColor = MaterialTheme.colorScheme.onBackground.copy(0.55f),
                onValueChange = {
                    viewModel.onEvent(EditNotesEvent.TextEntered(it))
                    viewModel.isNoteModified = true
                },
                onFocusChange = {
                    viewModel.isTextHintVisible = !it.isFocused && noteState.value.text.isBlank()
                },
                expanded = true,
                isHintVisible = viewModel.isTextHintVisible,
                textStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onBackground),
            )
        }
    }

    Toolbox(
        colorTool.value.visibility.value,
        colorTool.value.animation.value
    ) {
        PopupSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 60.dp,
            offsetPercent = 0.07f,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            LaunchedEffect(noteState.value.color) {
                scope.launch {
                    delay(500)
                    if (noteState.value.color != 0) {
                        colorPaletteState.animateScrollToItem(noteColorsArgb.indexOf(noteState.value.color))
                    }
                }
            }
            LazyRow(
                state = colorPaletteState,
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(Note.colors) {
                    val colorInt = it.toArgb()
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(15.dp, CircleShape)
                            .clip(CircleShape)
                            .background(it)
                            .clickable {
                                viewModel.onEvent(
                                    EditNotesEvent.ColorChanged(colorInt)
                                )
                            }
                    ) {
                        if (noteState.value.color == colorInt) {
                            Column(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(it)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.onBackground.copy(0.75f),
                                        shape = CircleShape
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painterResource(R.drawable.vec_done),
                                    //modifier = Modifier.background(Color.Black),
                                    contentDescription = "",
                                    modifier = Modifier.scale(1.2f, 1.2f),
                                    tint = Color.Black.copy(0.75f)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}
