package org.notesapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.notesapp.data.model.Note
import org.notesapp.utils.DateFormatter
import org.notesapp.utils.isIOS

class HtmlClickHandler(private val onMessage: (String) -> Unit) : IJsMessageHandler {
  override fun methodName(): String = "showInfo"
  override fun handle(
    message: JsMessage,
    navigator: WebViewNavigator?,
    callback: (String) -> Unit
  ) {
    Logger.i {
      "KMP Received $message"
    }
    val param = message.params
    onMessage(param) // Pass the click message up
  }
}

@Composable
fun NoteCard(
  note: Note,
  onDeleteClick: (id: Long) -> Unit,
  onCardClick: () -> Unit,
  onJsMessage: (String) -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = MaterialTheme.shapes.medium
) {
  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .clickable { onCardClick() },
    elevation = elevatedCardElevation(4.dp),
    shape = RoundedCornerShape(4.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
  ) {
    val webViewState = rememberWebViewStateWithHTMLData(data = note.body)
    val jsBridge = rememberWebViewJsBridge()

    DisposableEffect(Unit) {
      webViewState.webSettings.apply {
        isJavaScriptEnabled = true
        androidWebSettings.apply {
          isAlgorithmicDarkeningAllowed = true
          safeBrowsingEnabled = true
        }
      }
      onDispose { }
    }

    LaunchedEffect(jsBridge) {
      jsBridge.register(HtmlClickHandler(onJsMessage))
    }

    Box {
      IconButton(
        onClick = { onDeleteClick(note.id) },
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Delete note",
          tint = MaterialTheme.colorScheme.error
        )
      }
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 12.dp)
      ) {
        Text(
          text = note.title,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        WebView(
          state = webViewState,
          modifier = if(isIOS()) Modifier.fillMaxWidth().height(150.dp) else Modifier.fillMaxWidth(),
          webViewJsBridge = jsBridge
        )
        Spacer(Modifier.height(8.dp))
        Text(
          text = DateFormatter.formatEpochMillis(note.createdDateMillis),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.tertiary,
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}

@Preview
@Composable
fun NoteCardPreview() {
  NoteCard(
    note = Note(
      id = 1,
      title = "Title",
      body = "<h1>Hello World</h1>",
      createdDateMillis = 1698451200000
    ),
    onDeleteClick = {},
    onCardClick = {},
    onJsMessage = {}
  )
}