package com.example.e_commorce_fashions.presentation.views.notification.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme

@Composable
fun NotificationView(navController: ()-> NavHostController) {
    Scaffold(
        containerColor = Scheme.primary
    ) { scaffoldPadding ->
        Box(Modifier.fillMaxSize().padding(scaffoldPadding).wrapContentSize(Alignment.Center)){
            Text("Notification View")
        }
    }
}

