package com.messenger.toaster.api.impl

import com.messenger.toaster.api.CommentApi
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.dto.RequestCommentDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import retrofit2.Call

private val commentApi = RetrofitClient.getInstance().create(CommentApi::class.java)
class CommentApiImpl: CommentApi {
    override fun smashLike(id: Int, h1: String): Call<ResponseCommentDTO> {
        return commentApi.smashLike(id, h1)
    }

    override fun editComment(
        id: Int,
        h1: String,
        comment: RequestCommentDTO
    ): Call<ResponseCommentDTO> {
        return commentApi.editComment(id, h1, comment)
    }

    override fun deleteComment(id: Int, h1: String): Call<Unit> {
        return commentApi.deleteComment(id, h1)
    }

}