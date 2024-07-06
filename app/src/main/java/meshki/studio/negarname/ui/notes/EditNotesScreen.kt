package meshki.studio.negarname.ui.notes

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.infiniteLinearGradient
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.entities.CustomPath
import meshki.studio.negarname.entities.DrawMode
import meshki.studio.negarname.entities.DrawingPath
import meshki.studio.negarname.entities.MotionEvent
import meshki.studio.negarname.entities.PathProperties
import meshki.studio.negarname.entities.Tool
import meshki.studio.negarname.services.alarm.AlarmData
import meshki.studio.negarname.services.voice_recorder.VoiceRecorder
import meshki.studio.negarname.ui.app.AppEvent
import meshki.studio.negarname.ui.app.AppViewModel
import meshki.studio.negarname.ui.calendar.Week
import meshki.studio.negarname.ui.components.ActionButton
import meshki.studio.negarname.ui.components.HintedTextField
import meshki.studio.negarname.ui.components.PopupSection
import meshki.studio.negarname.ui.components.Toolbox
import meshki.studio.negarname.ui.notes.entities.EditNotesEvent
import meshki.studio.negarname.ui.notes.entities.NoteEntity
import meshki.studio.negarname.ui.notes.vm.EditNotesViewModel
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.theme.PastelLavender
import meshki.studio.negarname.ui.theme.PastelLime
import meshki.studio.negarname.ui.theme.PastelOrange
import meshki.studio.negarname.ui.theme.PastelPink
import meshki.studio.negarname.ui.theme.PastelRed
import meshki.studio.negarname.ui.util.BackPressHandler
import meshki.studio.negarname.ui.util.LeftToRightLayout
import meshki.studio.negarname.ui.util.RightToLeftLayout
import meshki.studio.negarname.ui.util.checkAlarmsPermission
import meshki.studio.negarname.ui.util.checkPermission
import meshki.studio.negarname.ui.util.checkPermissions
import meshki.studio.negarname.ui.util.extensions.dragMotionEvent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import java.text.NumberFormat
import java.util.Calendar

@Composable
fun EditNotesScreen(navController: NavHostController, color: Int) {
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
    appViewModel.onEvent(AppEvent.SetBottomBarVisible(false))
    val vm = koinViewModel<EditNotesViewModel>()

    LaunchedEffect(color) {
        if (color > 0) {
            vm.viewModelScope.launch {
                vm.onEvent(EditNotesEvent.ColorChanged(color))
            }
        }
    }
    if (appState.value.isRtl) {
        EditNotesScreenScaffold(navController) {
            RightToLeftLayout {
                EditNotesScreenMain(navController)
            }
        }
    } else {
        EditNotesScreenScaffold(navController) {
            LeftToRightLayout {
                EditNotesScreenMain(navController)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditNotesScreenScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
    val vm = koinViewModel<EditNotesViewModel>()

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
                text = stringResource(R.string.save),
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.vec_done),
                        contentDescription = stringResource(R.string.save)
                    )
                },
                modifier = Modifier,
                onClick = {
                    vm.viewModelScope.launch {
                        vm.onEvent(EditNotesEvent.NoteSaved)
                        vm.setNoteModified(false)
                        navController.navigateUp()
                    }
                },
                isBottomBarVisible = appState.value.isBottomBarVisible
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
    navController: NavHostController
) {
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
    val scope = rememberCoroutineScope()
    val vm = koinViewModel<EditNotesViewModel>()

    val noteState = vm.noteEntityState
    val noteEntityColorsArgb = NoteEntity.colors.map {
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

    val titleState = vm.noteTitleState
    val textState = vm.noteTextState

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

    BackPressHandler {
        if ((titleState.value.text.isNotEmpty() || textState.value.text.isNotEmpty()) && vm.isNoteModified.value) {
            workInProgressAlertVisible = true
        } else {
            navController.navigateUp()
        }
    }

    if (workInProgressAlertVisible) {
        IncompleteNoteAlert(
            onDismissRequest = { workInProgressAlertVisible = false },
            onCancel = {
                workInProgressAlertVisible = false
                navController.navigateUp()
            },
            onConfirm = {
                workInProgressAlertVisible = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(noteState.value.color)
                    .copy(
                        alpha =
                        if (
                            (appState.value.theme == "system" && isSystemInDarkTheme()) || appState.value.theme == "dark"
                        ) 0.125f
                        else 0.25f
                    )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    end = 12.dp,
                    bottom = 0.dp,
                    start = 12.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            if (vm.alarmEntities.size > 0) PastelLavender else {
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
                            if (vm.voiceState.value.duration > 0)
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

            Row {
                val dateModified = remember { derivedStateOf {
                    val pDate = PersianDate(noteState.value.dateModified)
                    if (appState.value.isRtl) {
                        "${pDate.dayName()}، ${pDate.hour.toString().padStart(2, '0')}:${pDate.minute.toString().padStart(2, '0')}:${pDate.second.toString().padStart(2, '0')}\n${pDate.shDay} ${pDate.monthName} ${pDate.shYear}"
                    } else {
                        "${pDate.dayEnglishName()}, ${pDate.hour.toString().padStart(2, '0')}:${pDate.minute.toString().padStart(2, '0')}:${pDate.second.toString().padStart(2, '0')}\n${pDate.grgDay} ${pDate.grgMonthName} ${pDate.grgYear}"
                    }
                } }

                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = dateModified.value,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            //.offset(0.dp, offsetAnimation.value.value.dp)
        ) {
            HintedTextField(
                modifier = Modifier.padding(8.dp),
                text = titleState.value.text,
                hint = stringResource(R.string.title),
                color = Color(noteState.value.color),
                hintColor = MaterialTheme.colorScheme.onBackground.copy(0.55f),
                onValueChange = {
                    scope.launch {
                        vm.onEvent(EditNotesEvent.TitleEntered(it))
                        vm.setNoteModified(true)
                    }
                },
                onFocusChange = {
                    scope.launch {
                        vm.onEvent(EditNotesEvent.TitleFocusChanged(it))
                    }
                },
                isHintVisible = titleState.value.isHintVisible,
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                textStyle = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onBackground)
            )

            Divider (
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(0.97f)
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

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
                text = textState.value.text,
                hint = stringResource(R.string.note_text_hint),
                color = Color(noteState.value.color),
                hintColor = MaterialTheme.colorScheme.onBackground.copy(0.55f),
                onValueChange = {
                    scope.launch {
                        vm.onEvent(EditNotesEvent.TextEntered(it))
                        vm.setNoteModified(true)
                    }
                },
                onFocusChange = {
                    scope.launch {
                        vm.onEvent(EditNotesEvent.TextFocusChanged(it))
                    }
                },
                expanded = true,
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                isHintVisible = textState.value.isHintVisible,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.None
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onBackground),
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
                        colorPaletteState.animateScrollToItem(noteEntityColorsArgb.indexOf(noteState.value.color))
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
                items(NoteEntity.colors) {
                    val colorInt = it.toArgb()
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(15.dp, CircleShape)
                            .clip(CircleShape)
                            .background(it)
                            .clickable {
                                scope.launch {
                                    vm.onEvent(
                                        EditNotesEvent.ColorChanged(colorInt)
                                    )
                                }
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
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontSize = 28.sp,
                        text = "${
                            if (vm.voiceState.value.duration / 1000 / 60 < 10) formatter.format(
                                0
                            ) else ""
                        }${formatter.format((vm.voiceState.value.duration / 1000) / 60)}:${
                            if (vm.voiceState.value.duration / 1000 % 60 < 10) formatter.format(
                                0
                            ) else ""
                        }${
                            formatter.format(
                                (vm.voiceState.value.duration / 1000) % 60
                            )
                        }"
                    )
                    var waveformProgress by remember { mutableStateOf(0F) }

                    val animatedGradientBrush = Brush.infiniteLinearGradient(
                        colors = listOf(
                            Color(noteState.value.color),
                            MaterialTheme.colorScheme.primary
                        ),
                        animation = tween(durationMillis = 6000, easing = LinearEasing),
                        width = 128F
                    )

                    if (vm.voiceState.value.duration > 0) {
                        AudioWaveform(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            style = Fill,
                            waveformAlignment = WaveformAlignment.Center,
                            amplitudeType = AmplitudeType.Avg,
                            amplitudes = vm.voiceState.value.amplitudes,
                            progressBrush = animatedGradientBrush,
                            waveformBrush = SolidColor(Color.LightGray),
                            spikeWidth = 4.dp,
                            spikePadding = 2.dp,
                            spikeRadius = 4.dp,
                            progress = waveformProgress,
                            onProgressChange = {
                                waveformProgress = it
                                vm.recorder.value.seekTo((it * vm.voiceState.value.duration).toInt())
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                    Icon(
                                        Icons.Filled.ExitToApp,
                                        modifier = Modifier.size(48.dp),
                                        contentDescription = stringResource(R.string.record)
                                    )
                                },
                                title = {
                                    Text(text = stringResource(R.string.voice_replace_title), style = MaterialTheme.typography.titleLarge)
                                },
                                text = {
                                    Text(text = stringResource(R.string.voice_replace_text), style = MaterialTheme.typography.bodyLarge)
                                },
                                onDismissRequest = {
                                    replaceRecordAlert = false
                                },
                                confirmButton = {
                                    TextButton(
                                        modifier = Modifier.padding(horizontal = 2.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PastelRed,
                                            contentColor = Color.Black
                                        ),
                                        onClick = {
                                            replaceRecordAlert = false
                                            vm.recorder.value.startRecording()
                                            isRecording = vm.recorder.value.isRecording()
                                        }
                                    ) {
                                        Text(stringResource(R.string.yes), fontSize = 14.sp)
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        modifier = Modifier.padding(horizontal = 2.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PastelGreen,
                                            contentColor = Color.Black
                                        ),
                                        onClick = {
                                            replaceRecordAlert = false
                                        }
                                    ) {
                                        Text(stringResource(R.string.no), fontSize = 14.sp)
                                    }
                                }
                            )
                        }

                        if (deleteRecordAlert) {
                            AlertDialog(
                                icon = {
                                    Icon(
                                        Icons.Filled.Delete,
                                        modifier = Modifier.size(48.dp),
                                        contentDescription = stringResource(R.string.record)
                                    )
                                },
                                title = {
                                    Text(text = stringResource(R.string.voice_delete_title), style = MaterialTheme.typography.titleLarge)
                                },
                                text = {
                                    Text(text = stringResource(R.string.voice_delete_text), style = MaterialTheme.typography.bodyLarge)
                                },
                                onDismissRequest = {
                                    deleteRecordAlert = false
                                },
                                confirmButton = {
                                    TextButton(
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PastelRed,
                                            contentColor = Color.Black
                                        ),
                                        onClick = {
                                            scope.launch {
                                                deleteRecordAlert = false
                                                vm.onEvent(EditNotesEvent.VoiceRemoved)
                                            }
                                        }
                                    ) {
                                        Text(stringResource(R.string.yes))
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PastelGreen,
                                            contentColor = Color.Black
                                        ),
                                        onClick = {
                                            deleteRecordAlert = false
                                        }
                                    ) {
                                        Text(stringResource(R.string.no))
                                    }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ElevatedButton(
                                colors = ButtonDefaults.elevatedButtonColors(
                                    PastelOrange,
                                    Color.Black.copy(0.9f)
                                ),
                                enabled = vm.voiceState.value.duration > 0,
                                elevation = ButtonDefaults.buttonElevation(4.dp),
                                onClick = { deleteRecordAlert = true }) {
                                Icon(
                                    painterResource(R.drawable.vec_delete),
                                    //modifier = Modifier.background(Color.Black),
                                    contentDescription = stringResource(R.string.delete),
                                    tint = Color.Black.copy(0.9f)
                                )
                                Text(
                                    text = stringResource(R.string.delete)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
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

                                                if (!vm.recorder.value.isRecording()) {
                                                    if (vm.voiceState.value.duration > 0) {
                                                        replaceRecordAlert = true
                                                    } else {
                                                        vm.recorder.value.startRecording()
                                                    }
                                                } else {
                                                    vm.recorder.value.stopRecording()
                                                    scope.launch {
                                                        vm.readVoiceToState(
                                                            "records/${noteState.value.id}.aac"
                                                        ).collectLatest {
                                                            vm.setVoiceState(it)
                                                            Timber.tag("Edit Screen").i(it.toString())
                                                        }
                                                    }
                                                }

                                                isRecording = vm.recorder.value.isRecording()
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
                                    enabled = vm.voiceState.value.duration > 0,
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
                            if (vm.alarmEntities.size > 0) {
                                val tempCal = Calendar.getInstance().apply {
                                    timeInMillis = vm.alarmEntities[0].time
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

                    var repeating by remember { mutableStateOf(vm.alarmEntities.size > 1) }
                    var critical by remember {
                        mutableStateOf(
                            if (vm.alarmEntities.size > 0) {
                                vm.alarmEntities[0].critical
                            } else false
                        )
                    }
                    val week by remember { mutableStateOf(Week.fromAlarms(vm.alarmEntities)) }

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
                            -it / 8
                        }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = {
                            -it / 8
                        }),
                    ) {
                        Row(
                            modifier = Modifier.padding(top = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val sliceRange = remember {
                                if (appState.value.isRtl) mutableStateOf(IntRange(0, 0))
                                else mutableStateOf(IntRange(0, 2))
                            }

                            week.list.forEachIndexed { idx, item ->
                                var checked by remember { mutableStateOf(week.list[idx].value) }
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(item.name.slice(sliceRange.value))
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
                        if (vm.alarmEntities.size > 0) {
                            ElevatedButton(
                                colors = ButtonDefaults.elevatedButtonColors(
                                    PastelRed,
                                    Color.Black.copy(0.9f)
                                ),
                                elevation = ButtonDefaults.buttonElevation(4.dp),
                                onClick = {
                                    scope.launch {
                                        vm.onEvent(EditNotesEvent.DeleteNoteAlarms(noteState.value.id))
                                        vm.alarmEntities.clear()
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

                                            scope.launch {
                                                vm.onEvent(
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

                    val configuration = LocalConfiguration.current
                    val screenHeight = configuration.screenHeightDp.dp

                    val drawModifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .shadow(1.dp)
                        .height(screenHeight / 1.9f)
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
                                    vm.onEvent(EditNotesEvent.DrawingSaved(serialized))
                                    closeTool(drawingTool)
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
                                        vm.onEvent(EditNotesEvent.DrawingRemoved)
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
