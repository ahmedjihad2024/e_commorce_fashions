package com.example.e_commorce_fashions.presentation.views.layout.view.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeEvent
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ViewAllProductsScreenData(
    val products: List<ProductDetails>,
    val screenTitle: String,
    val lastDocumentSnapshot: DocumentSnapshot?
)

@Composable
fun ProductCardOne(
    productItem: () -> ProductDetails,
    navController: () -> NavHostController,
    onFavoriteClick: (ProductDetails) -> Unit,
) {

    val item = productItem()
    val scope = rememberCoroutineScope()
    val productDetailsView : ()-> Unit = remember(item) {
        {
            scope.launch {
                Di.sharedData.value.setData(
                    "product-details",
                    item
                )
                navController().navigate(Views.ProductDetailsView.route)
            }
        }
    }

    Box(
        Modifier.widthIn(max = 160.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable(
                    onClick = productDetailsView
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black.copy(.05f))
                    .height(170.dp),
                painter = rememberAsyncImagePainter(
                    item.imageUrls?.first(),
                    contentScale = ContentScale.FillWidth
                ),
                contentScale = ContentScale.FillWidth,
                contentDescription = "product"
            )
            Spacer(Modifier.height(13.dp))
            Text(
                item.name,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 15.dp),
                style = TitleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    color = Scheme.onPrimary,
                )
            )
            Spacer(Modifier.height(3.dp))
            Text(
                item.description,
                modifier = Modifier.padding(horizontal = 15.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = TitleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    color = Scheme.onPrimary.copy(.5f),
                )
            )
            Spacer(Modifier.height(3.dp))
            Text(
                "\$${item.price}",
                style = TitleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    color = Scheme.onPrimary,
                )
            )
            Spacer(Modifier.height(5.dp))

        }

        // favorite button
        Box(
            modifier = Modifier
                .padding(top = 15.dp, end = 15.dp)
                .clip(CircleShape)
                .clickable {
                    onFavoriteClick(item)
                }
                .background(Scheme.onPrimary)
                .size(30.dp)
                .padding(7.dp)
                .align(Alignment.TopEnd),
        ) {
            AnimatedContent(item.isFavorite, label = "favorite-button",
                transitionSpec = {
                    (scaleIn(
                        initialScale = .7f,
                        animationSpec = tween(250)
                    ) + fadeIn(animationSpec = tween(250))).togetherWith(
                        scaleOut(targetScale = .5f, animationSpec = tween(250)) + fadeOut(
                            animationSpec = tween(250)
                        )
                    )
                }) { isIt ->
                if (isIt) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = Scheme.primary
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(
                            R.drawable.ic_favorites,
                            contentScale = ContentScale.Fit
                        ),
                        colorFilter = ColorFilter.tint(Scheme.primary),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null
                    )
                }
            }

        }
    }
}


