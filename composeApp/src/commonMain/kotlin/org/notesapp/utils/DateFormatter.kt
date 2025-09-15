package org.notesapp.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Formats epoch millis to a date string with pattern "d MMMM yyyy" (e.g., 17 July 2025).
 * NOTE: For Phase 1–4 we use a simple English month mapping in commonMain.
 * Platform-specific locale-aware formatting can be added later.
 */
object DateFormatter {
  @OptIn(kotlin.time.ExperimentalTime::class)
  fun formatEpochMillis(epochMillis: Long): String {
    val date = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
    return formatLocalDate(date)
  }

  fun formatLocalDate(date: LocalDate): String {
    val monthName = when (date.monthNumber) {
      1 -> "January"
      2 -> "February"
      3 -> "March"
      4 -> "April"
      5 -> "May"
      6 -> "June"
      7 -> "July"
      8 -> "August"
      9 -> "September"
      10 -> "October"
      11 -> "November"
      12 -> "December"
      else -> date.month.name
    }
    return "${date.dayOfMonth} ${monthName} ${date.year}"
  }
}
