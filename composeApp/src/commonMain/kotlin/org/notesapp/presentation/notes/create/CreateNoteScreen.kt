package org.notesapp.presentation.notes.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.notesapp.utils.DateFormatter
import org.notesapp.presentation.components.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.notesapp.domain.usecase.CreateNoteUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
  viewModel: CreateNoteViewModel,
  onBackClick: () -> Unit,
  onDatePickRequest: () -> Unit,
  onSaved: () -> Unit
) {
  val state by viewModel.state.collectAsState()
  val fm = LocalFocusManager.current

  // State for date picker dialog
  var showDatePicker by remember { mutableStateOf(false) }
  val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.selectedDateMillis)

  // Side-effect to trigger onSaved when saveSuccess is set
  LaunchedEffect(state.saveSuccess) {
    if (state.saveSuccess) onSaved()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("New Note") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    content = { innerPadding ->
      Column(
        modifier = Modifier
          .padding(innerPadding)
          .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        OutlinedTextField(
          value = state.title,
          onValueChange = viewModel::onTitleChange,
          label = { Text("Title") },
          singleLine = true,
          isError = state.titleError != null,
          supportingText = {
            val errorMsg = state.titleError
            if (errorMsg != null) Text(errorMsg, color = MaterialTheme.colorScheme.error)
          },
          modifier = Modifier.fillMaxWidth(),
          keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text
          )
        )
        OutlinedTextField(
          value = state.body,
          onValueChange = viewModel::onBodyChange,
          label = { Text("Content (HTML)") },
          isError = state.bodyError != null || !state.isHtmlValid,
          supportingText = {
            val errorMsg = state.bodyError
            when {
              errorMsg != null -> Text(errorMsg, color = MaterialTheme.colorScheme.error)
              !state.isHtmlValid -> Text("Invalid HTML", color = MaterialTheme.colorScheme.error)
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 120.dp),
          keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default, autoCorrectEnabled = false),
          maxLines = 10,
          minLines = 5,
        )
        TextButton(
          onClick = { showDatePicker = true },
        ) {
          Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(Icons.Filled.DateRange, contentDescription = "Pick Date")
            Spacer(Modifier.width(8.dp))
            val dateText = remember(state.selectedDateMillis) {
              DateFormatter.formatEpochMillis(state.selectedDateMillis)
            }
            Text(dateText)
          }
        }
        Spacer(Modifier.width(16.dp))
        Button(
          onClick = { viewModel.save() },
          enabled = state.canSave && !state.isSaving,
        ) {
          Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),) {
            if (state.isSaving) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
              )
              Spacer(Modifier.width(12.dp))
            }
            Text("Save Note")
          }
        }
      }
      if (showDatePicker) {
        DatePickerDialog(
          onDateSelected = { selectedMillis ->
            if (selectedMillis != null) {
              viewModel.onDateChange(selectedMillis)
            }
            showDatePicker = false
          },
          onDismiss = { showDatePicker = false },
          state = datePickerState
        )
      }
    }
  )
}

// Preview helpers
fun previewCreateNoteViewModel(): CreateNoteViewModel {
  val fakeNotesRepo = object : org.notesapp.data.repository.NotesRepository {
    override fun observeNotes() = flowOf(emptyList<org.notesapp.data.model.Note>())
    override suspend fun createNote(title: String, body: String, createdDateMillis: Long) {}
    override suspend fun deleteNote(id: Long) {}
  }
  val fakeCreateNote = CreateNoteUseCase(fakeNotesRepo)
  return CreateNoteViewModel(fakeCreateNote)
}

@Preview
@Composable
fun CreateNoteScreenPreview() {
  CreateNoteScreen(
    viewModel = previewCreateNoteViewModel(),
    onBackClick = {},
    onDatePickRequest = {},
    onSaved = {}
  )
}
