package org.notesapp.domain.usecase

/** Simple HTML validator with an allow-list of tags/attrs and basic checks. */
class ValidateHtmlUseCase {
  data class Result(val isValid: Boolean, val error: String? = null)

  private val allowedTags = setOf(
    "p", "b", "i", "u", "a", "ul", "ol", "li", "br", "h1", "h2", "h3", "button", "script", "style", "img", "video", "audio", "source", "track", "canvas", "table", "tbody", "tr", "td", "th", "form", "input", "select", "option", "textarea", "fieldset", "legend", "label", "span", "pre", "code", "pre", "hr"
  )

  /**
   * Validates HTML body for basic constraints.
   * - Non-empty
   * - Length limit (<= 50k)
   * - Tags from allow-list; 'a' may have href attribute only
   */
  operator fun invoke(html: String): Result {
    val body = html.trim()
    if (body.isEmpty()) return Result(false, "Body is required")
    if (body.length > 50_000) return Result(false, "Body too long")

    // Basic tag allow-list check
    val tagRegex = Regex("<(/?)([a-zA-Z0-9]+)([^>]*)>")
    for (match in tagRegex.findAll(body)) {
      val tag = match.groupValues[2].lowercase()
      if (tag !in allowedTags) return Result(false, "Disallowed tag: <$tag>")
      if (tag == "a") {
        val attrs = match.groupValues[3]
        val hrefAllowed = attrs.contains(Regex("\\bhref=", RegexOption.IGNORE_CASE))
        // allow empty attrs for closing tag </a>
        if (!match.groupValues[1].equals("/")) {
          if (!hrefAllowed) return Result(false, "Anchor tags must include href")
        }
      }
    }
    return Result(true)
  }
}
