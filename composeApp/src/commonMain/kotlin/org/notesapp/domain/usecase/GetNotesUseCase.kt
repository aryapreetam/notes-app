package org.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.notesapp.data.model.Note
import org.notesapp.data.repository.NotesRepository

class GetNotesUseCase(): KoinComponent {
  private val repo: NotesRepository by inject()

  operator fun invoke(): Flow<List<Note>> = repo.observeNotes()
}
