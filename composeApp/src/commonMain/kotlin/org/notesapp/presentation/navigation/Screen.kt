package org.notesapp.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe, sealed navigation destinations for Compose Multiplatform navigation.
 * Serializable allows navigation state to be saved, restored, or passed as screen argument.
 */
@Serializable
sealed class Screen {
  @Serializable
  object NotesList : Screen()

  @Serializable
  object CreateNote : Screen()

  @Serializable
  data class PdfViewer(val url: String) : Screen()

  // Example for screen with arguments
  // @Serializable
  // data class NoteDetail(val id: Long) : Destination
}
