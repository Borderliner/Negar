package meshki.studio.negarname.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.R
import meshki.studio.negarname.entity.ScreenEntity
import meshki.studio.negarname.ui.element.ActionButton
import meshki.studio.negarname.util.LeftToRightLayout
import meshki.studio.negarname.util.RightToLeftLayout

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesScreen(navigateTo: (route: String) -> Unit) {
    LeftToRightLayout {
        Scaffold(
            floatingActionButton = {
                ActionButton(
                    text = stringResource(R.string.write),
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.vec_ink_pen),
                            contentDescription = "Add Note"
                        )
                    },
                    modifier = Modifier
                        .imePadding()
                        .offset((-10).dp, (40).dp),
                    onClick = {
                        navigateTo(ScreenEntity.EditNotes.route)
                    },
                )
            },
            floatingActionButtonPosition = FabPosition.End,
        ) {
            RightToLeftLayout {
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
//                if (uiState.notes.isNotEmpty()) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .graphicsLayer {
//                                if (filterAnimation.value > 0f) {
//                                    translationY = filterAnimation.value
//                                } else if (searchAnimation.value > 0f) {
//                                    translationY = searchAnimation.value
//                                }
//                            },
//                        contentPadding = PaddingValues(bottom = 65.dp)
//                    ) {
//                        items(uiState.notes) { note ->
//                            SingleNote(
//                                note = note,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        navController.navigate(
//                                            ScreenItem.ModifyNote.route +
//                                                    "?noteId=${note.id}&noteColor=${note.color}"
//                                        )
//                                    },
//                                onDelete = {
//                                    notesViewModel.onEvent(NotesEvent.DeleteNote(note))
//                                    scope.launch {
//                                        val result =
//                                            snackbarHostState.showSnackbar(
//                                                message = "یادداشت پاک شد",
//                                                actionLabel = "بازیابی"
//                                            )
//                                        if (result == SnackbarResult.ActionPerformed) {
//                                            notesViewModel.onEvent(NotesEvent.RestoreNote)
//                                        }
//                                    }
//                                },
//                                onPin = {
//                                    if (!note.pinned) {
//                                        scope.launch {
//                                            snackbarHostState.showSnackbar(
//                                                message = "یادداشت سوزن شد.",
//                                            )
//                                        }
//                                    }
//
//                                    notesViewModel.onEvent(NotesEvent.TogglePinNote(note))
//                                }
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                        }
//                    }
//                } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.no_notes))
                        }
                        //}
                    }
                }
            }
        }
    }
}
