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

@ExperimentalMaterial3Api
@Composable
fun NavigationHost(navController: NavHostController) {
  NavHost(navController = navController, startDestination = Screen.NotesList) {
    composable<Screen.NotesList> {
      NotesListScreen(
        onAddClick = { navController.navigate(Screen.CreateNote) }
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
  }
}