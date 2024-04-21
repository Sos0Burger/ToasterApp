package com.messenger.toaster.api.impl

import com.messenger.toaster.api.PostApi
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.data.SortingEnum
import com.messenger.toaster.dto.RequestCommentDTO
import com.messenger.toaster.dto.RequestPostDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import com.messenger.toaster.dto.ResponsePostDTO
import retrofit2.Call

private val postApi = RetrofitClient.getInstance().create(PostApi::class.java)

class PostApiImpl: PostApi {
    override fun getPosts(page: Int, token:String): Call<List<ResponsePostDTO>> {
        return postApi.getPosts(page, token)
    }

    override fun createPost(postDTO: RequestPostDTO, token:String): Call<ResponsePostDTO> {
        return postApi.createPost(postDTO, token)
    }

    override fun getPost(id: Int, h1: String): Call<ResponsePostDTO> {
        return postApi.getPost(id, h1)
    }

    override fun getPostComments(id: Int, sort:SortingEnum, h1: String): Call<List<ResponseCommentDTO>> {
        return postApi.getPostComments(id, sort,  h1)
    }

    override fun createComment(id: Int, commentDTO: RequestCommentDTO, h1: String) : Call<ResponseCommentDTO>{
        return postApi.createComment(id, commentDTO, h1)
    }

    override fun smashLike(id: Int, h1: String): Call<ResponsePostDTO> {
        return postApi.smashLike(id, h1)
    }
}