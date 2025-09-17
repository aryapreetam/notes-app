package org.notesapp.domain.usecase

import org.notesapp.data.repository.NotesRepository

class CreateNoteUseCase(private val repo: NotesRepository) {
  suspend operator fun invoke(title: String, body: String, createdDateMillis: Long) {
    repo.createNote(title = title, body = body, createdDateMillis = createdDateMillis)
  }
}
