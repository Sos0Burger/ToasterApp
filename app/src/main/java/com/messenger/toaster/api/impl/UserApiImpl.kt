package com.messenger.toaster.api.impl

import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.UserApi
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.dto.UserDTO
import com.messenger.toaster.dto.UserProfileDTO
import com.messenger.toaster.dto.UserSettingsDTO
import retrofit2.Call

private val userApi = RetrofitClient.getInstance().create(UserApi::class.java)

class UserApiImpl:UserApi {
    override fun registration(token:String, userDTO: UserDTO): Call<UserProfileDTO> {
        return userApi.registration(token, userDTO)
    }

    override fun auth(token:String): Call<Int> {
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

    override fun sendCode(email: String): Call<Unit> {
        return userApi.sendCode(email)
    }

    override fun deleteFriend(id: Int, h1: String) :Call<Unit>{
        return userApi.deleteFriend(id, h1)
    }

    override fun deleteFriendRequest(id: Int, h1: String):Call<Unit> {
        return userApi.deleteFriendRequest(id, h1)
    }

    override fun searchUsers(query: String, h1: String): Call<List<FriendDTO>> {
        return userApi.searchUsers(query, h1)
    }

    override fun getUserProfile(id: Int, h1: String): Call<UserProfileDTO> {
        return userApi.getUserProfile(id, h1)
    }

    override fun getUserPosts(
        id: Int,
        query: String,
        page: Int,
        h1: String
    ): Call<List<ResponsePostDTO>> {
        return userApi.getUserPosts(id, query, page, h1)
    }
}