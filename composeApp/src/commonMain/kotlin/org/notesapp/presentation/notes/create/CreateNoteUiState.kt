package org.notesapp.presentation.notes.create

data class CreateNoteUiState(
  val title: String = "",
  val body: String = "",
  val selectedDateMillis: Long = 0L,
  val titleError: String? = null,
  val bodyError: String? = null,
  val isHtmlValid: Boolean = true,
  val isSaving: Boolean = false,
  val saveSuccess: Boolean = false,
) {
  val canSave: Boolean
    get() =
      title.isNotBlank() && body.isNotBlank() && isHtmlValid && titleError == null && bodyError == null
}
