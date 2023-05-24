package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.api.UserApi
import com.messenger.messengerapp.dto.RequestUserDTO
import retrofit2.Call

private val userApi = RetrofitClient.getInstance().create(UserApi::class.java)

class UserApiImpl:UserApi {
    override fun registration(requestUserDTO: RequestUserDTO): Call<Unit> {
        return userApi.registration(requestUserDTO)
    }
}