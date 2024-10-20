package com.example.e_commorce_fashions.presentation.views.payment_method.views

import android.app.Application
import android.location.Address
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.GOOGLE_PAY_LAUNCHER
import com.example.e_commorce_fashions.MainActivity
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.LOCAL_MAIN_ACTIVITY
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.PaymentMethod
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.choice_location.views.PaymentData
import com.example.e_commorce_fashions.presentation.views.payment_method.view_state.PaymentMethodEvent
import com.example.e_commorce_fashions.presentation.views.payment_method.view_state.PaymentMethodViewState
import com.example.e_commorce_fashions.presentation.views.payment_method.views.compose.PairText
import com.example.e_commorce_fashions.presentation.views.payment_method.views.compose.PaymentCard
import com.google.android.gms.wallet.button.PayButton
import com.stripe.android.PaymentConfiguration
import com.stripe.android.customersheet.PaymentOptionSelection
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import com.stripe.android.model.CardBrand
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.payments.paymentlauncher.rememberPaymentLauncher
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch
import java.lang.IllegalStateException


@Composable
fun PaymentMethodView(
    navController: () -> NavHostController,
    uiState: () -> PaymentMethodViewState
) {
    val paymentData = Di.sharedData.value.getData<PaymentData>("payment-method")!!
    val state = uiState().state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val complete = remember(state.value.selectedPaymentMethod) {
        {
            uiState().onEvent(
                PaymentMethodEvent.PurchaseCompleted(
                    String.format("%.2fF", paymentData.amount).toFloat(),
                    uiState().state.value.selectedPaymentMethod!!,
                    paymentData.address!!.latitude,
                    paymentData.address.longitude
                )
            )
        }
    }
    val stripePaymentSheet = rememberPaymentSheet { paymentSheetResult ->
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.d("TAG", "onPaymentSheetResult: Canceled")
               scope.launch {
                   snackBarHostState.showSnackbar("Canceled")
               }
            }

            is PaymentSheetResult.Failed -> {
                Log.d("TAG", "onPaymentSheetResult: ${paymentSheetResult.error}")
                scope.launch {
                    snackBarHostState.showSnackbar("Failed")
                }
            }

            is PaymentSheetResult.Completed -> {
                scope.launch {
                    launch {
                        snackBarHostState.showSnackbar("Completed Payment")
                    }
                    complete()
                }
            }
        }
    }

    val paymentLauncher: PaymentLauncher = rememberPaymentLauncher(
            PaymentConfiguration.getInstance(context).publishableKey,
            PaymentConfiguration.getInstance(context).stripeAccountId,
    ){ onResult ->
        when(onResult){
            is PaymentResult.Completed -> {
                scope.launch {
                    launch{
                        snackBarHostState.showSnackbar("Completed Payment")
                    }
                    complete()
                }
            }
            is PaymentResult.Canceled -> {
                scope.launch {
                    snackBarHostState.showSnackbar("Canceled")
                }
            }
            is PaymentResult.Failed -> {
                scope.launch {
                    snackBarHostState.showSnackbar("Failed")
                }
                Log.d("TAG", "onPaymentResult: ${onResult.throwable.stackTrace}")
            }
        }

    }

    LaunchedEffect(true) {
        scope.launch {
            uiState().snackBarState.collect {
                snackBarHostState.showSnackbar(it)
            }
        }
    }

    // showing the stripe payment
    LaunchedEffect(state.value.requestState) {
        if (state.value.requestState != RequestState.LOADING && state.value.clientSecret != null
        ) {
            if (state.value.selectedPaymentMethod == PaymentMethod.CREDIT_CARD || state.value.selectedPaymentMethod == PaymentMethod.VISA) {
                presentPaymentSheet(
                    stripePaymentSheet,
                    state.value.clientSecret!!,
                    context.getString(R.string.app_name),
                    PaymentSheet.CustomerConfiguration(
                        state.value.customerId!!,
                        state.value.ephemeralKey!!
                    ),
                    PaymentSheet.GooglePayConfiguration(
                        environment = PaymentSheet.GooglePayConfiguration.Environment.Test, // Or .Test for testing
                        countryCode = "US", // Set your country code
                        currencyCode = "USD",
                        amount = (paymentData.amount * 100).toLong(),
                        buttonType = PaymentSheet. GooglePayConfiguration.ButtonType.Checkout,
                    )
                )
            }else if(state.value.selectedPaymentMethod == PaymentMethod.GOOGLE_PAY) {
                try{
                    GOOGLE_PAY_LAUNCHER.presentForPaymentIntent(state.value.clientSecret!!, paymentData.amount.toString())
                }catch (e: IllegalStateException){
                    if(
                        e.message!= null && e.message!!.contains("is available")
                    ){
                        snackBarHostState.showSnackbar(context.getString(R.string.google_pay_not_available))
                    }
                }
            }else if(state.value.selectedPaymentMethod == PaymentMethod.PAYPAL){
                val confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(
                        paymentMethodCreateParams = PaymentMethodCreateParams.createPayPal(),
                        clientSecret =  state.value.clientSecret!!
                    )
                paymentLauncher.confirm(confirmParams)
            }

            if(state.value.purchaseCompleted){
                navController().popBackStack(Views.LayoutView.route, inclusive = false)
            }
        }
    }


    Scaffold(
        containerColor = Scheme.primary,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { scaffoldPaddings ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                Modifier
                    .padding(scaffoldPaddings)
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                navController().navigateUp()
                            }
                            .background(Scheme.onPrimary)
                            .size(55.dp)
                            .padding(15.dp)
                            .wrapContentSize(Alignment.Center),

                        ) {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.ic_arrow_back),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(Scheme.primary)
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.delivery_address),
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .shadow(
                            40.dp,
                            RoundedCornerShape(15.dp),
                            clip = false,
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                        .background(Scheme.primary)
                        .padding(horizontal = 15.dp, vertical = 25.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        PairText(
                            tOne = "Street: ",
                            tTwo = paymentData.address!!.thoroughfare ?: "None"
                        )
                        PairText(tOne = "City: ", tTwo = paymentData.address.locality ?: "None")
                        PairText(
                            tOne = "State/province/area: ",
                            tTwo = paymentData.address.adminArea ?: "None"
                        )
                        PairText(
                            tOne = "Phone number: ",
                            tTwo = paymentData.address.phone ?: "None"
                        )
                        PairText(
                            tOne = "Zip code: ",
                            tTwo = paymentData.address.postalCode ?: "None"
                        )
                        PairText(
                            tOne = "Country code: ",
                            tTwo = paymentData.address.countryCode ?: "None"
                        )
                        PairText(
                            tOne = "Country: ",
                            tTwo = paymentData.address.countryName ?: "None"
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.payment),
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(17.dp)
                ) {
                    PaymentMethod.entries.forEach {
                        PaymentCard(label = it.string, image = it.drawable) {
                            uiState().onEvent(
                                        PaymentMethodEvent.MakePayment(
                                            paymentData.amount,
                                            it
                                        )
                                    )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = state.value.requestState == RequestState.LOADING,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.zIndex(2f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        color = Scheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}


private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    paymentIntentClientSecret: String,
    merchantDisplayName: String,
    customerConfig: PaymentSheet.CustomerConfiguration? = null,
    googlePayConfig: PaymentSheet.GooglePayConfiguration? = null
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = merchantDisplayName,
            customer = customerConfig,
            allowsDelayedPaymentMethods = true,
            googlePay = googlePayConfig
        )
    )
}

