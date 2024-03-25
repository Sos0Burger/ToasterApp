package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.api.UserApi
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.ResponsePostDTO
import com.messenger.messengerapp.dto.UserDTO
import com.messenger.messengerapp.dto.UserProfileDTO
import com.messenger.messengerapp.dto.UserSettingsDTO
import retrofit2.Call

private val userApi = RetrofitClient.getInstance().create(UserApi::class.java)

class UserApiImpl:UserApi {
    override fun registration(token:String, userDTO: UserDTO): Call<UserProfileDTO> {
        return userApi.registration(token, userDTO)
    }

    override fun auth(token:String): Call<Any> {
        return userApi.auth(token)
    }

    override fun getFriends(token:String): Call<List<FriendDTO>> {
        return userApi.getFriends(token)
    }

    override fun sendFriendRequest(receiver: Int, token:String): Call<FriendDTO> {
        return userApi.sendFriendRequest(receiver, token)
    }

    override fun getPending(token:String): Call<List<FriendDTO>> {
        return userApi.getPending(token)
    }

    override fun getSent(token:String): Call<List<FriendDTO>> {
        return userApi.getSent(token)
    }

    override fun acceptFriendRequest(sender: Int,token:String): Call<FriendDTO> {
        return userApi.acceptFriendRequest(sender, token)
    }

    override fun updatePicture(file:Int, token:String): Call<Unit> {
        return userApi.updatePicture(file, token)
    }

    override fun updateNickname(nickname: String, token:String): Call<Unit> {
        return userApi.updateNickname(nickname, token)
    }

    override fun getSettings(token:String): Call<UserSettingsDTO> {
        return userApi.getSettings(token)
    }

    override fun updateToken(firebase: String, token: String ): Call<Unit> {
        return userApi.updateToken(firebase, token)
    }

    override fun getUser(token:String): Call<UserProfileDTO> {
        return userApi.getUser(token)
    }

    override fun getFeed(token:String, page:Int): Call<List<ResponsePostDTO>> {
        return userApi.getFeed(token, page)
    }
}