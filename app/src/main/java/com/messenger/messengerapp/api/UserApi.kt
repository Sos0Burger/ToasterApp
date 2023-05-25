package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.RequestUserDTO
import com.messenger.messengerapp.exception.ExceptionMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {
    @POST("user")
    fun registration(@Body requestUserDTO: RequestUserDTO): Call<Unit>

    @GET("auth")
    fun auth(@Header("email") email:String, @Header("hash") hash:String): Call<ExceptionMessage>
}