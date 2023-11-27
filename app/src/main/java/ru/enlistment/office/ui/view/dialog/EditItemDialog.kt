package ru.enlistment.office.ui.view.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.enlistment.office.ui.view.BaseOutlinedTextField

@Composable
fun EditItemDialog(
    label: String = "",
    value: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    switchText: String = "",
    switch: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )
        },
        text = {
            Column {
                BaseOutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = keyboardOptions,
                    label = label
                )

                onSwitchChange?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = switchText,
                            modifier = Modifier.padding(5.dp)
                        )

                        Switch(
                            checked = switch,
                            onCheckedChange = onSwitchChange
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSaveClick(); onDismissRequest()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Отмена")
            }
        }
    )
}