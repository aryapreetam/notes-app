package org.notesapp.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.notesapp.db.NotesDB

actual fun createDatabaseDriver(): SqlDriver {
  return NativeSqliteDriver(NotesDB.Schema, "notes.db")
}