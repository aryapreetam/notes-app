package org.notesapp.domain.usecase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.notesapp.data.repository.NotesRepository

class DeleteNoteUseCase() : KoinComponent {
  private val repo: NotesRepository by inject()

  suspend operator fun invoke(id: Long) {
    repo.deleteNote(id)
  }
}
