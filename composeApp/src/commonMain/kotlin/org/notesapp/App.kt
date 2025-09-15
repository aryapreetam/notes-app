package org.notesapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.notesapp.presentation.navigation.NavigationHost
import org.notesapp.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun App() {
  AppTheme {
    NavigationHost(rememberNavController())
  }
}
