package com.example.route.ui.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    hintText: String,
    hintStyle: TextStyle,
    ColorScheme: ColorScheme,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    errorText: String? = null,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = hintText,
                style = hintStyle,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ColorScheme.secondary,
            unfocusedContainerColor = ColorScheme.secondary,
            errorContainerColor = ColorScheme.secondary,
            errorBorderColor = ColorScheme.error,
        ),
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        isError = errorText != null,
        supportingText = errorText?.let { message ->
            {
                Text(
                    text = message,
                    color = ColorScheme.error,
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}