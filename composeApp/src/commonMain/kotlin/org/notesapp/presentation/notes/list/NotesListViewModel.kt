package org.notesapp.presentation.notes.list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.notesapp.data.model.Note
import org.notesapp.domain.usecase.DeleteNoteUseCase
import org.notesapp.domain.usecase.GetNotesUseCase

class NotesListViewModel(
  private val getNotes: GetNotesUseCase,
  private val deleteNote: DeleteNoteUseCase,
): ViewModel(), KoinComponent {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private val _state = MutableStateFlow(NotesListUiState(isLoading = true))
  val state: StateFlow<NotesListUiState> = _state.asStateFlow()

  init {
    scope.launch {
      getNotes().collect { notes ->
        _state.update { it.copy(isLoading = false, notes = notes, error = null) }
      }
    }
  }

  fun onDeleteClick(note: Note) {
    _state.update { it.copy(showDeleteDialog = true, noteToDelete = note) }
  }

  fun onDismissDelete() {
    _state.update { it.copy(showDeleteDialog = false, noteToDelete = null) }
  }

  fun confirmDelete() {
    val note = _state.value.noteToDelete ?: return
    scope.launch {
      _state.update { it.copy(isDeleting = true) }
      deleteNote(note.id)
      _state.update { it.copy(isDeleting = false, showDeleteDialog = false, noteToDelete = null) }
    }
  }
}