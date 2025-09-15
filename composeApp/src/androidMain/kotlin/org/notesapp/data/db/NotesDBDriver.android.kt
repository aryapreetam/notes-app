package org.notesapp.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.java.KoinJavaComponent.get
import org.notesapp.NotesApp
import org.notesapp.db.NotesDB

actual fun createDatabaseDriver(): SqlDriver {
  return AndroidSqliteDriver(NotesDB.Schema, get(Context::class.java), "notes.db")
}