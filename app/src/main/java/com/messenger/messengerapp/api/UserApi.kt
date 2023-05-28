package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.RequestUserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface UserApi {
    @POST("user")
    fun registration(@Body requestUserDTO: RequestUserDTO): Call<Any>

    @GET("user/auth")
    fun auth(@Header("email") email:String, @Header("hash") hash:String): Call<Any>

    @GET("user/{id}/friends")
    fun getFriends(@Path(value = "id") id:Int): Call<List<FriendDTO>>
}