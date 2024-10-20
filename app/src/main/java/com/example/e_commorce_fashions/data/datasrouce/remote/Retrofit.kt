package com.example.e_commorce_fashions.data.datasrouce.remote

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val APPLICATION_JSON = "application/json"
private const val CONTENT_TYPE = "content-type"
private const val ACCEPT = "accept"
private const val AUTHORIZATION = "authorization"
private const val DEFAULT_LANGUAGE = "language"

class RetrofitBuilder {
    fun getPaymentApiServices(context: Context): PaymentApiServices {
        val interceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .header(DEFAULT_LANGUAGE, "en")
                .header(AUTHORIZATION, "Bearer ${context.getString(R.string.stripe_key)}")
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // Build Retrofit instance
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentApiServices::class.java)
    }

    fun getPaypalPaymentApiServices(context: Context): PaypalPaymentApiServices {
        val interceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .header(DEFAULT_LANGUAGE, "en")
//                .header(AUTHORIZATION, "Bearer ${context.getString(R.string.paypal_client_id)}")
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // Build Retrofit instance
        return Retrofit.Builder()
            .baseUrl(Constants.PAYPAL_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaypalPaymentApiServices::class.java)
    }
}