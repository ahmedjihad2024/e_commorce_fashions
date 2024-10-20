package com.example.e_commorce_fashions.presentation.views.on_boarding.view

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.presentation.common.roundedRectangleWithCustomCornerShape
import com.example.e_commorce_fashions.presentation.common.sh
import com.example.e_commorce_fashions.presentation.common.toPxFloat
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge
import kotlinx.coroutines.launch

@Composable
fun OnBoardingView(navController: NavHostController) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val details = rememberSaveable {
        listOf(
            OnBoardingDetails(
                title = context.getString(R.string.on_boarding_title_one),
                subtitle = context.getString(R.string.on_boarding_subtitle),
                image = R.drawable.on_boarding_one
            ),
            OnBoardingDetails(
                title = context.getString(R.string.on_boarding_title_two),
                subtitle = context.getString(R.string.on_boarding_subtitle),
                image = R.drawable.on_boarding_two
            ),
            OnBoardingDetails(
                title = context.getString(R.string.on_boarding_title_three),
                subtitle = context.getString(R.string.on_boarding_subtitle),
                image = R.drawable.on_boarding_three
            )
        )
    }

    val pageState = rememberPagerState { details.size }

    Box(
        Modifier
            .background(Scheme.primary)
            .fillMaxSize()
    ) {
        OnBoardingPager(OnBoardingPagerDetails(
            pageState, details
        ))
        Row(
            Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                for (i in details.indices) {
                    PageIndicator(isSelected = pageState.currentPage == i)
                }
            }
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Scheme.onPrimary)
                    .size(70.dp)
                    .padding(20.dp),
                color = Color.Transparent,
                onClick = {
                  if(pageState.currentPage == details.size - 1){
                      scope.launch {
                          Di.preferences.value.setFirstLaunch(true)
                          navController.navigate(Views.AuthWelcomeView.route){
                              popUpTo(Views.OnBoardingView.route){
                                  inclusive = true
                              }
                          }
                      }
                  }else{
                     scope.launch {
                         pageState.animateScrollToPage(pageState.currentPage + 1)
                     }
                  }
                }
            ) {
                Image(
                    modifier = Modifier.rotate(180f),
                    painter = rememberAsyncImagePainter(R.drawable.ic_arrow_back),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Scheme.primary)
                )
            }
        }
    }
}

@Composable
fun OnBoardingPager(
   details: OnBoardingPagerDetails
) {
    HorizontalPager(state = details.pageState, key = {it}, beyondViewportPageCount = 3) { pageNo ->
        OnBoardingPage(details.details[pageNo])
    }
}

@Composable
fun OnBoardingPage(item: OnBoardingDetails) {
    Column(
        Modifier
            .padding(20.dp)
            .padding(top = 30.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        val imagePainter =
            rememberAsyncImagePainter(item.image, contentScale = ContentScale.Crop)
        Image(
            painter = imagePainter,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(
                    roundedRectangleWithCustomCornerShape(
                        40.dp.toPxFloat,
                        40.dp.toPxFloat,
                        35.dp.toPxFloat,
                        40.dp.toPxFloat,
                        bottomRightOffsetHeight = 30.dp.toPxFloat
                    )
                )
                .height(.55.sh)
        )

        Spacer(modifier = Modifier.height(55.dp))
        Text(
            item.title,
            style = TitleLarge.copy(
                color = Scheme.onPrimary,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            item.subtitle,
            style = TitleLarge.copy(
                color = Scheme.onPrimary.copy(alpha = .5f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}


@Composable
fun PageIndicator(isSelected: Boolean) {
    val color =
        if (isSelected) Scheme.onPrimary else Scheme.onPrimary.copy(alpha = .5f)
    val width by animateDpAsState(if (isSelected) 25.dp else 7.dp, label = "indicatorWidth")

    Box(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .height(7.dp)
            .width(width)
            .clip(CircleShape)
            .background(color)
    )
}


@Stable
data class OnBoardingDetails(
    val title: String,
    val subtitle: String,
    @DrawableRes val image: Int
)

@Stable
data class OnBoardingPagerDetails (
    val pageState: PagerState,
    val details: List<OnBoardingDetails>
)
