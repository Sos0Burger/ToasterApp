package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.FileDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    @Multipart
    @POST("/file/upload")
    fun upload(@Part attachment: MultipartBody.Part): Call<FileDTO>
}