package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.PostApi
import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.dto.RequestPostDTO
import com.messenger.messengerapp.dto.ResponsePostDTO
import retrofit2.Call

private val postApi = RetrofitClient.getInstance().create(PostApi::class.java)

class PostApiImpl: PostApi {
    override fun getPost(id: Int, page: Int): Call<List<ResponsePostDTO>> {
        return postApi.getPost(id, page)
    }

    override fun createPost(postDTO: RequestPostDTO): Call<ResponsePostDTO> {
        return postApi.createPost(postDTO)
    }
}