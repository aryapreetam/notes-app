package org.notesapp.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.notesapp.data.repository.NotesRepository

class CreateNoteUseCaseTest {
  private class FakeNotesRepository : NotesRepository {
    data class CreatedCall(val title: String, val body: String, val createdDateMillis: Long)

    var createdCalls = mutableListOf<CreatedCall>()
    override fun observeNotes() = throw NotImplementedError()
    override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {
      createdCalls.add(CreatedCall(title, body, createdDateMillis))
    }

    override suspend fun deleteNote(id: Long) = throw NotImplementedError()
  }

  @Test
  fun testCreateNote_CallsRepositoryWithArgs() = runTest {
    val fakeRepo = FakeNotesRepository()
    val useCase = CreateNoteUseCase(fakeRepo)

    useCase.invoke("Test Note", "Some <b>body</b>", 123456789L)

    assertEquals(1, fakeRepo.createdCalls.size)
    val call = fakeRepo.createdCalls.first()
    assertEquals("Test Note", call.title)
    assertEquals("Some <b>body</b>", call.body)
    assertEquals(123456789L, call.createdDateMillis)
  }
}
