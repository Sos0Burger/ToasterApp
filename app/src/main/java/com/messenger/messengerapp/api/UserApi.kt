package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.AuthDTO
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.UserDTO
import com.messenger.messengerapp.dto.UserSettingsDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface UserApi {
    @POST("user")
    fun registration(@Body userDTO: UserDTO): Call<Unit>

    @GET("user/auth")
    fun auth(@Header("email") email:String, @Header("hash") hash:String): Call<Any>

    @GET("user/{id}/friends")
    fun getFriends(@Path(value = "id") id:Int): Call<List<FriendDTO>>

    @POST("user/friend-request/{senderid}/{receiverid}")
    fun sendFriendRequest(@Path("senderid") senderid:Int, @Path("receiverid") receiverid:Int) : Call<FriendDTO>

    @GET("/user/{id}/pending")
    fun getPending(@Path("id") id:Int):Call<List<FriendDTO>>

    @GET("/user/{id}/sent")
    fun getSent(@Path("id") id:Int):Call<List<FriendDTO>>

    @POST("user/friends/{receiverid}/{senderid}")
    fun acceptFriendRequest(@Path("receiverid") receiverid: Int, @Path("senderid") senderid: Int) : Call<Unit>

    @PUT("/user/{id}/picture")
    fun updatePicture(@Path("id") id:Int, @Body auth:AuthDTO, @Header("url") url:String) : Call<Unit>

    @PUT("/user/{id}/nickname")
    fun updateNickname(@Path("id") id:Int, @Body auth:AuthDTO, @Header("nickname") nickname:String) : Call<Unit>

    @GET("/user/{id}/settings")
    fun getSettings(@Path("id") id:Int) : Call<UserSettingsDTO>

    @PUT("/user/{id}/firebase")
    fun updateToken(@Path("id")id:Int, @Body token:String) : Call<Unit>

}