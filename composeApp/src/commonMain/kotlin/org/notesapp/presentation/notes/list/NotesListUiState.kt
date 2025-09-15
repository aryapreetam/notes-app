package org.notesapp.presentation.notes.list

import org.notesapp.data.model.Note

data class NotesListUiState(
  val notes: List<Note> = emptyList(),
  val isLoading: Boolean = false,
  val isDeleting: Boolean = false,
  val error: String? = null,
  val showDeleteDialog: Boolean = false,
  val noteToDelete: Note? = null,
) {
  val hasNotes: Boolean get() = notes.isNotEmpty()
}
