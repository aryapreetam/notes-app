package org.notesapp.presentation.notes.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.notesapp.data.model.Note
import org.notesapp.presentation.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
  onAddClick: () -> Unit
) {

  val viewModel = koinViewModel<NotesListViewModel>()

  val state by viewModel.state.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Notes") },
        actions = {
          IconButton(onClick = onAddClick) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Note"
            )
          }
        }
      )
    },
    content = { innerPadding ->
      Box(
        modifier = Modifier
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
                )
              }
            }
          }
        }
        // Delete confirmation dialog
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
