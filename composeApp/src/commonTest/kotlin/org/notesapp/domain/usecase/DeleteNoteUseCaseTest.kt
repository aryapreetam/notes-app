package org.notesapp.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.notesapp.data.repository.NotesRepository

class DeleteNoteUseCaseTest {
  private class FakeNotesRepository : NotesRepository {
    val deletedCalls = mutableListOf<Long>()
    override fun observeNotes() = throw NotImplementedError()
    override suspend fun createNote(title: String, body: String, createdDateMillis: Long) = throw NotImplementedError()
    override suspend fun deleteNote(id: Long) {
      deletedCalls.add(id)
    }
  }

  @Test
  fun testDeleteNote_CallsRepositoryWithId() = runTest {
    val fakeRepo = FakeNotesRepository()
    val useCase = DeleteNoteUseCase(fakeRepo)

    useCase.invoke(42L)

    assertEquals(1, fakeRepo.deletedCalls.size)
    assertEquals(42L, fakeRepo.deletedCalls.first())
  }
}
