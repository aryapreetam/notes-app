package org.notesapp.domain.usecase

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class ValidateHtmlUseCaseTest {
  private val useCase = ValidateHtmlUseCase()

  @Test
  fun testValidHtmlSample_Passes() {
    val html = """
        <h2>Welcome to KMP Notes</h2>
        <p>This is a <b>sample note</b> with HTML and interactive elements.</p>
        <button onclick=\"showInfo('Clicked on Button 1')\">Click Me 1</button>
        <a href=\"#\" onclick=\"showInfo('Link Clicked')\">Click This Link</a>
        <script>
          function showInfo(msg) {
            if (window.kmpJsBridge) {
              window.kmpJsBridge.callNative("showInfo", msg, function (data) {
                console.log("Greet from Native: " + data);
              });
            }
          }
        </script>
        """.trimIndent()
    val result = useCase.invoke(html)
    assertTrue(result.isValid, result.error ?: "Should be valid")
  }

  @Test
  fun testHeaderTag_Valid() {
    val result = useCase.invoke("<h1>some text</h1>")
    assertTrue(result.isValid, result.error ?: "Should be valid header")
  }

  @Test
  fun testMalformedTag_Fails() {
    // Tag is malformed and doesn't close properly
    val result = useCase.invoke("<h1>some text ht>")
    assertTrue(result.isValid, "Malformed tag should be treated as plain text and not produce a disallowed tag error")
  }

  @Test
  fun testDisallowedTag_Fails() {
    val result = useCase.invoke("<foo>bar</foo>")
    assertFalse(result.isValid)
    assertEquals("Disallowed tag: <foo>", result.error)
  }

  @Test
  fun testAnchorWithoutHref_Fails() {
    val result = useCase.invoke("<a>no href</a>")
    assertFalse(result.isValid)
    assertEquals("Anchor tags must include href", result.error)
  }

  @Test
  fun testAnchorWithHref_Passes() {
    val result = useCase.invoke("<a href=\"#\">with href</a>")
    assertTrue(result.isValid, result.error ?: "Should be valid anchor tag")
  }

  @Test
  fun testButtonAndScript_Passes() {
    val html = """
            <button onclick=\"alert('hi')\">Hi</button>
            <script>console.log('hi')</script>
        """.trimIndent()
    val result = useCase.invoke(html)
    assertTrue(result.isValid, result.error ?: "Button/script should be allowed")
  }

  @Test
  fun testImageAndVideo_Passes() {
    val html = "<img src='foo.png'/><video src='v.mp4'></video>"
    val result = useCase.invoke(html)
    assertTrue(result.isValid, result.error ?: "Media tags should be allowed")
  }

  @Test
  fun testEmptyBody_Fails() {
    val result = useCase.invoke("")
    assertFalse(result.isValid)
    assertEquals("Body is required", result.error)
  }
}
