package com.example.e_commorce_fashions

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.e_commorce_fashions.app.App
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher

lateinit var GOOGLE_PAY_LAUNCHER: GooglePayLauncher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)){ view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }

        GOOGLE_PAY_LAUNCHER = GooglePayLauncher(
            activity = this,
            config = GooglePayLauncher.Config(
                environment = GooglePayEnvironment.Test,
                merchantCountryCode = "US",
                merchantName = this.getString(R.string.app_name)
            ),
            readyCallback = {
                Log.d("TAG", "onPaymentSheetResult: Ready")
            },
            resultCallback = {
                Log.d("TAG", "onPaymentSheetResult: $it")
            }
        )

        enableEdgeToEdge()

        setContent {
            App(this)
        }
    }
}


// keytool -exportcert -alias androiddebugkey -keystore "C:\Users\XPRISTO\.android\debug.keystore" | "C:\Users\XPRISTO\Downloads\Compressed\openssl-0.9.8k_X64\\bin\openssl" sha1 -binary | "C:\Users\XPRISTO\Downloads\Compressed\openssl-0.9.8k_X64\bin\openssl" base64
// keytool -exportcert -alias key -keystore "D:\Kotlin Projects\e_commorce_fashions\key.jks" | "C:\Users\XPRISTO\Downloads\Compressed\openssl-0.9.8k_X64\\bin\openssl" sha1 -binary | "C:\Users\XPRISTO\Downloads\Compressed\openssl-0.9.8k_X64\bin\openssl" base64
// keytool -list -v -keystore "D:\Kotlin Projects\e_commorce_fashions\key.jks" -alias key // the release key