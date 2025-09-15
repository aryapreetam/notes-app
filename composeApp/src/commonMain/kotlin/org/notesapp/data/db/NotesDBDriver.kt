package org.notesapp.data.db

import app.cash.sqldelight.db.SqlDriver
import org.notesapp.db.NotesDB

/**
 * Simple wrapper to expose a platform-provided SqlDriver.
 * Platform modules must provide a SqlDriver binding.
 */
expect fun createDatabaseDriver(): SqlDriver

fun createDatabase(): NotesDB = NotesDB(createDatabaseDriver())