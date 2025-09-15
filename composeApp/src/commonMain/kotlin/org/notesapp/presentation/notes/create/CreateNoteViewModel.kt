package org.notesapp.presentation.notes.create

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.notesapp.domain.usecase.CreateNoteUseCase
import org.notesapp.domain.usecase.ValidateHtmlUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateNoteViewModel(
  private val createNote: CreateNoteUseCase,
  private val validateHtml: ValidateHtmlUseCase,
) : ViewModel(), KoinComponent {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

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
    val result = validateHtml(value)
    _state.update { it.copy(body = value, bodyError = result.error, isHtmlValid = result.isValid) }
  }

  fun onDateChange(millis: Long) {
    _state.update { it.copy(selectedDateMillis = millis) }
  }

  fun save() {
    val s = _state.value
    if (!s.canSave) return
    scope.launch {
      _state.update { it.copy(isSaving = true) }
      // Perform background work on IO dispatcher
      withContext(Dispatchers.IO) {
        createNote(s.title.trim(), s.body.trim(), s.selectedDateMillis)
      }
      // Update UI state and call navigation callback on main thread
      _state.update { it.copy(isSaving = false, saveSuccess = true) }
    }
  }
}