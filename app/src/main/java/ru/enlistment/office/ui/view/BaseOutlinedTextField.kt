package ru.enlistment.office.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseOutlinedTextField(
    value: String,
    label: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit = {}
){
    Text(
        text = label,
        fontWeight = FontWeight.W900,
        modifier = Modifier.padding(5.dp).fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    OutlinedTextField(
        modifier = Modifier.padding(bottom = 5.dp, start = 5.dp),
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        shape = AbsoluteRoundedCornerShape(15.dp),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}