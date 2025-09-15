package org.notesapp.domain.usecase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.notesapp.data.repository.NotesRepository

class CreateNoteUseCase() : KoinComponent {
  private val repo: NotesRepository by inject()

  suspend operator fun invoke(title: String, body: String, createdDateMillis: Long) {
    repo.createNote(title = title, body = body, createdDateMillis = createdDateMillis)
  }
}
