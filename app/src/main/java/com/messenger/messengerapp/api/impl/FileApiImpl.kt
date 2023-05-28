package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.FileApi
import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.dto.FileDTO
import okhttp3.MultipartBody
import retrofit2.Call

private val fileApi = RetrofitClient.getInstance().create(FileApi::class.java)

class FileApiImpl:FileApi {
    override fun upload(attachment: MultipartBody.Part): Call<FileDTO> {
        return fileApi.upload(attachment)
    }
}