package org.notesapp.presentation.pdf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFView

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, ExperimentalMaterial3Api::class)
@Composable
actual fun PdfScreen(
  pdfUrl: String,
  onBack: () -> Unit,
  modifier: Modifier
) {
  var loading by remember { mutableStateOf(true) }
  var error by remember { mutableStateOf<String?>(null) }
  var pdfDocument by remember { mutableStateOf<PDFDocument?>(null) }

  LaunchedEffect(pdfUrl) {
    loading = true
    error = null
    pdfDocument = null
    withContext(Dispatchers.Default) {
      try {
        val url = NSURL(string = pdfUrl)
        val data = NSData.dataWithContentsOfURL(url)
        if (data != null) {
          val doc = PDFDocument(data)
          if (doc != null) {
            withContext(Dispatchers.Main) {
              pdfDocument = doc
              loading = false
            }
          } else {
            withContext(Dispatchers.Main) {
              error = "Could not open PDF"
              loading = false
            }
          }
        } else {
          withContext(Dispatchers.Main) {
            error = "Failed to load data"
            loading = false
          }
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          error = e.message ?: "Unknown error"
          loading = false
        }
      }
    }
  }

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
      Column(Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
          when {
            loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            error != null -> Text(error!!, Modifier.align(Alignment.Center))
            pdfDocument != null -> {
              UIKitView(
                factory = {
                  val view = PDFView().apply {
                    document = pdfDocument
                    autoScales = true
                  }
                  view
                },
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }
    }
  )
}
