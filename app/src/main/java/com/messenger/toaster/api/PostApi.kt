package com.messenger.toaster.api

import com.messenger.toaster.data.SortingEnum
import com.messenger.toaster.dto.RequestCommentDTO
import com.messenger.toaster.dto.RequestPostDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import com.messenger.toaster.dto.ResponsePostDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @GET("/post")
    fun getPosts(@Query("page") page:Int, @Header("Authorization") h1:String) : Call<List<ResponsePostDTO>>

    @POST("/post")
    fun createPost(@Body postDTO: RequestPostDTO, @Header("Authorization") h1:String):Call<ResponsePostDTO>

    @GET("/post/{id}")
    fun getPost(@Path("id")id:Int, @Header("Authorization") h1:String):Call<ResponsePostDTO>

    @GET("/post/{id}/comments")
    fun getPostComments(@Path("id")id:Int, @Query("sort")sort:SortingEnum, @Header("Authorization") h1:String):Call<List<ResponseCommentDTO>>
    @POST("/post/{id}/comment")
    fun createComment(@Path("id")id:Int, @Body commentDTO: RequestCommentDTO, @Header("Authorization") h1:String): Call<ResponseCommentDTO>

    @PUT("/post/{id}/like")
    fun smashLike(@Path("id")id:Int,@Header("Authorization") h1:String):Call<ResponsePostDTO>
}