package org.notesapp.presentation.notes.create

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.notesapp.data.model.Note
import org.notesapp.data.repository.NotesRepository
import org.notesapp.domain.usecase.CreateNoteUseCase
import org.notesapp.domain.usecase.GetNotesUseCase
import org.notesapp.domain.usecase.DeleteNoteUseCase
import org.notesapp.presentation.notes.list.NotesListScreen
import org.notesapp.presentation.notes.list.NotesListViewModel
import org.notesapp.utils.HtmlValidator
import kotlin.test.Test

class SharedFakeNotesRepository : NotesRepository {
  private val _notesFlow = MutableStateFlow<List<Note>>(emptyList())
  override fun observeNotes(): Flow<List<Note>> = _notesFlow
  override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {
    val newId = (_notesFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
    val note = Note(id = newId, title = title, body = body, createdDateMillis = createdDateMillis)
    _notesFlow.value = _notesFlow.value + note
  }

  override suspend fun deleteNote(id: Long) {}
}

@OptIn(ExperimentalTestApi::class)
class CreateNoteScreenTest {
  @Test
  fun showsInitialEmptyUi() = runComposeUiTest {
    val fakeRepo = SharedFakeNotesRepository()
    val createNote = CreateNoteUseCase(fakeRepo)
    val viewModel = CreateNoteViewModel(createNote)
    setContent {
      CreateNoteScreen(
        viewModel = viewModel,
        onBackClick = {},
        onDatePickRequest = {},
        onSaved = {}
      )
    }
    onNodeWithText("Title").assertIsDisplayed()
    onNodeWithText("Content (HTML)").assertIsDisplayed()
    onNodeWithText("Save Note").assertIsDisplayed()
  }

  @Test
  fun creatingNote_showsInNotesListScreen() = runComposeUiTest {
    val sharedRepo = SharedFakeNotesRepository()
    val createNote = CreateNoteUseCase(sharedRepo)
    val createNoteViewModel = CreateNoteViewModel(createNote)
    val getNotes = GetNotesUseCase(sharedRepo)
    val deleteNote = DeleteNoteUseCase(sharedRepo)
    val notesListViewModel = NotesListViewModel(getNotes, deleteNote)

    // Simulate creating a note
    createNoteViewModel.onTitleChange("Created from Test")
    createNoteViewModel.onBodyChange("Some HTML content")
    createNoteViewModel.save()
    // Wait for coroutine, in Compose UI test this is generally synched

    setContent {
      NotesListScreen(
        viewModel = notesListViewModel,
        onAddClick = {},
        onJsMessage = {},
        modifier = Modifier
      )
    }
    // Should display the created note
    onNodeWithText("Created from Test", substring = true).assertIsDisplayed()
  }
}
