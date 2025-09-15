package org.notesapp.presentation.pdf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PdfScreen(
  pdfUrl: String,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
)
