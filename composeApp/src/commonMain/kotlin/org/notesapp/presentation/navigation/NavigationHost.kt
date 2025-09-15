package org.notesapp.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.koin.compose.koinInject
import org.notesapp.presentation.notes.create.CreateNoteScreen
import org.notesapp.presentation.notes.list.NotesListScreen
import org.notesapp.presentation.pdf.PdfScreen

@ExperimentalMaterial3Api
@Composable
fun NavigationHost(
  onJsMessage: (String) -> Unit,
  navController: NavHostController
) {
  NavHost(navController = navController, startDestination = Screen.NotesList) {
    composable<Screen.NotesList> {
      NotesListScreen(
        onAddClick = { navController.navigate(Screen.CreateNote) },
        onJsMessage = onJsMessage,
        onViewPdf = {
          navController.navigate(Screen.PdfViewer("https://qa.pilloo.ai/GeneratedPDF/Companies/202/2025-2026/DL.pdf"))
        }
      )
    }
    composable<Screen.CreateNote> {
      CreateNoteScreen(
        onDatePickRequest = {},
        onSaved = {
          navController.popBackStack()
        },
        onBackClick = {
          navController.popBackStack()
        }
      )
    }
    composable<Screen.PdfViewer> { entry ->
      val screen = entry.toRoute<Screen.PdfViewer>()
      PdfScreen(
        pdfUrl = screen.url,
        onBack = { navController.popBackStack() }
      )
    }
  }
}