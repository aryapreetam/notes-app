package org.notesapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.notesapp.data.model.Note
import org.notesapp.utils.DateFormatter

@Composable
fun NoteCard(
  note: Note,
  onDeleteClick: () -> Unit,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = MaterialTheme.shapes.medium
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .clickable { onCardClick() },
    shape = shape,
    elevation = elevatedCardElevation(1.dp),
    colors = outlinedCardColors(),
  ) {
    Box {
      // Delete button (top-right corner)
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
      // Note content
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
        Text(
          text = note.body.replace(Regex("<[^>]*>"), "").trim(), // Strip HTML tags for preview
          style = MaterialTheme.typography.bodyMedium,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurfaceVariant
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
