package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.ResponsePostDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @GET("/user/{id}/post")
    fun getPost(@Path("id") id:Int, @Query("page") page:Int) : Call<List<ResponsePostDTO>>
}