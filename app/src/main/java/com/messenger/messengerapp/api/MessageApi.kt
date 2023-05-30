package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.RequestMessageDTO
import com.messenger.messengerapp.dto.ResponseMessageDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApi {

    @POST("/message")
    fun send(@Body requestMessageDTO: RequestMessageDTO) : Call<Unit>

    @GET("/message/{userid}/{companionid}/{page}")
    fun getDialog(@Path("userid") userId:Int, @Path("companionid")companionId:Int, @Path("page") page:Int ): Call<List<ResponseMessageDTO>>
}