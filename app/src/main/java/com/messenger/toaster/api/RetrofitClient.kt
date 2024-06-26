package com.messenger.toaster.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    fun getInstance():Retrofit{
        return Retrofit
            .Builder()
            .client(OkHttpClient()
                .newBuilder()
                .readTimeout(15, TimeUnit.SECONDS)
                .build())
            .baseUrl("http://158.255.5.102:8080/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}