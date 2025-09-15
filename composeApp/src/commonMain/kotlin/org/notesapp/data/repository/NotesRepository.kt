package org.notesapp.data.repository

import kotlinx.coroutines.flow.Flow
import org.notesapp.data.model.Note

/** Repository contract for managing notes. */
interface NotesRepository {
  /** Observe notes sorted by created date desc, updating reactively. */
  fun observeNotes(): Flow<List<Note>>

  /** Create a new note. */
  suspend fun createNote(title: String, body: String, createdDateMillis: Long)

  /** Delete a note by id. */
  suspend fun deleteNote(id: Long)
}
