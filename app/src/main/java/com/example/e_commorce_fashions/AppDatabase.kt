package com.example.e_commorce_fashions

import android.app.Application
import android.util.Log
import com.example.e_commorce_fashions.app.LOCAL_MAIN_ACTIVITY
import com.example.e_commorce_fashions.app.utils.Di
import com.stripe.android.PaymentConfiguration
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import dagger.hilt.android.HiltAndroidApp

class AppDatabaseAndPreferences : Application(){
    override fun onCreate() {
        super.onCreate()
//        Di.database(this)
        Di.googleAuthUiClient.provide(this)
        Di.preferences.provide(this)
        Di.paymentApiServices.provide(this)
        Di.paypalPaymentApiServices.provide(this)

        PaymentConfiguration.init(this, this.getString(R.string.publishable_key))


//        val uploader = UploadFakeData()
//        uploader.uploadPopularData()
//        uploader.uploadNewArrival()
//        uploader.fetchImageUrls()
//        uploader.upload(this)
    }
}

//@HiltAndroidApp
//class App : Application()


