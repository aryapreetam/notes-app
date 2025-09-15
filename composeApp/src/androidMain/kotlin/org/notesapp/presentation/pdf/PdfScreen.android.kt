package org.notesapp.presentation.pdf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PdfScreen(
  pdfUrl: String,
  onBack: () -> Unit,
  modifier: Modifier
) {
  val pdfState = rememberVerticalPdfReaderState(
    resource = ResourceType.Remote(pdfUrl),
    isZoomEnable = true
  )
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("PDF Viewer") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    content = { innerPadding ->
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxSize()
          .then(modifier)
          .padding(innerPadding)
      ) {
        if (!pdfState.isLoaded) {
          CircularProgressIndicator()
        }
        VerticalPDFReader(
          state = pdfState,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  )
}
