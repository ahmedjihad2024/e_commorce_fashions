package com.example.e_commorce_fashions.presentation.views.layout.view.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.sourceInformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import kotlinx.coroutines.launch

@Composable
fun BottomAppBar(
    scrollTo: suspend (page: Int) -> Unit,
    tween: Int = 300,
    currentPage: ()-> Int
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                ambientColor = Scheme.onPrimary
            )
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .fillMaxWidth()
            .background(Scheme.primary)
            .height(70.dp)
    ) {

        Row(
            Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            BottomBarItem(item = BottomItem(
                icon = R.drawable.ic_home,
                label = stringResource(R.string.home),
                currentPage = currentPage,
                tween = tween,
                scrollTo = scrollTo,
                pageIndex = 0
            )
            )
            BottomBarItem(item = BottomItem(
                icon = R.drawable.ic_cart_black,
                label = stringResource(R.string.cart),
                currentPage = currentPage,
                tween = tween,
                scrollTo = scrollTo,
                pageIndex = 1
            )
            )
//            BottomBarItem(item = BottomItem(
//                icon = R.drawable.ic_notification_black,
//                label = stringResource(R.string.notification),
//                currentPage = currentPage,
//                scrollTo = scrollTo,
//                pageIndex = 2
//            )
//            )
            BottomBarItem(item = BottomItem(
                icon = R.drawable.ic_profile_black,
                label = stringResource(R.string.profile),
                currentPage = currentPage,
                tween = tween,
                scrollTo = scrollTo,
                pageIndex = 2
            )
            )
        }
    }
}



data class BottomItem(
    @DrawableRes val icon: Int,
    val label: String,
    val scrollTo: suspend (Int) -> Unit,
    val tween: Int = 500,
    val pageIndex: Int,
    val currentPage: ()-> Int
)

@Composable
fun BottomBarItem(item: BottomItem) {

    val scope = rememberCoroutineScope()
    val painter = rememberAsyncImagePainter(
        item.icon,
        contentScale = ContentScale.Crop
    )
    val isSelected = item.currentPage() == item.pageIndex

    val iconSize = 42.dp
    val mainAnimation = updateTransition(isSelected, label = "")
    val iconColor by mainAnimation.animateColor(label = item.label, transitionSpec = {
        tween(
            item.tween
        )
    }) { selected ->
        if (selected) Scheme.primary else Scheme.onPrimary
    }
    val backgroundColor by mainAnimation.animateColor(label = item.label, transitionSpec = {
        tween(
            item.tween
        )
    }) { selected ->
        if (selected) Scheme.onPrimary else Scheme.primary
    }

    Box(
    ) {
        Image(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    scope.launch {
                        item.scrollTo(item.pageIndex)
                    }
                }
                .background(backgroundColor)
                .size(iconSize)
                .padding(12.dp)
                .wrapContentSize(Alignment.Center)
                .zIndex(2f),
            painter = painter,
            colorFilter = ColorFilter.tint(iconColor),
            contentDescription = item.label
        )
        AnimatedVisibility(
            isSelected,
            enter = expandHorizontally(animationSpec = tween(item.tween)),
            exit = shrinkHorizontally(animationSpec = tween(item.tween)),
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.Center)
                .zIndex(1f),
            label = item.label
        ) {
            Row {
                Spacer(modifier = Modifier.width(iconSize / 2))
                Text(
                    text = item.label,
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Scheme.onPrimary
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Scheme.onPrimary.copy(.1f))
                        .padding(vertical = 3.dp)
                        .padding(iconSize / 2 + 7.dp, end = 7.dp),
                )
            }
        }
    }
}