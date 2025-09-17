package org.notesapp.presentation.notes.list

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.notesapp.data.model.Note
import org.notesapp.data.repository.NotesRepository
import org.notesapp.domain.usecase.DeleteNoteUseCase
import org.notesapp.domain.usecase.GetNotesUseCase
import kotlin.test.Test

class FakeNotesRepository(private val notes: List<Note>) : NotesRepository {
  private val _notesFlow = MutableStateFlow(notes)
  override fun observeNotes(): Flow<List<Note>> = _notesFlow
  override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {}
  override suspend fun deleteNote(id: Long) {
    _notesFlow.value = _notesFlow.value.filterNot { it.id == id }
  }
}

@OptIn(ExperimentalTestApi::class)
class NotesListScreenTest {
  @Test
  fun showsEmptyState_whenNoNotes() = runComposeUiTest {
    val fakeRepo = FakeNotesRepository(emptyList())
    val getNotes = GetNotesUseCase(fakeRepo)
    val deleteNote = DeleteNoteUseCase(fakeRepo)
    val viewModel = NotesListViewModel(getNotes, deleteNote)
    setContent {
      NotesListScreen(
        viewModel = viewModel,
        onAddClick = {},
        onJsMessage = {},
        modifier = Modifier
      )
    }
    onRoot().printToLog("UI-tree-empty-state")
    onNodeWithText("No notes yet. Create your first note!", substring = true).assertIsDisplayed()
    onNodeWithContentDescription("No notes image").assertIsDisplayed()
  }

  @Test
  fun showsNotesList_whenNotesAvailable() = runComposeUiTest {
    val notes = listOf(
      Note(id = 1L, title = "Test Note", body = "Test Body", createdDateMillis = 1000L),
      Note(id = 2L, title = "Second Note", body = "Second Body", createdDateMillis = 5678L)
    )
    val fakeRepo = FakeNotesRepository(notes)
    val getNotes = GetNotesUseCase(fakeRepo)
    val deleteNote = DeleteNoteUseCase(fakeRepo)
    val viewModel = NotesListViewModel(getNotes, deleteNote)
    setContent {
      NotesListScreen(
        viewModel = viewModel,
        onAddClick = {},
        onJsMessage = {},
        modifier = Modifier
      )
    }
    onRoot().printToLog("UI-tree-filled-state")
    onNodeWithText("Test Note", substring = true).assertIsDisplayed()
    onNodeWithText("Second Note", substring = true).assertIsDisplayed()
  }

  @Test
  fun deletingItem_removesItFromNotesList() = runComposeUiTest {
    val notes = listOf(
      Note(id = 1L, title = "Alpha Note", body = "Alpha Body", createdDateMillis = 1111L),
      Note(id = 2L, title = "Beta Note", body = "Beta Body", createdDateMillis = 2222L)
    )
    val fakeRepo = FakeNotesRepository(notes)
    val getNotes = GetNotesUseCase(fakeRepo)
    val deleteNote = DeleteNoteUseCase(fakeRepo)
    val viewModel = NotesListViewModel(getNotes, deleteNote)

    setContent {
      NotesListScreen(
        viewModel = viewModel,
        onAddClick = {},
        onJsMessage = {},
        modifier = Modifier,
      )
    }
    // Initially both notes should be visible
    onNodeWithText("Alpha Note", substring = true).assertIsDisplayed()
    onNodeWithText("Beta Note", substring = true).assertIsDisplayed()

    // Simulate delete of "Beta Note"
    viewModel.onDeleteClick(notes[1])
    viewModel.confirmDelete()

    // After deletion, "Beta Note" should NOT be visible, but "Alpha Note" remains
    onNodeWithText("Alpha Note", substring = true).assertIsDisplayed()
    onNodeWithText("Beta Note", substring = true).assertDoesNotExist()
  }
}
