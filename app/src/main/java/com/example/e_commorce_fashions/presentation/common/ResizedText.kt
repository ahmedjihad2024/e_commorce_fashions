package com.example.e_commorce_fashions.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified

@Composable
fun ResizedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    ){

    val resizedTextStyle = remember(style) { mutableStateOf(style) }
    val defaultFontSize = MaterialTheme.typography.bodySmall.fontSize
    var shouldDraw =  false

    Text(
        text = text,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        style = resizedTextStyle.value,
        onTextLayout = { result ->
            if(result.didOverflowWidth || result.didOverflowHeight){
                if(resizedTextStyle.value.fontSize.isUnspecified) {
                    resizedTextStyle.value = resizedTextStyle.value.copy(
                        fontSize = defaultFontSize
                    )
                }
                resizedTextStyle.value = resizedTextStyle.value.copy(
                    fontSize = resizedTextStyle.value.fontSize * 0.97
                )

            }else shouldDraw = true
        },
        modifier = Modifier.drawWithContent {
            if (shouldDraw) drawContent()
        }.then(modifier),
    )
}