package org.notesapp.presentation.notes.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.notesapp.data.model.Note
import org.notesapp.presentation.components.NoteCard
import org.notesapp.theme.LocalThemeIsDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
  onAddClick: () -> Unit,
  onJsMessage: (String) -> Unit,
  onViewPdf: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val viewModel = koinViewModel<NotesListViewModel>()
  val state by viewModel.state.collectAsState()

  // Theme state from CompositionLocal
  val isDarkState = LocalThemeIsDark.current
  val isDark by isDarkState

  var menuExpanded by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("My Notes", fontWeight = FontWeight.Bold) },
        actions = {
          IconButton(onClick = onAddClick) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Note"
            )
          }
          Box {
            IconButton(onClick = { menuExpanded = true }) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
              )
            }
            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false }
            ) {
              DropdownMenuItem(
                text = { Text("Toggle Theme") },
                leadingIcon = {
                  Icon(
                    imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = if (isDark) "Switch to light theme" else "Switch to dark theme"
                  )
                },
                onClick = {
                  isDarkState.value = !isDark
                  menuExpanded = false
                }
              )
              DropdownMenuItem(
                text = { Text("View PDF") },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Filled.PictureAsPdf,
                    contentDescription = "View PDF"
                  )
                },
                onClick = {
                  menuExpanded = false
                  onViewPdf()
                }
              )
            }
          }
        }
      )
    },
    content = { innerPadding ->
      Box(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when {
          state.isLoading -> {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
          }

          state.error != null -> {
            Text(
              text = state.error ?: "Error",
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.align(Alignment.Center)
            )
          }

          !state.hasNotes -> {
            Text(
              text = "No notes yet. Create your first note!",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.align(Alignment.Center)
            )
          }

          else -> {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              items(state.notes) { note: Note ->
                NoteCard(
                  note = note,
                  onDeleteClick = { viewModel.onDeleteClick(note) },
                  onCardClick = { /* No-op for now */ },
                  onJsMessage = onJsMessage,
                )
              }
            }
          }
        }
        if (state.showDeleteDialog && state.noteToDelete != null) {
          AlertDialog(
            onDismissRequest = viewModel::onDismissDelete,
            title = { Text("Delete note?") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
              TextButton(
                onClick = { viewModel.confirmDelete() },
                enabled = !state.isDeleting
              ) {
                if (state.isDeleting) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Delete", color = MaterialTheme.colorScheme.error)
              }
            },
            dismissButton = {
              TextButton(onClick = viewModel::onDismissDelete) {
                Text("Cancel")
              }
            }
          )
        }
      }
    }
  )
}
