package com.tirexmurina.tilerboard.features.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SingleButtonDialog(
    modifier: Modifier = Modifier,
    message: String,
    title: String = "Ошибка",
    buttonText: String = "OK",
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(buttonText, color = MaterialTheme.colorScheme.primary)
            }
        },
        modifier = modifier
    )
}

@Composable
fun TwoButtonDialog(
    modifier: Modifier = Modifier,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Инфо",
    buttonConfirmText: String = "OK",
    buttonDismissText: String = "Отмена"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(buttonDismissText, color = MaterialTheme.colorScheme.primary)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(buttonConfirmText, color = MaterialTheme.colorScheme.primary)
            }
        },
        modifier = modifier
    )
}