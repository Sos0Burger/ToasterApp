package com.messenger.toaster.api

import com.messenger.toaster.dto.RequestPostDTO
import com.messenger.toaster.dto.ResponsePostDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PostApi {
    @GET("/user/post")
    fun getPost(@Query("page") page:Int, @Header("Authorization") h1:String) : Call<List<ResponsePostDTO>>

    @POST("/user/post")
    fun createPost(@Body postDTO: RequestPostDTO, @Header("Authorization") h1:String):Call<ResponsePostDTO>
}