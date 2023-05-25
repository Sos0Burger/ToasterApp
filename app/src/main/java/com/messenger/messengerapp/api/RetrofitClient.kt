package com.messenger.messengerapp.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    fun getInstance():Retrofit{
        return Retrofit.Builder().baseUrl("http://192.168.1.178:8080/").addConverterFactory(MoshiConverterFactory.create()).build()
    }
}