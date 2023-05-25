package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.RequestUserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {
    @POST("user")
    fun registration(@Body requestUserDTO: RequestUserDTO): Call<Unit>

    @GET("user/auth")
    fun auth(@Header("email") email:String, @Header("hash") hash:String): Call<Any>
}