package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.ResponsePostDTO
import com.messenger.messengerapp.dto.UserDTO
import com.messenger.messengerapp.dto.UserProfileDTO
import com.messenger.messengerapp.dto.UserSettingsDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface UserApi {
    @POST("user")
    fun registration(@Query("token") token:String, @Body userDTO: UserDTO): Call<UserProfileDTO>

    @GET("user/auth")
    fun auth(@Header("Authorization") h1:String): Call<Any>

    @GET("user/friends")
    fun getFriends(@Header("Authorization") h1:String): Call<List<FriendDTO>>

    @POST("user/friend-request")
    fun sendFriendRequest(@Query("receiver") receiver:Int, @Header("Authorization") h1:String) : Call<FriendDTO>

    @GET("/user/pending")
    fun getPending(@Header("Authorization") h1:String):Call<List<FriendDTO>>

    @GET("/user/sent")
    fun getSent(@Header("Authorization") h1:String):Call<List<FriendDTO>>

    @POST("user/friends")
    fun acceptFriendRequest(@Query("sender") sender: Int, @Header("Authorization") h1:String) : Call<FriendDTO>

    @PUT("/user/{id}/picture")
    fun updatePicture(@Query("file") file:Int, @Header("Authorization") h1:String) : Call<Unit>

    @PUT("/user/{id}/nickname")
    fun updateNickname(@Query("nickname") nickname:String, @Header("Authorization") h1:String) : Call<Unit>

    @GET("/user/{id}/settings")
    fun getSettings(@Header("Authorization") h1:String) : Call<UserSettingsDTO>

    @PUT("/user/firebase")
    fun updateToken(@Query("token") token:String, @Header("Authorization") h1:String) : Call<Unit>

    @GET("/user")
    fun getUser(@Header("Authorization") h1:String):Call<UserProfileDTO>

    @GET("/user/feed")
    fun getFeed(@Header("Authorization") h1:String, @Query("page")page:Int) : Call<List<ResponsePostDTO>>

}