package com.example.e_commorce_fashions.presentation.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import kotlinx.coroutines.launch



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.bringIntoViewRequester(
    bringIntoViewRequester: BringIntoViewRequester
): Modifier {
    val coroutineScope = rememberCoroutineScope()
    return this.onFocusEvent {
        if (it.isFocused || it.hasFocus) {
            coroutineScope.launch {
                bringIntoViewRequester.bringIntoView()
            }
        }
    }
}