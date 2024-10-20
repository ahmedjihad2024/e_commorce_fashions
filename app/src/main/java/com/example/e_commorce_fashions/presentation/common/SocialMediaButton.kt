package com.example.e_commorce_fashions.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall

@Composable
fun SocialMediaButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: ImageVector? = null,
    painter: Painter? = null,
    iconColor: Color,
    annotatedString: AnnotatedString,
    textColor: Color,
    onClick: () -> Unit
){
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .fillMaxWidth()
            .height(60.dp)
            .wrapContentSize(Alignment.Center).then(modifier),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            if(icon != null ){
                Icon(
                    modifier = Modifier.padding(end = 10.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }else{
                Icon(
                    modifier = Modifier.padding(end = 10.dp),
                    painter = painter!!,
                    contentDescription = null,
                    tint = iconColor
                )
            }
            Text(
                text = annotatedString,
                style = TitleSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = textColor,
                    fontSize = 16.sp,)
            )
        }
    }
}