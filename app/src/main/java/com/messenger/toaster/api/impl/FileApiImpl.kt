package com.messenger.toaster.api.impl

import com.messenger.toaster.api.FileApi
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.dto.FileDTO
import okhttp3.MultipartBody
import retrofit2.Call

private val fileApi = RetrofitClient.getInstance().create(FileApi::class.java)

class FileApiImpl:FileApi {
    override fun upload(
        h1: String,
        attachment: MultipartBody.Part
    ): Call<FileDTO> {
        return fileApi.upload(h1, attachment)
    }
}