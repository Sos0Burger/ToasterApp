package com.messenger.toaster.api

import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.ResponseChatDTO
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.dto.UserDTO
import com.messenger.toaster.dto.UserProfileDTO
import com.messenger.toaster.dto.UserSettingsDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {
    @POST("user")
    fun registration(@Query("token") token: String, @Body userDTO: UserDTO): Call<UserProfileDTO>

    @GET("user/auth")
    fun auth(@Header("Authorization") h1: String): Call<Int>

    @GET("user/friends")
    fun getFriends(@Header("Authorization") h1: String): Call<List<FriendDTO>>

    @POST("user/friend-request")
    fun sendFriendRequest(
        @Query("receiver") receiver: Int,
        @Header("Authorization") h1: String
    ): Call<FriendDTO>

    @GET("/user/pending")
    fun getPending(@Header("Authorization") h1: String): Call<List<FriendDTO>>

    @GET("/user/sent")
    fun getSent(@Header("Authorization") h1: String): Call<List<FriendDTO>>

    @POST("/user/friends")
    fun acceptFriendRequest(
        @Query("sender") sender: Int,
        @Header("Authorization") h1: String
    ): Call<FriendDTO>

    @PUT("/user/picture")
    fun updatePicture(@Query("file") file: Int, @Header("Authorization") h1: String): Call<Unit>

    @PUT("/user/nickname")
    fun updateNickname(
        @Query("nickname") nickname: String,
        @Header("Authorization") h1: String
    ): Call<Unit>

    @GET("/user/settings")
    fun getSettings(@Header("Authorization") h1: String): Call<UserSettingsDTO>

    @PUT("/user/firebase")
    fun updateToken(@Query("token") token: String, @Header("Authorization") h1: String): Call<Unit>

    @GET("/user")
    fun getUser(@Header("Authorization") h1: String): Call<UserProfileDTO>

    @GET("/user/feed")
    fun getFeed(
        @Header("Authorization") h1: String,
        @Query("page") page: Int
    ): Call<List<ResponsePostDTO>>

    @POST("/user/code")
    fun sendCode(@Query("email") email: String): Call<Unit>

    @DELETE("/user/friend")
    fun deleteFriend(@Query("id") id: Int, @Header("Authorization") h1: String): Call<Unit>

    @DELETE("/user/friend-request")
    fun deleteFriendRequest(@Query("id") id: Int, @Header("Authorization") h1: String): Call<Unit>

    @GET("user/search")
    fun searchUsers(
        @Query("query") query: String,
        @Header("Authorization") h1: String
    ): Call<List<FriendDTO>>

    @GET("user/{id}")
    fun getUserProfile(
        @Path("id") id: Int,
        @Header("Authorization") h1: String
    ): Call<UserProfileDTO>

    @GET("user/{id}/posts")
    fun getUserPosts(
        @Path("id") id: Int,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Header("Authorization") h1: String
    ): Call<List<ResponsePostDTO>>

    @GET("user/chats")
    fun getUserChats(@Header("Authorization") h1: String): Call<List<ResponseChatDTO>>
}