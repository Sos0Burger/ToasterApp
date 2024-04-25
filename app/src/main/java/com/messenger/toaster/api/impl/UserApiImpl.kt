package com.messenger.toaster.api.impl

import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.UserApi
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.ResponseChatDTO
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

    override fun auth(h1:String): Call<Int> {
        return userApi.auth(h1)
    }

    override fun getFriends(h1:String): Call<List<FriendDTO>> {
        return userApi.getFriends(h1)
    }

    override fun sendFriendRequest(receiver: Int, h1:String): Call<FriendDTO> {
        return userApi.sendFriendRequest(receiver, h1)
    }

    override fun getPending(h1:String): Call<List<FriendDTO>> {
        return userApi.getPending(h1)
    }

    override fun getSent(h1:String): Call<List<FriendDTO>> {
        return userApi.getSent(h1)
    }

    override fun acceptFriendRequest(sender: Int, h1:String): Call<FriendDTO> {
        return userApi.acceptFriendRequest(sender, h1)
    }

    override fun updatePicture(file:Int, h1:String): Call<Unit> {
        return userApi.updatePicture(file, h1)
    }

    override fun updateNickname(nickname: String, h1:String): Call<Unit> {
        return userApi.updateNickname(nickname, h1)
    }

    override fun getSettings(h1:String): Call<UserSettingsDTO> {
        return userApi.getSettings(h1)
    }

    override fun updateToken(token: String, h1: String ): Call<Unit> {
        return userApi.updateToken(token, h1)
    }

    override fun getUser(h1:String): Call<UserProfileDTO> {
        return userApi.getUser(h1)
    }

    override fun getFeed(h1:String, page:Int): Call<List<ResponsePostDTO>> {
        return userApi.getFeed(h1, page)
    }

    override fun getFeed(query: String, page: Int, h1: String): Call<List<ResponsePostDTO>> {
        return userApi.getFeed(query, page, h1)
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

    override fun getUserChats(h1: String): Call<List<ResponseChatDTO>> {
        return userApi.getUserChats(h1)
    }

    override fun getFriendFeed(query: String, page: Int, h1: String): Call<List<ResponsePostDTO>> {
        return userApi.getFriendFeed(query, page, h1)
    }

    override fun logout(h1: String): Call<Unit> {
        return userApi.logout(h1)
    }
}