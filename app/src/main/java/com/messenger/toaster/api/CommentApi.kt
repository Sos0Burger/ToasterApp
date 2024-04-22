package com.messenger.toaster.api

import com.messenger.toaster.dto.RequestCommentDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface CommentApi {
    @PUT("/comment/{id}/like")
    fun smashLike(
        @Path("id") id: Int,
        @Header("Authorization") h1: String
    ): Call<ResponseCommentDTO>

    @PUT("comment/{id}")
    fun editComment(
        @Path("id") id: Int,
        @Header("Authorization") h1: String,
        @Body comment: RequestCommentDTO
    ): Call<ResponseCommentDTO>

    @DELETE("comment/{id}")
    fun deleteComment(@Path("id") id: Int, @Header("Authorization") h1: String):Call<Unit>
}