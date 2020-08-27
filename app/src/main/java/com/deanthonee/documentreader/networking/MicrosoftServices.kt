package com.deanthonee.documentreader.networking

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface MicrosoftServices {

    @JvmSuppressWildcards
    @POST("vision/v3.0/read/analyze")
    suspend fun startReadOperation(@HeaderMap header: Map<String, Any>, @Body body: Map<String, Any>): Any

    companion object {

        const val MY_BASE_URL = "https://deanthoneeking.cognitiveservices.azure.com/"
        const val BASE_URL = "https://westus.api.cognitive.microsoft.com/"

        fun create(): MicrosoftServices {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(MicrosoftServices::class.java)
        }
    }
}