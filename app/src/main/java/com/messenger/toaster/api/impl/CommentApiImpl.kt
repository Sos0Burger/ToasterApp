package com.messenger.toaster.api.impl

import com.messenger.toaster.api.CommentApi
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.dto.ResponseCommentDTO
import retrofit2.Call

private val commentApi = RetrofitClient.getInstance().create(CommentApi::class.java)
class CommentApiImpl: CommentApi {
    override fun smashLike(id: Int, h1: String): Call<ResponseCommentDTO> {
        return commentApi.smashLike(id, h1)
    }

}