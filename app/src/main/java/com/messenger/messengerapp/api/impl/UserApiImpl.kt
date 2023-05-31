package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.api.UserApi
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.UserDTO
import retrofit2.Call

private val userApi = RetrofitClient.getInstance().create(UserApi::class.java)

class UserApiImpl:UserApi {
    override fun registration(userDTO: UserDTO): Call<Any> {
        return userApi.registration(userDTO)
    }

    override fun auth(email: String, hash: String): Call<Any> {
        return userApi.auth(email, hash)
    }

    override fun getFriends(id: Int): Call<List<FriendDTO>> {
        return userApi.getFriends(id)
    }

    override fun sendFriendRequest(senderid: Int, receiverid: Int): Call<FriendDTO> {
        return userApi.sendFriendRequest(senderid, receiverid)
    }

    override fun getPending(id: Int): Call<List<FriendDTO>> {
        return userApi.getPending(id)
    }

    override fun getSent(id: Int): Call<List<FriendDTO>> {
        return userApi.getSent(id)
    }

    override fun acceptFriendRequest(receiverid: Int, senderid: Int): Call<Unit> {
        return userApi.acceptFriendRequest(receiverid, senderid)
    }
}