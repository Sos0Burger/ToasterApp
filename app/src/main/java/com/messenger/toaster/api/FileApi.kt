package com.messenger.toaster.api

import com.messenger.toaster.dto.FileDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    @Multipart
    @POST("/file/upload")
    fun upload(@Header("Authorization") h1:String, @Part attachment: MultipartBody.Part): Call<FileDTO>
}