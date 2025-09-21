package org.notesapp.presentation.notes.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.notesapp.domain.usecase.CreateNoteUseCase
import org.notesapp.utils.HtmlValidator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateNoteViewModel(
  private val createNote: CreateNoteUseCase,
) : ViewModel() {

  @OptIn(ExperimentalTime::class)
  private val _state = MutableStateFlow(
    CreateNoteUiState(
      selectedDateMillis = Clock.System.now().toEpochMilliseconds()
    )
  )
  val state: StateFlow<CreateNoteUiState> = _state.asStateFlow()

  fun onTitleChange(value: String) {
    val title = value.trimStart()
    val error = when {
      title.isBlank() -> "Title is required"
      title.length > 120 -> "Title too long"
      else -> null
    }
    _state.update { it.copy(title = title, titleError = error) }
  }

  fun onBodyChange(value: String) {
    val result = HtmlValidator.validate(value)
    _state.update { it.copy(body = value, bodyError = result.error, isHtmlValid = result.isValid) }
  }

  fun onDateChange(millis: Long) {
    _state.update { it.copy(selectedDateMillis = millis) }
  }

  fun save() {
    val s = _state.value
    if (!s.canSave) return
    viewModelScope.launch {
      _state.update { it.copy(isSaving = true) }
      createNote(s.title.trim(), s.body.trim(), s.selectedDateMillis)
      _state.update { it.copy(isSaving = false, saveSuccess = true) }
    }
  }
}