package com.example.e_commorce_fashions.presentation.views.layout.view.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.GoldYellow
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import kotlinx.coroutines.launch

@Composable
fun ProductCardTwo(item: ProductDetails, navController: ()-> NavHostController) {
    val scope = rememberCoroutineScope()
    val productDetailsView = remember(item) {
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
        modifier = Modifier
            .shadow(
                elevation = 40.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Scheme.onPrimary.copy(.15f)
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                productDetailsView()
            }
            .background(Scheme.primary)
            .padding(7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(.05f))
                    .width(70.dp)
                    .height(65.dp),
                painter = rememberAsyncImagePainter(
                    item.imageUrls?.first(),
                    contentScale = ContentScale.FillWidth
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Scheme.onPrimary
                    ),
                )
                Text(
                    modifier = Modifier.widthIn(max = 150.dp),
                    text = item.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        lineHeight = 16.sp,
                        color = Scheme.onPrimary.copy(.4f)
                    ),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Star,
                        null,
                        tint = GoldYellow,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        modifier = Modifier.widthIn(max = 150.dp),
                        text = "(${item.rating})",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TitleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            lineHeight = 16.sp,
                            color = Scheme.onPrimary
                        ),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                "\$${item.price}",
                style = TitleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Scheme.onPrimary,
                    fontSize = 13.sp,)
            )
        }
    }
}