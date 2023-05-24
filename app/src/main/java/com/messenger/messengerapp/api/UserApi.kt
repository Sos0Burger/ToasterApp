package com.messenger.messengerapp.api

import com.messenger.messengerapp.dto.RequestUserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("user")
    fun registration(@Body requestUserDTO: RequestUserDTO): Call<Unit>
}