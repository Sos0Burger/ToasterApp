package com.messenger.toaster.api.impl

import com.messenger.toaster.api.PostApi
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.dto.RequestPostDTO
import com.messenger.toaster.dto.ResponsePostDTO
import retrofit2.Call

private val postApi = RetrofitClient.getInstance().create(PostApi::class.java)

class PostApiImpl: PostApi {
    override fun getPost(page: Int, token:String): Call<List<ResponsePostDTO>> {
        return postApi.getPost(page, token)
    }

    override fun createPost(postDTO: RequestPostDTO, token:String): Call<ResponsePostDTO> {
        return postApi.createPost(postDTO, token)
    }
}