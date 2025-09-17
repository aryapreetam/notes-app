package org.notesapp.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.notesapp.data.model.Note
import org.notesapp.data.repository.NotesRepository

class GetNotesUseCaseTest {
  private class FakeNotesRepository(val notes: List<Note>) : NotesRepository {
    override fun observeNotes(): Flow<List<Note>> = flowOf(notes)
    override suspend fun createNote(title: String, body: String, createdDateMillis: Long) = throw NotImplementedError()
    override suspend fun deleteNote(id: Long) = throw NotImplementedError()
  }

  @Test
  fun testGetNotes_ReturnsNotesFlow() = runTest {
    val sampleNotes = listOf(
      Note(id = 1L, title = "First Note", body = "Note <b>body</b>", createdDateMillis = 1111L),
      Note(id = 2L, title = "Second Note", body = "Another <i>body</i>", createdDateMillis = 2222L)
    )
    val fakeRepo = FakeNotesRepository(sampleNotes)
    val useCase = GetNotesUseCase(fakeRepo)

    val emitted = useCase.invoke()
    val values = mutableListOf<List<Note>>()
    emitted.collect { list -> values.add(list) }
    // There should be only one emission from flowOf
    assertEquals(1, values.size)
    assertEquals(sampleNotes, values.first())
  }
}
