package com.messenger.toaster.api

import com.messenger.toaster.dto.FileDTO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FileApi {
    @Multipart
    @POST("/file/upload")
    fun upload(@Header("Authorization") h1:String, @Part attachment: MultipartBody.Part): Call<FileDTO>

    @GET("/file/{id}")
    fun download(@Path("id")id:Int):Call<ResponseBody>
}