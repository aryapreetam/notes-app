package org.notesapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.notesapp.data.model.Note
import org.notesapp.utils.DateFormatter

class HtmlClickHandler(private val onMessage: (String) -> Unit) : IJsMessageHandler {
  override fun methodName(): String = "showInfo"
  override fun handle(
    message: JsMessage,
    navigator: WebViewNavigator?,
    callback: (String) -> Unit
  ) {
    Logger.i {
      "Greet Handler Get Message: $message"
    }
    val param = message.params
    val data = "KMP Received $param"
    onMessage(param) // Pass the click message up
    // callback(data) // Can be used if you want to return to JS
  }
}

@Composable
fun NoteCard(
  note: Note,
  onDeleteClick: () -> Unit,
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
        onClick = onDeleteClick,
        modifier = Modifier
          .align(androidx.compose.ui.Alignment.TopEnd)
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
          modifier = Modifier.fillMaxWidth(),
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
