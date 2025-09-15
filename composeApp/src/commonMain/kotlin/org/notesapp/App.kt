package org.notesapp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.notesapp.presentation.navigation.NavigationHost
import org.notesapp.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() = AppTheme {
  var dialogMessage by remember { mutableStateOf<String?>(null) }
  var snackbarMessage by remember { mutableStateOf<String?>(null) }
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  var shouldShowDialog by remember { mutableStateOf(true) }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    content = { innerPadding ->
      NavigationHost(
        navController = rememberNavController(),
        onJsMessage = { msg ->
          if (shouldShowDialog) {
            dialogMessage = msg
          } else {
            snackbarMessage = msg
          }
          shouldShowDialog = !shouldShowDialog
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
