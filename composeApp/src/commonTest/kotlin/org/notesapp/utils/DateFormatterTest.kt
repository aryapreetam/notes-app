package org.notesapp.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.notesapp.utils.DateFormatter

class DateFormatterTest {
  @Test
  fun testFormatLocalDate_AllMonths() {
    val expectedMonths = listOf(
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
    )
    for (month in 1..12) {
      val date = LocalDate(year = 2025, monthNumber = month, dayOfMonth = 15)
      val expected = "15 ${expectedMonths[month - 1]} 2025"
      assertEquals(expected, DateFormatter.formatLocalDate(date), "Month name incorrect for month $month")
    }
  }

  @Test
  fun testFormatLocalDate_LeapYear() {
    val date = LocalDate(year = 2024, monthNumber = 2, dayOfMonth = 29)
    val expected = "29 February 2024"
    assertEquals(expected, DateFormatter.formatLocalDate(date), "Leap year date formatting failed")
  }

  @Test
  fun testFormatEpochMillis_KnownDate() {
    // 17 July 2025, UTC timezone
    val epochMillis = 1752710400000L // 2025-07-17T00:00:00 UTC
    val expected = "17 July 2025"
    assertEquals(expected, DateFormatter.formatEpochMillis(epochMillis))
  }

  @Test
  fun testFormatEpochMillis_EdgeCases() {
    // Jan 1, 1970 (epoch 0)
    val janFirst1970 = 0L
    assertEquals("1 January 1970", DateFormatter.formatEpochMillis(janFirst1970))

    // Dec 31, 1999 (UTC)
    val dec311999 = 946598400000L // 1999-12-31T00:00:00 UTC
    assertEquals("31 December 1999", DateFormatter.formatEpochMillis(dec311999))
  }
}
