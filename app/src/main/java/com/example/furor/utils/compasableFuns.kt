package com.example.furor.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun UriImage(
    dp: Dp,
    uri: String,
    action: () -> Unit,
) {
    AsyncImage(
        model = uri,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(CircleShape)
            .size(dp)
            .clickable {
                action()
            }
    )
}

@Composable
fun mainFieldStyle(
    labelText: String,
    enable: Boolean,
    maxLine: Int,
    action: () -> Unit,
): String {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                action()
            },
        label = { Text(text = labelText, fontSize = 12.sp) },
        maxLines = maxLine,
        enabled = enable,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,

            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onTertiary,

            disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.outline,
            disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,

            )
    )
    return text
}