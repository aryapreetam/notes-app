package org.notesapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.notesapp.presentation.navigation.NavigationHost
import org.notesapp.theme.AppTheme
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() = AppTheme {
  var dialogMessage by remember { mutableStateOf<String?>(null) }
  var snackbarMessage by remember { mutableStateOf<String?>(null) }
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    content = { innerPadding ->
      NavigationHost(
        navController = rememberNavController(),
        onJsMessage = { msg ->
          if (Random.nextBoolean()) {
            dialogMessage = msg
          } else {
            snackbarMessage = msg
          }
        }
      )
      // Dialog can be shown anywhere, it's a dialog overlay
      dialogMessage?.let { msg ->
        AlertDialog(
          onDismissRequest = { dialogMessage = null },
          title = { Text("Clicked Event") },
          text = { Text(msg) },
          confirmButton = {
            Button(onClick = { dialogMessage = null }) {
              Text("OK")
            }
          }
        )
      }
      LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
          coroutineScope.launch {
            snackbarHostState.showSnackbar(msg)
            snackbarMessage = null
          }
        }
      }
    }
  )
}
