package com.messenger.toaster.api

import com.messenger.toaster.dto.ResponseCommentDTO
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface CommentApi {
    @PUT("/comment/{id}/like")
    fun smashLike(@Path("id")id:Int, @Header("Authorization") h1:String):Call<ResponseCommentDTO>
}