package org.notesapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.notesapp.data.model.Note
import org.notesapp.db.NotesDB

/** SQLDelight-backed implementation of [NotesRepository]. */
class NotesRepositoryImpl(private val db: NotesDB) : NotesRepository {

  private val queries get() = db.notesQueries

  override fun observeNotes(): Flow<List<Note>> {
    return queries.selectAll()
      .asFlow()
      .mapToList(Dispatchers.Default)
      .map { rows ->
        rows.map { row ->
          Note(
            id = row.id,
            title = row.title,
            body = row.body,
            createdDateMillis = row.createdDate,
          )
        }
      }
  }

  override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {
    queries.insertNote(title = title, body = body, createdDate = createdDateMillis)
  }

  override suspend fun deleteNote(id: Long) {
    queries.deleteById(id)
  }
}
