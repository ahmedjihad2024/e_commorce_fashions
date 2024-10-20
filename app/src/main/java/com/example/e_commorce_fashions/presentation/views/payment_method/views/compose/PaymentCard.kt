package com.example.e_commorce_fashions.presentation.views.payment_method.views.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall

@Composable
fun PaymentCard(@StringRes label: Int, @DrawableRes image: Int, onTap: () -> Unit) {
    Box(
        modifier = Modifier.shadow(
            40.dp,
            RoundedCornerShape(15.dp),
            clip = false,
            spotColor = Color.Black.copy(alpha = 0.2f)
        )
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = onTap)
            .fillMaxWidth()
            .background(Scheme.primary)
            .padding(horizontal = 15.dp, vertical = 10.dp),
    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(image),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Scheme.onPrimary),
                modifier = Modifier
                    .background(Scheme.onPrimary.copy(.05f), CircleShape)
                    .size(45.dp)
                    .padding(12.dp)
            )

            Text(
                text = stringResource(label),
                style = TitleSmall.copy(
                    color = Scheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            )
        }
    }
}
