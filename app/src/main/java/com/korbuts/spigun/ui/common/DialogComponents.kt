package com.korbuts.spigun.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.korbuts.spigun.R
import com.korbuts.spigun.ui.theme.SpigunTheme

@Composable
fun SpigunDialogConfirmButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isDestructive: Boolean = false
) {
    val view = LocalView.current
    Button(
        onClick = {
            view.vibrate()
            onClick()
        },
        enabled = enabled,
        colors = if (isDestructive) {
            ButtonDefaults.buttonColors(containerColor = SpigunTheme.colors.error)
        } else {
            ButtonDefaults.buttonColors()
        }
    ) {
        Text(text)
    }
}

@Composable
fun SpigunDialogDismissButton(
    onClick: () -> Unit,
    text: String = stringResource(R.string.dialog_confirm_cancel)
) {
    val view = LocalView.current
    TextButton(onClick = {
        view.vibrate()
        onClick()
    }) {
        Text(text)
    }
}

@Composable
fun SpigunDialogOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}

@Composable
fun SpigunAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = content,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}
