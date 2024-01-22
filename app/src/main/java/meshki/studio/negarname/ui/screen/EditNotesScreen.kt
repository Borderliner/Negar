package meshki.studio.negarname.ui.screen

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.infiniteLinearGradient
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import meshki.studio.negarname.R
import meshki.studio.negarname.entity.CustomPath
import meshki.studio.negarname.entity.DrawMode
import meshki.studio.negarname.entity.DrawingPath
import meshki.studio.negarname.entity.MotionEvent
import meshki.studio.negarname.ui.element.BackPressHandler
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.entity.PathProperties
import meshki.studio.negarname.entity.Tool
import meshki.studio.negarname.entity.UiEvent
import meshki.studio.negarname.entity.Week
import meshki.studio.negarname.service.AlarmData
import meshki.studio.negarname.service.VoiceRecorder
import meshki.studio.negarname.ui.element.ActionButton
import meshki.studio.negarname.ui.element.HintedTextField
import meshki.studio.negarname.ui.element.PopupSection
import meshki.studio.negarname.ui.element.Toolbox
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.theme.PastelLavender
import meshki.studio.negarname.ui.theme.PastelLime
import meshki.studio.negarname.ui.theme.PastelOrange
import meshki.studio.negarname.ui.theme.PastelPink
import meshki.studio.negarname.ui.theme.PastelRed
import meshki.studio.negarname.ui.theme.RoundedShapes
import meshki.studio.negarname.util.LeftToRightLayout
import meshki.studio.negarname.util.RightToLeftLayout
import meshki.studio.negarname.util.checkAlarmsPermission
import meshki.studio.negarname.util.checkPermission
import meshki.studio.negarname.util.checkPermissions
import meshki.studio.negarname.util.dragMotionEvent
import meshki.studio.negarname.vm.EditNotesEvent
import meshki.studio.negarname.vm.EditNotesViewModel
import meshki.studio.negarname.vm.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import timber.log.Timber
import java.text.NumberFormat
import java.util.Calendar

@Composable
fun EditNotesScreen(color: Int, navController: NavHostController) {
    val mainViewModel = koinInject<MainViewModel>()
    mainViewModel.setBottomBarVisible(false)
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val recorderTool = remember { mutableStateOf(Tool("recorder")) }
    val alarmTool = remember { mutableStateOf(Tool("alarm")) }
    val drawingTool = remember { mutableStateOf(Tool("drawing")) }
    val offsetAnimation = remember { mutableStateOf(Animatable(0f)) }
    val ctx = LocalContext.current

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

//    LaunchedEffect(
//        colorTool.value.visibility.value,
//        recorderTool.value.visibility.value,
//        alarmTool.value.visibility.value
//    ) {
//        if (colorTool.value.visibility.value) {
//            offsetAnimation.value.animateTo(
//                80f,
//                tween(280, 0, easing = FastOutSlowInEasing)
//            )
//        } else if (recorderTool.value.visibility.value) {
//            offsetAnimation.value.animateTo(
//                110f,
//                tween(280, 0, easing = FastOutSlowInEasing)
//            )
//        } else if (alarmTool.value.visibility.value) {
//            offsetAnimation.value.animateTo(
//                240f,
//                tween(280, 0, easing = FastOutSlowInEasing)
//            )
//        } else {
//            offsetAnimation.value.animateTo(
//                offsetAnimation.value.lowerBound ?: 0f,
//                tween(300, 200, easing = FastOutSlowInEasing)
//            )
//        }
//    }

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
                            if (!colorTool.value.visibility.value) {
                                closeTool(recorderTool)
                                closeTool(alarmTool)
                                closeTool(drawingTool)
                                openTool(colorTool)
                            } else {
                                closeTool(colorTool)
                                closeTool(recorderTool)
                                closeTool(alarmTool)
                                closeTool(drawingTool)
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

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (viewModel.alarms.size > 0) PastelLavender else {
                            if (isSystemInDarkTheme()) Color.Gray else Color.LightGray
                        }
                    )
                    .clickable {
                        scope.launch {
                            if (!alarmTool.value.visibility.value) {
                                closeTool(colorTool)
                                closeTool(recorderTool)
                                closeTool(drawingTool)
                                openTool(alarmTool)
                            } else {
                                closeTool(alarmTool)
                                closeTool(colorTool)
                                closeTool(drawingTool)
                                closeTool(recorderTool)
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
                        painterResource(R.drawable.alarm),
                        //modifier = Modifier.background(Color.Black),
                        contentDescription = "",
                        tint = Color.Black.copy(0.9f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (viewModel.voiceState.value.duration > 0)
                            PastelPink
                        else {
                            if (isSystemInDarkTheme()) Color.Gray else Color.LightGray
                        }
                    )
                    .clickable {
                        scope.launch {
                            if (!recorderTool.value.visibility.value) {
                                closeTool(colorTool)
                                closeTool(alarmTool)
                                closeTool(drawingTool)
                                openTool(recorderTool)
                            } else {
                                closeTool(recorderTool)
                                closeTool(colorTool)
                                closeTool(drawingTool)
                                closeTool(alarmTool)
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
                        painterResource(R.drawable.mic),
                        //modifier = Modifier.background(Color.Black),
                        contentDescription = "",
                        tint = Color.Black.copy(0.9f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (noteState.value.drawing.isNotEmpty()) PastelLime else {
                            if (isSystemInDarkTheme()) Color.Gray else Color.LightGray
                        }
                    )
                    .clickable {
                        scope.launch {
                            if (!drawingTool.value.visibility.value) {
                                closeTool(colorTool)
                                closeTool(alarmTool)
                                closeTool(recorderTool)
                                openTool(drawingTool)
                            } else {
                                closeTool(drawingTool)
                                closeTool(recorderTool)
                                closeTool(colorTool)
                                closeTool(alarmTool)
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
                        painterResource(R.drawable.stylus_note),
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
            //.offset(0.dp, offsetAnimation.value.value.dp)
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
                color = Color(noteState.value.color),
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
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
                color = Color(noteState.value.color),
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onBackground),
            )
        }
    }

    Toolbox(
        colorTool.value.visibility,
        colorTool.value.animation
    ) {
        PopupSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 63.dp,
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
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (noteState.value.color == colorInt) {
                            Column(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                                    .background(it)
                                    .border(
                                        2.dp,
                                        Color.Black.copy(0.75f),
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

    Toolbox(
        recorderTool.value.visibility,
        recorderTool.value.animation
    ) {
        PopupSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 63.dp,
            offsetPercent = 0.33f,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Box(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val formatter = NumberFormat.getInstance()
                    Text(
                        fontSize = 22.sp,
                        text = "${
                            if (viewModel.voiceState.value.duration / 1000 / 60 < 10) formatter.format(
                                0
                            ) else ""
                        }${formatter.format((viewModel.voiceState.value.duration / 1000) / 60)}:${
                            if (viewModel.voiceState.value.duration / 1000 % 60 < 10) formatter.format(
                                0
                            ) else ""
                        }${
                            formatter.format(
                                (viewModel.voiceState.value.duration / 1000) % 60
                            )
                        }"
                    )
                    var waveformProgress by remember { mutableStateOf(0F) }

                    val animatedGradientBrush = Brush.infiniteLinearGradient(
                        colors = listOf(Color(noteState.value.color), MaterialTheme.colorScheme.primary),
                        animation = tween(durationMillis = 6000, easing = LinearEasing),
                        width = 128F
                    )

                    if (viewModel.voiceState.value.duration > 0) {
                        AudioWaveform(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            style = Fill,
                            waveformAlignment = WaveformAlignment.Center,
                            amplitudeType = AmplitudeType.Avg,
                            amplitudes = viewModel.voiceState.value.amplitudes,
                            progressBrush = animatedGradientBrush,
                            waveformBrush = SolidColor(Color.LightGray),
                            spikeWidth = 4.dp,
                            spikePadding = 2.dp,
                            spikeRadius = 4.dp,
                            progress = waveformProgress,
                            onProgressChange = {
                                waveformProgress = it
                                viewModel.recorder.value.seekTo((it * viewModel.voiceState.value.duration).toInt())
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val voicePermissionTitle =
                            stringResource(R.string.permission_required_title)
                        val voicePermissionText = stringResource(R.string.permission_required_text)
                        val voicePermissionLauncher =
                            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                                if (!granted) {
                                    Toast.makeText(ctx, voicePermissionText, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        var isRecording by remember { mutableStateOf(false) }
                        var replaceRecordAlert by remember { mutableStateOf(false) }
                        var deleteRecordAlert by remember { mutableStateOf(false) }

                        if (replaceRecordAlert) {
                            AlertDialog(
                                icon = {
                                    Icon(Icons.Filled.Warning, contentDescription = stringResource(R.string.record))
                                },
                                title = {
                                    Text(text = stringResource(R.string.voice_replace_title))
                                },
                                text = {
                                    Text(text = stringResource(R.string.voice_replace_text))
                                },
                                onDismissRequest = {
                                    replaceRecordAlert = false
                                },
                                confirmButton = {
                                    TextButton(
                                        colors = ButtonDefaults.textButtonColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error),
                                        onClick = {
                                            replaceRecordAlert = false
                                            viewModel.recorder.value.startRecording()
                                            isRecording = viewModel.recorder.value.isRecording()
                                        }
                                    ) {
                                        Text(stringResource(R.string.yes))
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        colors = ButtonDefaults.textButtonColors(PastelGreen, MaterialTheme.colorScheme.onBackground),
                                        onClick = {
                                            replaceRecordAlert = false
                                        }
                                    ) {
                                        Text(stringResource(R.string.no))
                                    }
                                }
                            )
                        }

                        if (deleteRecordAlert) {
                            AlertDialog(
                                icon = {
                                    Icon(Icons.Filled.Warning, contentDescription = stringResource(R.string.record))
                                },
                                title = {
                                    Text(text = stringResource(R.string.voice_delete_title))
                                },
                                text = {
                                    Text(text = stringResource(R.string.voice_delete_text))
                                },
                                onDismissRequest = {
                                    deleteRecordAlert = false
                                },
                                confirmButton = {
                                    TextButton(
                                        colors = ButtonDefaults.textButtonColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error),
                                        onClick = {
                                            deleteRecordAlert = false
                                            viewModel.onEvent(EditNotesEvent.VoiceRemoved)
                                        }
                                    ) {
                                        Text(stringResource(R.string.yes))
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        colors = ButtonDefaults.textButtonColors(PastelGreen, MaterialTheme.colorScheme.onBackground),
                                        onClick = {
                                            deleteRecordAlert = false
                                        }
                                    ) {
                                        Text(stringResource(R.string.no))
                                    }
                                }
                            )
                        }

                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                PastelOrange,
                                Color.Black.copy(0.9f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            onClick = { deleteRecordAlert = true }) {
                            Icon(
                                painterResource(R.drawable.vec_delete),
                                //modifier = Modifier.background(Color.Black),
                                contentDescription = stringResource(R.string.delete),
                                tint = Color.Black.copy(0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                if (isRecording) PastelOrange else PastelRed,
                                Color.Black.copy(0.9f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            onClick = {
                                scope.launch {
                                    checkPermission(
                                        ctx,
                                        Manifest.permission.RECORD_AUDIO,
                                        voicePermissionLauncher
                                    ) {

                                        if (!viewModel.recorder.value.isRecording()) {
                                            if (viewModel.voiceState.value.duration > 0) {
                                                replaceRecordAlert = true
                                            } else {
                                                viewModel.recorder.value.startRecording()
                                            }
                                        } else {
                                            viewModel.recorder.value.stopRecording()
                                            scope.launch {
                                                viewModel.readVoiceToState(
                                                    "records/${noteState.value.id}.aac",
                                                    scope.coroutineContext
                                                ).data?.collectLatest {
                                                    viewModel.setVoiceState(it)
                                                    Timber.tag("Edit Screen").i(it.toString())
                                                }
                                            }
                                        }

                                        isRecording = viewModel.recorder.value.isRecording()
                                    }
                                }
                            }) {
                            Icon(
                                painterResource(if (isRecording) R.drawable.stop else R.drawable.mic),
                                //modifier = Modifier.background(Color.Black),
                                contentDescription = "",
                                tint = Color.Black.copy(0.9f)
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                text = if (isRecording) stringResource(R.string.stop) else stringResource(
                                    R.string.record
                                )
                            )
                        }

                        var isPlaying by remember { mutableStateOf(false) }
                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                PastelGreen,
                                Color.Black.copy(0.9f)
                            ),
                            enabled = viewModel.voiceState.value.duration > 0,
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            onClick = {
                                scope.launch {
                                    val recorder = VoiceRecorder(ctx, noteState.value.id.toString())
                                    if (!recorder.isPlaying()) {
                                        recorder.startPlaying()
                                    } else {
                                        !recorder.stopPlaying()
                                    }
                                    isPlaying = recorder.isPlaying()
                                }
                            }) {
                            Icon(
                                painterResource(if (isPlaying) R.drawable.stop else R.drawable.play_arrow),
                                //modifier = Modifier.background(Color.Black),
                                contentDescription = "",
                                tint = Color.Black.copy(0.9f)
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                text = if (isPlaying) stringResource(R.string.stop) else stringResource(
                                    R.string.play
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    Toolbox(
        alarmTool.value.visibility,
        alarmTool.value.animation
    ) {
        PopupSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 63.dp,
            offsetPercent = 0.20f,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Box(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.alarm))

                    val alarmTime = remember { mutableLongStateOf(0L) }
                    val cal = remember {
                        mutableStateOf(Calendar.getInstance().apply {
                            if (viewModel.alarms.size > 0) {
                                val tempCal = Calendar.getInstance().apply {
                                    timeInMillis = viewModel.alarms[0].time
                                }
                                val hour = tempCal.get(Calendar.HOUR_OF_DAY)
                                val minute = tempCal.get(Calendar.MINUTE)

                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                        })
                    }

                    LaunchedEffect(alarmTime.longValue) {
                        if (alarmTime.longValue > 0) {
                            cal.value.timeInMillis = alarmTime.longValue
                        }
                    }

                    val timePicker = rememberTimePickerState(
                        initialHour = cal.value.get(Calendar.HOUR_OF_DAY),
                        initialMinute = cal.value.get(Calendar.MINUTE),
                        is24Hour = true
                    )
                    TimeInput(state = timePicker, modifier = Modifier.padding(8.dp))

                    var repeating by remember { mutableStateOf(viewModel.alarms.size > 1) }
                    var critical by remember {
                        mutableStateOf(
                            if (viewModel.alarms.size > 0) {
                                viewModel.alarms[0].critical
                            } else false
                        )
                    }
                    val week by remember { mutableStateOf(Week.fromAlarms(viewModel.alarms)) }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(checked = repeating, onCheckedChange = { repeating = it })
                            Text(stringResource(R.string.repeating))
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = critical,
                                onCheckedChange = { critical = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PastelRed,
                                    checkedBorderColor = PastelRed,
                                    checkedTrackColor = PastelPink,
                                )
                            )
                            Text(stringResource(R.string.critical))
                        }
                    }

                    AnimatedVisibility(
                        visible = repeating,
                        enter = fadeIn() + slideInVertically(initialOffsetY = {
                            it / 8
                        }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = {
                            it / 8
                        }),
                    ) {
                        Row(
                            modifier = Modifier.padding(top = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            week.list.forEachIndexed { idx, item ->
                                var checked by remember { mutableStateOf(week.list[idx].value) }
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(item.name)
                                    //Text(DateFormatSymbols().getDayOfWeek(firstDayOfWeekIndex % 6))
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            week.setDayValueByName(item.name, it)
                                            checked = it
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val notificationPermissions = mutableListOf<String>()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            notificationPermissions.add(Manifest.permission.FOREGROUND_SERVICE)
                        }
                        notificationPermissions.add(Manifest.permission.WAKE_LOCK)

                        val notificationPermissionLauncher =
                            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionMap ->
                                val areGranted =
                                    permissionMap.values.reduce { acc, next -> acc && next }
                                if (!areGranted) {
                                    Toast.makeText(
                                        ctx,
                                        ctx.resources.getString(R.string.permission_required_text),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        if (viewModel.alarms.size > 0) {
                            ElevatedButton(
                                colors = ButtonDefaults.elevatedButtonColors(
                                    PastelRed,
                                    Color.Black.copy(0.9f)
                                ),
                                elevation = ButtonDefaults.buttonElevation(4.dp),
                                onClick = {
                                    scope.launch {
                                        viewModel.onEvent(EditNotesEvent.DeleteNoteAlarms(noteState.value.id))
                                        viewModel.alarms.clear()
                                        repeating = false
                                        critical = false
                                    }
                                }) {
                                Icon(
                                    painterResource(R.drawable.timer_off),
                                    //modifier = Modifier.background(Color.Black),
                                    contentDescription = "",
                                    tint = Color.Black.copy(0.9f)
                                )
                                Text(
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                    text = stringResource(R.string.delete_alarm)
                                )
                            }
                        } else {
                            ElevatedButton(
                                colors = ButtonDefaults.elevatedButtonColors(
                                    PastelLavender,
                                    Color.Black.copy(0.9f)
                                ),
                                elevation = ButtonDefaults.buttonElevation(4.dp),
                                onClick = {
                                    scope.launch {
                                        Timber.d(notificationPermissions.toString())
                                        checkAlarmsPermission(ctx)
                                        checkPermissions(
                                            ctx,
                                            notificationPermissions.toTypedArray(),
                                            notificationPermissionLauncher
                                        ) {
                                            cal.value.apply {
                                                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                                                set(Calendar.MINUTE, timePicker.minute)
                                                set(Calendar.SECOND, 0)
                                            }

                                            alarmTime.longValue = cal.value.timeInMillis

                                            viewModel.onEvent(
                                                EditNotesEvent.SetAlarm(
                                                    AlarmData(
                                                        id = noteState.value.id,
                                                        time = alarmTime.longValue,
                                                        title = noteState.value.title,
                                                        text = noteState.value.text,
                                                        critical = critical,
                                                        repeating = repeating,
                                                        week = week
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }) {
                                Icon(
                                    painterResource(R.drawable.alarm),
                                    //modifier = Modifier.background(Color.Black),
                                    contentDescription = stringResource(R.string.set_alarm),
                                    tint = Color.Black.copy(0.9f)
                                )
                                Text(
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                    text = stringResource(R.string.set_alarm)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Toolbox(
        drawingTool.value.visibility,
        drawingTool.value.animation
    ) {
        PopupSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            topPadding = 63.dp,
            offsetPercent = 0.46f,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Box(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.drawing))

                    val paths = remember { mutableStateListOf<DrawingPath>() }
                    val pathsUndone = remember { mutableStateListOf<DrawingPath>() }

                    LaunchedEffect(noteState.value.drawing) {
                        if (noteState.value.drawing.isNotEmpty()) {
                            val drawingPaths = Gson().fromJson(
                                noteState.value.drawing,
                                Array<DrawingPath>::class.java
                            ).toList()
                            drawingPaths.forEach {
                                it.path.draw()
                                paths.add(it)
                            }
                            println("LOADED PATH: $drawingPaths")
                        }
                    }

                    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
                    // This is our motion event we get from touch motion
                    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
                    // This is previous motion event before next touch is saved into this current position
                    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

                    var drawMode by remember { mutableStateOf(DrawMode.Draw) }
                    var currentPath by remember { mutableStateOf(CustomPath()) }
                    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

                    val drawModifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .shadow(1.dp)
                        .height(375.dp)
                        .clipToBounds()
                        .background(Color.White)
                        .dragMotionEvent(
                            onDragStart = { pointerInputChange ->
                                motionEvent = MotionEvent.Down
                                currentPosition = pointerInputChange.position
                                pointerInputChange.consumeDownChange()
                            },
                            onDrag = { pointerInputChange ->
                                motionEvent = MotionEvent.Move
                                currentPosition = pointerInputChange.position

                                if (drawMode == DrawMode.Touch) {
                                    val change = pointerInputChange.positionChange()
                                    paths.forEach { entry ->
                                        entry.path.translate(change)
                                    }
                                    currentPath.translate(change)
                                }
                                pointerInputChange.consumePositionChange()

                            },
                            onDragEnd = { pointerInputChange ->
                                motionEvent = MotionEvent.Up
                                pointerInputChange.consumeDownChange()
                            }
                        )

                    Canvas(modifier = drawModifier) {
                        when (motionEvent) {
                            MotionEvent.Down -> {
                                if (drawMode != DrawMode.Touch) {
                                    currentPath.moveTo(currentPosition.x, currentPosition.y)
                                }
                                previousPosition = currentPosition
                            }

                            MotionEvent.Move -> {
                                if (drawMode != DrawMode.Touch) {
                                    currentPath.quadraticBezierTo(
                                        previousPosition.x,
                                        previousPosition.y,
                                        (previousPosition.x + currentPosition.x) / 2,
                                        (previousPosition.y + currentPosition.y) / 2

                                    )
                                }
                                previousPosition = currentPosition
                            }

                            MotionEvent.Up -> {
                                if (drawMode != DrawMode.Touch) {
                                    currentPath.lineTo(currentPosition.x, currentPosition.y)
                                    // Pointer is up save current path
                                    // paths[currentPath] = currentPathProperty
                                    paths.add(DrawingPath(currentPath, currentPathProperty))
                                    // Since paths are keys for map, use new one for each key
                                    // and have separate path for each down-move-up gesture cycle
                                    currentPath = CustomPath()
                                    // Create new instance of path properties to have new path and properties
                                    // only for the one currently being drawn
                                    currentPathProperty = PathProperties(
                                        strokeWidth = currentPathProperty.strokeWidth,
                                        color = currentPathProperty.color,
                                        strokeCap = currentPathProperty.strokeCap,
                                        strokeJoin = currentPathProperty.strokeJoin,
                                        eraseMode = currentPathProperty.eraseMode
                                    )
                                }

                                // Since new path is drawn no need to store paths to undone
                                pathsUndone.clear()

                                // If we leave this state at MotionEvent.Up it causes current path to draw
                                // line from (0,0) if this composable recomposes when draw mode is changed
                                currentPosition = Offset.Unspecified
                                previousPosition = currentPosition
                                motionEvent = MotionEvent.Idle
                            }

                            else -> Unit
                        }

                        with(drawContext.canvas.nativeCanvas) {
                            val checkPoint = saveLayer(null, null)
                            paths.forEach {
                                val path = it.path
                                val property = it.properties

                                if (!property.eraseMode) {
                                    drawPath(
                                        color = property.color,
                                        path = path.getPath(),
                                        style = Stroke(
                                            width = property.strokeWidth,
                                            cap = property.strokeCap,
                                            join = property.strokeJoin
                                        )
                                    )
                                } else {
                                    // Source
                                    drawPath(
                                        color = Color.Transparent,
                                        path = path.getPath(),
                                        style = Stroke(
                                            width = currentPathProperty.strokeWidth,
                                            cap = currentPathProperty.strokeCap,
                                            join = currentPathProperty.strokeJoin
                                        ),
                                        blendMode = BlendMode.Clear
                                    )
                                }
                            }

                            if (motionEvent != MotionEvent.Idle) {
                                if (!currentPathProperty.eraseMode) {
                                    drawPath(
                                        color = currentPathProperty.color,
                                        path = currentPath.getPath(),
                                        style = Stroke(
                                            width = currentPathProperty.strokeWidth,
                                            cap = currentPathProperty.strokeCap,
                                            join = currentPathProperty.strokeJoin
                                        )
                                    )
                                } else {
                                    drawPath(
                                        color = Color.Transparent,
                                        path = currentPath.getPath(),
                                        style = Stroke(
                                            width = currentPathProperty.strokeWidth,
                                            cap = currentPathProperty.strokeCap,
                                            join = currentPathProperty.strokeJoin
                                        ),
                                        blendMode = BlendMode.Clear
                                    )
                                }
                            }
                            restoreToCount(checkPoint)
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                PastelGreen,
                                Color.Black.copy(0.9f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            onClick = {
                                scope.launch {
                                    val serialized = Gson().toJson(paths.toList())
                                    println("PATH: $serialized")
                                    viewModel.onEvent(EditNotesEvent.DrawingSaved(serialized))
                                }
                            }) {
                            Icon(
                                painterResource(R.drawable.vec_done),
                                //modifier = Modifier.background(Color.Black),
                                contentDescription = stringResource(R.string.save),
                                tint = Color.Black.copy(0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                PastelPink,
                                Color.Black.copy(0.9f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            onClick = {
                                scope.launch {
                                    if (paths.isNotEmpty()) {
                                        paths.clear()
                                        viewModel.onEvent(EditNotesEvent.DrawingRemoved)
                                    }
                                }
                            }) {
                            Icon(
                                painterResource(R.drawable.vec_delete),
                                //modifier = Modifier.background(Color.Black),
                                contentDescription = stringResource(R.string.delete),
                                tint = Color.Black.copy(0.9f)
                            )
                        }

                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                PastelOrange,
                                Color.Black.copy(0.9f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            onClick = {
                                scope.launch {
                                    if (paths.isNotEmpty()) {

                                        val lastItem = paths.last()
                                        val lastPath = lastItem.path
                                        val lastPathProperty = lastItem.properties
                                        paths.remove(lastItem)

                                        pathsUndone.add(DrawingPath(lastPath, lastPathProperty))
                                    }
                                }
                            }) {
                            Icon(
                                painterResource(R.drawable.vec_undo),
                                //modifier = Modifier.background(Color.Black),
                                contentDescription = stringResource(R.string.undo),
                                tint = Color.Black.copy(0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}
