package org.notesapp.presentation.notes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.notesapp.data.model.Note
import org.notesapp.domain.usecase.DeleteNoteUseCase
import org.notesapp.domain.usecase.GetNotesUseCase

class NotesListViewModel(
  private val getNotes: GetNotesUseCase,
  private val deleteNote: DeleteNoteUseCase,
) : ViewModel() {

  private val _state = MutableStateFlow(NotesListUiState(isLoading = true))
  val state: StateFlow<NotesListUiState> = _state.asStateFlow()

  init {
    viewModelScope.launch {
      try {
        getNotes().collect { notes ->
          _state.update { it.copy(isLoading = false, notes = notes, error = null) }
        }
      } catch (e: Exception) {
        _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
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
    viewModelScope.launch {
      _state.update { it.copy(isDeleting = true) }
      deleteNote(note.id)
      _state.update { it.copy(isDeleting = false, showDeleteDialog = false, noteToDelete = null) }
    }
  }
}