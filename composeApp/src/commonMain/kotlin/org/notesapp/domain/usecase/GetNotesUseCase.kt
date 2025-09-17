package org.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.notesapp.data.model.Note
import org.notesapp.data.repository.NotesRepository

class GetNotesUseCase(private val repo: NotesRepository) {
  operator fun invoke(): Flow<List<Note>> = repo.observeNotes()
}
