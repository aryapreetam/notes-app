package org.notesapp.domain.usecase

import org.notesapp.data.repository.NotesRepository

class DeleteNoteUseCase(private val repo: NotesRepository) {
  suspend operator fun invoke(id: Long) {
    repo.deleteNote(id)
  }
}
