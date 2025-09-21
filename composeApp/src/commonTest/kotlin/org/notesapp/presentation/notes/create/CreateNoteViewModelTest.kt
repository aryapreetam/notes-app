package org.notesapp.presentation.notes.create

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.notesapp.data.repository.NotesRepository
import kotlin.test.*
import org.notesapp.domain.usecase.CreateNoteUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FakeNotesRepository : NotesRepository {
  val createdNotes = mutableListOf<Triple<String, String, Long>>()
  override fun observeNotes() = throw UnsupportedOperationException()
  override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {
    createdNotes.add(Triple(title, body, createdDateMillis))
  }

  override suspend fun deleteNote(id: Long) = throw UnsupportedOperationException()
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class CreateNoteViewModelTest {
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var fakeRepo: FakeNotesRepository
  private lateinit var createNoteUseCase: CreateNoteUseCase
  private lateinit var viewModel: CreateNoteViewModel

  @BeforeTest
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    fakeRepo = FakeNotesRepository()
    createNoteUseCase = CreateNoteUseCase(fakeRepo)
    viewModel = CreateNoteViewModel(createNoteUseCase)
  }

  @AfterTest
  fun teardown() {
    Dispatchers.resetMain()
  }

  @Test
  fun initial_state() = runTest {
    val state = viewModel.state.value
    assertEquals("", state.title)
    assertEquals("", state.body)
    assertNull(state.titleError)
    assertNull(state.bodyError)
    assertFalse(state.isSaving)
    assertFalse(state.saveSuccess)
    assertFalse(state.canSave)
  }

  @Test
  fun title_validation_blank() = runTest {
    viewModel.onTitleChange("")
    val state = viewModel.state.value
    assertEquals("Title is required", state.titleError)
    assertFalse(state.canSave)
  }

  @Test
  fun title_validation_too_long() = runTest {
    val longTitle = "a".repeat(125)
    viewModel.onTitleChange(longTitle)
    val state = viewModel.state.value
    assertEquals("Title too long", state.titleError)
    assertFalse(state.canSave)
  }

  @Test
  fun title_valid() = runTest {
    viewModel.onTitleChange("Valid Title")
    val state = viewModel.state.value
    assertNull(state.titleError)
  }

  @Test
  fun body_validation_blank() = runTest {
    viewModel.onBodyChange("")
    val state = viewModel.state.value
    assertNotNull(state.bodyError)
    assertFalse(state.isHtmlValid)
    assertFalse(state.canSave)
  }

  @Test
  fun body_validation_invalid_html() = runTest {
    viewModel.onBodyChange("<xyz>")
    val state = viewModel.state.value
    assertNotNull(state.bodyError)
    assertFalse(state.isHtmlValid)
    assertFalse(state.canSave)
  }

  @Test
  fun body_validation_valid_html() = runTest {
    viewModel.onBodyChange("<p>Valid Body</p>")
    val state = viewModel.state.value
    assertNull(state.bodyError)
    assertTrue(state.isHtmlValid)
  }

  @Test
  fun date_change_propagates() = runTest {
    val newDate = Clock.System.now().toEpochMilliseconds() + 12345L
    viewModel.onDateChange(newDate)
    val state = viewModel.state.value
    assertEquals(newDate, state.selectedDateMillis)
  }

  @Test
  fun canSave_requires_valid_title_and_body() = runTest {
    // Invalid title
    viewModel.onTitleChange("")
    viewModel.onBodyChange("body")
    assertFalse(viewModel.state.value.canSave)
    // Valid title, invalid body
    viewModel.onTitleChange("Title")
    viewModel.onBodyChange("")
    assertFalse(viewModel.state.value.canSave)
    // Valid both
    viewModel.onTitleChange("Title")
    viewModel.onBodyChange("Body")
    assertTrue(viewModel.state.value.canSave)
  }

  @Test
  fun save_successful() = runTest {
    viewModel.onTitleChange("Title")
    viewModel.onBodyChange("Body")
    viewModel.onDateChange(123L)
    println("BEFORE save: ${viewModel.state.value}")
    viewModel.save()
    println("AFTER save, before runCurrent: ${viewModel.state.value}")
    testDispatcher.scheduler.runCurrent()
    println("AFTER runCurrent: ${viewModel.state.value}")
    advanceUntilIdle()
    println("AFTER advanceUntilIdle: ${viewModel.state.value}")
    val state = viewModel.state.value
    assertFalse(state.isSaving)
    assertTrue(state.saveSuccess)
    // created note recorded in repository
    assertEquals(listOf(Triple("Title", "Body", 123L)), fakeRepo.createdNotes)
  }

  @Test
  fun save_blocked_when_invalid() = runTest {
    viewModel.onTitleChange("")
    viewModel.onBodyChange("Body")
    viewModel.save()
    advanceUntilIdle()
    val state = viewModel.state.value
    assertFalse(state.isSaving)
    assertFalse(state.saveSuccess)
    assertTrue(fakeRepo.createdNotes.isEmpty())
  }

  @Test
  fun isSaving_transitions_during_save() = runTest {
    // Use a suspending repository
    val longRepo = object : NotesRepository {
      override fun observeNotes() = throw UnsupportedOperationException()
      override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {
        delay(100)
      }

      override suspend fun deleteNote(id: Long) = throw UnsupportedOperationException()
    }
    createNoteUseCase = CreateNoteUseCase(longRepo)
    viewModel = CreateNoteViewModel(createNoteUseCase)
    viewModel.onTitleChange("Title")
    viewModel.onBodyChange("Body")
    viewModel.onDateChange(123L)
    viewModel.save()
    // Run up to first suspension (delay)
    testDispatcher.scheduler.runCurrent()
    // Assert isSaving is true during the pending delay
    assertTrue(viewModel.state.value.isSaving)
    // Complete the delay
    testDispatcher.scheduler.advanceTimeBy(100)
    advanceUntilIdle()
    val state = viewModel.state.value
    assertFalse(state.isSaving)
    assertTrue(state.saveSuccess)
  }
}
