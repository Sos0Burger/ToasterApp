package com.messenger.toaster.api

import com.messenger.toaster.dto.RequestMessageDTO
import com.messenger.toaster.dto.ResponseMessageDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MessageApi {

    @POST("/message")
    fun send(@Body requestMessageDTO: RequestMessageDTO, @Header("Authorization") h1:String ) : Call<ResponseMessageDTO>

    @GET("/message/dialog")
    fun getDialog(@Query("companion")companion:Int, @Query("page") page:Int, @Header("Authorization") h1:String ): Call<List<ResponseMessageDTO>>
}