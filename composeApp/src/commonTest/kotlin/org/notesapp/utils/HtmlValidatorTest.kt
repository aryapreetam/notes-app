package org.notesapp.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class HtmlValidatorTest {
  @Test
  fun blankHtmlFails() {
    val result = HtmlValidator.validate("")
    assertFalse(result.isValid)
    assertEquals("Body is required", result.error)
  }

  @Test
  fun tooLongHtmlFails() {
    val html = "<p>hello</p>".repeat(10_100) // >50k chars
    val result = HtmlValidator.validate(html)
    assertFalse(result.isValid)
    assertEquals("Body too long", result.error)
  }

  @Test
  fun disallowedTagFails() {
    val result = HtmlValidator.validate("<custom>bad</custom>")
    assertFalse(result.isValid)
    assertEquals("Disallowed tag: <custom>", result.error)
  }

  @Test
  fun anchorWithoutHrefFails() {
    val result = HtmlValidator.validate("<a>something</a>")
    assertFalse(result.isValid)
    assertEquals("Anchor tags must include href", result.error)
  }

  @Test
  fun anchorWithHrefSucceeds() {
    val result = HtmlValidator.validate("<a href='x'>something</a>")
    assertTrue(result.isValid)
    assertEquals(null, result.error)
  }

  @Test
  fun validHtmlSucceeds() {
    val result = HtmlValidator.validate("<h1>Hello</h1><p>world!</p>")
    assertTrue(result.isValid)
    assertEquals(null, result.error)
  }
}
