package org.notesapp.presentation.notes.list

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.notesapp.data.model.Note
import org.notesapp.data.repository.NotesRepository
import org.notesapp.domain.usecase.DeleteNoteUseCase
import org.notesapp.domain.usecase.GetNotesUseCase
import kotlin.test.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain

/**
 * FakeNotesRepository - emits configurable notes lists and remembers deletes for testing ViewModel state.
 */
class FakeNotesRepository(initialNotes: List<Note> = emptyList()) : NotesRepository {
  private val _notesFlow = MutableStateFlow(initialNotes)
  val updates: Flow<List<Note>> = _notesFlow.asStateFlow()
  var deletedIds = mutableListOf<Long>()
  override fun observeNotes(): Flow<List<Note>> = updates
  override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {}
  override suspend fun deleteNote(id: Long) {
    deletedIds.add(id)
    _notesFlow.value = _notesFlow.value.filterNot { it.id == id }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class NotesListViewModelTest {
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var fakeRepo: FakeNotesRepository
  private lateinit var getNotes: GetNotesUseCase
  private lateinit var deleteNote: DeleteNoteUseCase
  private lateinit var viewModel: NotesListViewModel

  private val testNotes = listOf(
    Note(id = 1L, title = "First Note", body = "Body 1", createdDateMillis = 12345L),
    Note(id = 2L, title = "Second Note", body = "Body 2", createdDateMillis = 67890L)
  )

  @BeforeTest
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    fakeRepo = FakeNotesRepository(testNotes)
    getNotes = GetNotesUseCase(fakeRepo)
    deleteNote = DeleteNoteUseCase(fakeRepo)
    viewModel = NotesListViewModel(getNotes, deleteNote)
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  /**
   * Verifies state is loading on init and transitions to loaded when notes emit.
   */
  @Test
  fun initial_state_reflects_loading_then_shows_notes() = runTest {
    val uiState = viewModel.state.value
    println("[TEST] initial_state_reflects_loading_then_shows_notes - Before: $uiState")
    advanceUntilIdle()
    val nextUiState = viewModel.state.value
    println("[TEST] initial_state_reflects_loading_then_shows_notes - After: $nextUiState")
    assertTrue(nextUiState.isLoading == false)
    assertEquals(testNotes, nextUiState.notes)
    assertNull(nextUiState.error)
  }

  /**
   * onDeleteClick populates dialog data in state
   */
  @Test
  fun onDeleteClick_sets_noteToDelete_and_shows_dialog() = runTest {
    val note = testNotes.first()
    viewModel.onDeleteClick(note)
    val state = viewModel.state.value
    assertTrue(state.showDeleteDialog)
    assertEquals(note, state.noteToDelete)
  }

  /**
   * onDismissDelete clears dialog data and noteToDelete
   */
  @Test
  fun onDismissDelete_hides_dialog_and_clears_noteToDelete() = runTest {
    val note = testNotes.first()
    viewModel.onDeleteClick(note)
    viewModel.onDismissDelete()
    val state = viewModel.state.value
    assertFalse(state.showDeleteDialog)
    assertNull(state.noteToDelete)
  }

  /**
   * confirmDelete removes the note and resets dialog state
   */
  @Test
  fun confirmDelete_removes_note_and_dialog_is_hidden() = runTest {
    val note = testNotes.first()
    viewModel.onDeleteClick(note)
    viewModel.confirmDelete()
    advanceUntilIdle()
    val state = viewModel.state.value
    assertFalse(state.showDeleteDialog)
    assertNull(state.noteToDelete)
    assertFalse(state.isDeleting)
    // The repository should have recorded a delete
    assertTrue(fakeRepo.deletedIds.contains(note.id))
    // The notes list should NOT contain the deleted note
    assertFalse(state.notes.any { it.id == note.id })
  }

  /**
   * Simulate an error from repository flow (by using a faulty repo)
   */
  @Test
  fun notes_flow_error_sets_error_state() = runTest {
    // Error test - simulate repository throws
    val errorRepo = object : NotesRepository {
      override fun observeNotes(): Flow<List<Note>> = kotlinx.coroutines.flow.flow {
        throw Exception("Fake error")
      }

      override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {}
      override suspend fun deleteNote(id: Long) {}
    }
    val errorViewModel =
      NotesListViewModel(GetNotesUseCase(errorRepo), DeleteNoteUseCase(errorRepo))
    val beforeState = errorViewModel.state.value
    println("[TEST] notes_flow_error_sets_error_state - Before: $beforeState")
    advanceUntilIdle()
    val afterState = errorViewModel.state.value
    println("[TEST] notes_flow_error_sets_error_state - After: $afterState")
    assertFalse(afterState.isLoading)
    assertTrue(afterState.error != null)
  }

  /**
   * Verifies UI state when repository emits an empty notes list
   */
  @Test
  fun emits_empty_notes_list_and_reflects_no_notes_state() = runTest {
    val emptyRepo = FakeNotesRepository(emptyList())
    val getEmptyNotes = GetNotesUseCase(emptyRepo)
    val deleteEmptyNote = DeleteNoteUseCase(emptyRepo)
    val viewModel = NotesListViewModel(getEmptyNotes, deleteEmptyNote)
    advanceUntilIdle()
    val state = viewModel.state.value
    assertTrue(state.isLoading == false)
    assertTrue(state.notes.isEmpty())
    assertFalse(state.hasNotes)
    assertNull(state.error)
  }

  /**
   * Loading state transitions from true to false as notes are updated multiple times(first empty & then with notes)
   */
  @Test
  fun loading_transitions_correctly_with_multiple_emissions() = runTest {
    val mockNotes = mutableListOf<List<Note>>()
    val notesFlow = MutableStateFlow<List<Note>>(emptyList())
    val repo = object : NotesRepository {
      override fun observeNotes(): Flow<List<Note>> = notesFlow
      override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {}
      override suspend fun deleteNote(id: Long) {}
    }
    val getNotesUseCase = GetNotesUseCase(repo)
    val deleteNoteUseCase = DeleteNoteUseCase(repo)
    val viewModel = NotesListViewModel(getNotesUseCase, deleteNoteUseCase)
    // Initially loading, then emits empty, then emits filled
    advanceUntilIdle()
    var state = viewModel.state.value
    assertTrue(state.isLoading == false)
    assertTrue(state.notes.isEmpty())
    // Second emission with notes
    notesFlow.value = testNotes
    advanceUntilIdle()
    state = viewModel.state.value
    assertFalse(state.isLoading)
    assertEquals(testNotes, state.notes)
    assertTrue(state.hasNotes)
    assertNull(state.error)
  }
}
