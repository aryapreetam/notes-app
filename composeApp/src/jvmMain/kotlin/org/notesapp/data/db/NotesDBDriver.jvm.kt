package org.notesapp.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.notesapp.db.NotesDB

actual fun createDatabaseDriver(): SqlDriver {
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  NotesDB.Schema.create(driver)
  return driver
}