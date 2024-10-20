package com.example.e_commorce_fashions.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge

@Composable
fun TextFormField(
    value: String,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    supportingText: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)?,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false,
    focusRequester: FocusRequester = FocusRequester()
) {
    val passwordVisibility = remember { mutableStateOf(!isPassword) }

    TextField(
        value = value,
        onValueChange = {
            onValueChange.invoke(it)
        },
        modifier = Modifier.padding(0.dp).fillMaxWidth().focusRequester(focusRequester),
        singleLine = true,
        isError = isError,
        supportingText = supportingText,
        label = label,
        textStyle = TitleLarge.copy(
            color = Scheme.onPrimary,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        ),
        visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        placeholder = placeholder,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Scheme.onPrimary.copy(.1f),
            unfocusedIndicatorColor = Scheme.onPrimary.copy(.1f),
            errorContainerColor = Color.Transparent,
            cursorColor = Scheme.onPrimary,
        ),
        trailingIcon = if(isPassword) {
            {
                val icon = if (passwordVisibility.value) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }, modifier = Modifier.size(22.dp)) {
                    Icon(imageVector = icon, contentDescription = if (passwordVisibility.value) "Hide password" else "Show password")
                }
            }
        }else null
    )
}