package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.api.UserApi
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.RequestUserDTO
import retrofit2.Call

private val userApi = RetrofitClient.getInstance().create(UserApi::class.java)

class UserApiImpl:UserApi {
    override fun registration(requestUserDTO: RequestUserDTO): Call<Any> {
        return userApi.registration(requestUserDTO)
    }

    override fun auth(email: String, hash: String): Call<Any> {
        return userApi.auth(email, hash)
    }

    override fun getFriends(id: Int): Call<List<FriendDTO>> {
        return userApi.getFriends(id)
    }
}