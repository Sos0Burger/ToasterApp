package com.messenger.toaster.api

import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.dto.RequestEditMessageDTO
import com.messenger.toaster.dto.RequestMessageDTO
import com.messenger.toaster.dto.ResponseMessageDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApi {

    @POST("/message")
    fun send(@Body requestMessageDTO: RequestMessageDTO, @Header("Authorization") h1:String ) : Call<ResponseMessageDTO>

    @GET("/message/dialog")
    fun getDialog(@Query("companion")companion:Int, @Query("page") page:Int, @Header("Authorization") h1:String ): Call<List<ResponseMessageDTO>>

    @PUT("/message/{id}")
    fun editMessage(@Path("id")id:Int, @Body message:RequestEditMessageDTO,  @Header("Authorization") h1:String):Call<ResponseMessageDTO>

    @DELETE("/message/{id}")
    fun deleteMessage(@Path("id")id:Int, @Header("Authorization") h1:String): Call<Unit>
    @GET("/message/{id}/images")
    fun getMessageImages(@Path("id")id:Int, @Header("Authorization") h1:String):Call<List<FileDTO>>
}