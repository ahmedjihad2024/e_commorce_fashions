package com.example.e_commorce_fashions.presentation.resources.config.navigator

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.views.auth.login.view.LoginView
import com.example.e_commorce_fashions.presentation.views.auth.login.view_state.LoginViewState
import com.example.e_commorce_fashions.presentation.views.auth.login.view_state.LoginViewStateFactory
import com.example.e_commorce_fashions.presentation.views.auth.sign_up.view.SignUpView
import com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state.SignUpViewModelFactory
import com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state.SignUpViewState
import com.example.e_commorce_fashions.presentation.views.auth.success_register.view.SuccessRegisterView
import com.example.e_commorce_fashions.presentation.views.on_boarding.view.OnBoardingView
import com.example.e_commorce_fashions.presentation.views.splash.view.SplashView
import com.example.e_commorce_fashions.presentation.views.auth.welcome.view.AuthWelcomeView
import com.example.e_commorce_fashions.presentation.views.choice_location.views.ChoiceLocationView
import com.example.e_commorce_fashions.presentation.views.favorites.view.FavoritesView
import com.example.e_commorce_fashions.presentation.views.favorites.view_state.FavoritesViewState
import com.example.e_commorce_fashions.presentation.views.favorites.view_state.FavoritesViewStateFactory
import com.example.e_commorce_fashions.presentation.views.layout.view.LayoutView
import com.example.e_commorce_fashions.presentation.views.my_orders.view.MyOrdersView
import com.example.e_commorce_fashions.presentation.views.payment_method.view_state.PaymentMethodViewState
import com.example.e_commorce_fashions.presentation.views.payment_method.view_state.PaymentMethodViewStateFactory
import com.example.e_commorce_fashions.presentation.views.payment_method.views.PaymentMethodView
import com.example.e_commorce_fashions.presentation.views.personal_details.view.PersonalDetailsView
import com.example.e_commorce_fashions.presentation.views.product_details.view.ProductDetailsView
import com.example.e_commorce_fashions.presentation.views.product_details.view_state.ProductDetailsViewState
import com.example.e_commorce_fashions.presentation.views.product_details.view_state.ProductDetailsViewStateFactory
import com.example.e_commorce_fashions.presentation.views.search.view.SearchView
import com.example.e_commorce_fashions.presentation.views.search.view_state.SearchViewState
import com.example.e_commorce_fashions.presentation.views.search.view_state.SearchViewStateFactory
import com.example.e_commorce_fashions.presentation.views.view_all.view.ViewAllProductsView
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsStateView
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsStateViewFactory

sealed class Views(val route: String) {
    data object SplashView : Views("splash-view")
    data object OnBoardingView : Views("on-boarding-view")
    data object AuthWelcomeView : Views("auth-welcome-view")
    data object LoginView : Views("login-view")
    data object SignUpView : Views("sign-up-view")
    data object SuccessRegisterView : Views("success-register-view")
    data object LayoutView : Views("layout-view")
    data object ViewAllView : Views("view-all-view")
    data object ProductDetailsView : Views("product-details-view")
    data object ChoiceLocationView : Views("choice-location-view")
    data object PaymentMethodView : Views("payment-method-view")
    data object SearchView : Views("search-view")
    data object PersonalDetailsView : Views("personal-details-view")
    data object MyOrdersView : Views("my-orders-view")
    data object FavoritesView: Views("favorites-view")
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Views.SplashView.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(800))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { (-it / 2.5f).toInt() },
                animationSpec = tween(800)
            )
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { (-it / 2.5).toInt() }, animationSpec = tween(800))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(800)
            )
        },
    ) {
        composable(Views.SplashView.route) {
            SplashView(navController)
        }
        composable(Views.OnBoardingView.route) {
            OnBoardingView(navController)
        }
        composable(Views.AuthWelcomeView.route) {
            AuthWelcomeView(navController)
        }
        composable(Views.LoginView.route) {
            val uiState: LoginViewState = viewModel(factory = LoginViewStateFactory(Di.repository))
            LoginView(navController, uiState)
        }
        composable(Views.SignUpView.route) {
            val uiState: SignUpViewState =
                viewModel(factory = SignUpViewModelFactory(Di.repository))
            SignUpView(navController, uiState)
        }
        composable(Views.SuccessRegisterView.route) {
            SuccessRegisterView(navController)
        }
        composable(Views.LayoutView.route) {
            LayoutView { navController }
        }
        composable(Views.ViewAllView.route) {
            val listener = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Di.sharedData.value.clearData("view-all-products")
                        it.lifecycle.removeObserver(this)
                    }
                }
            }
            it.lifecycle.addObserver(listener)
            val uiState: ViewAllProductsStateView =
                viewModel(factory = ViewAllProductsStateViewFactory(Di.repository))
            ViewAllProductsView(navController = { navController }, uiState = uiState)
        }

        composable(Views.ProductDetailsView.route) {
            val listener = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Di.sharedData.value.clearData("product-details")
                        it.lifecycle.removeObserver(this)
                    }
                }
            }
            it.lifecycle.addObserver(listener)
            val uiState: ProductDetailsViewState =
                viewModel(
                    factory = ProductDetailsViewStateFactory(
                        Di.repository,
                        Di.sharedData.value.getData<ProductDetails>("product-details"),
                         Di.sharedData.value.getData<FavoritesViewState>("favorites-view-model")
                    )
                )
            ProductDetailsView({ navController }, uiState)
        }

        composable(Views.ChoiceLocationView.route) {
            val listener = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Di.sharedData.value.clearData("amount")
                        it.lifecycle.removeObserver(this)
                    }
                }
            }
            it.lifecycle.addObserver(listener)
            ChoiceLocationView{ navController }
        }

        composable(Views.PaymentMethodView.route){
            val listener = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Di.sharedData.value.clearData("payment-method")
                        it.lifecycle.removeObserver(this)
                    }
                }
            }
            it.lifecycle.addObserver(listener)
            val uiState = viewModel<PaymentMethodViewState>(factory = PaymentMethodViewStateFactory(Di.repository))
            PaymentMethodView({navController}){ uiState }
        }

        composable(Views.SearchView.route){

            val listener = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Di.sharedData.value.clearData("product-details")
                        it.lifecycle.removeObserver(this)
                    }
                }
            }
            it.lifecycle.addObserver(listener)

            val uiState = viewModel<SearchViewState>(factory = SearchViewStateFactory())
            SearchView({ navController }){
                uiState
            }

        }

        composable(Views.PersonalDetailsView.route){
            PersonalDetailsView { navController }
        }

        composable(Views.MyOrdersView.route){
            MyOrdersView { navController }
        }

        composable(Views.FavoritesView.route) {
            val listener = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Di.sharedData.value.clearData("favorites-view-model")
                        it.lifecycle.removeObserver(this)
                    }
                }
            }
            it.lifecycle.addObserver(listener)
            val viewModel = viewModel<FavoritesViewState>(factory = FavoritesViewStateFactory(Di.repository))
            FavoritesView({ navController }){
                viewModel
            }
        }
    }
}
