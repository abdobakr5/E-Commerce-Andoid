package com.example.route.ui.core.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomElevatedButton(modifier: Modifier = Modifier,
                         onClick : () -> Unit,
                         text : String,
                         colorScheme : ColorScheme,
                         ) {
    ElevatedButton(
        onClick =onClick ,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onPrimary,

                )
        )
    }
}