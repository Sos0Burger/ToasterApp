package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.api.UserApi
import com.messenger.messengerapp.dto.AuthDTO
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.UserDTO
import com.messenger.messengerapp.dto.UserProfileDTO
import com.messenger.messengerapp.dto.UserSettingsDTO
import retrofit2.Call

private val userApi = RetrofitClient.getInstance().create(UserApi::class.java)

class UserApiImpl:UserApi {
    override fun registration(userDTO: UserDTO): Call<UserProfileDTO> {
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

    override fun acceptFriendRequest(receiverid: Int, senderid: Int): Call<FriendDTO> {
        return userApi.acceptFriendRequest(receiverid, senderid)
    }

    override fun updatePicture(id: Int, auth: AuthDTO, url: String): Call<Unit> {
        return userApi.updatePicture(id, auth, url)
    }

    override fun updateNickname(id: Int, auth: AuthDTO, nickname: String): Call<Unit> {
        return userApi.updateNickname(id, auth, nickname)
    }

    override fun getSettings(id: Int): Call<UserSettingsDTO> {
        return userApi.getSettings(id)
    }

    override fun updateToken(id: Int, token: String): Call<Unit> {
        return userApi.updateToken(id, token)
    }

    override fun getUser(id: Int): Call<UserProfileDTO> {
        return userApi.getUser(id)
    }
}