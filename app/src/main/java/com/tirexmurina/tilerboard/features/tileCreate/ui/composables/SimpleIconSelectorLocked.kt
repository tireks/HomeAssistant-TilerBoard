package com.tirexmurina.tilerboard.features.tileCreate.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog

@Composable
fun SimpleIconSelectorLocked(
    modifier: Modifier = Modifier,
    hintText: String,
    dialogTitle: String,
    dialogMessage: String
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clickable { showDialog = true },

        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_handyman_24),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = hintText,
                fontSize = 10.sp
            )
        }
    }

    if (showDialog) {
        SingleButtonDialog(
            onDismiss = { showDialog = false },
            title = dialogTitle,
            message = dialogMessage
        )
    }
}