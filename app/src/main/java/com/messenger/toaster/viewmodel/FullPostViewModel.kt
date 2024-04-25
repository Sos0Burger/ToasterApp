package com.messenger.toaster.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.messenger.toaster.api.impl.PostApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import com.messenger.toaster.dto.ResponsePostDTO
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullPostViewModel : ViewModel() {
    private val _post = MutableStateFlow(
        ResponsePostDTO(
            1,
            null,
            FriendDTO(-1, null, null),
            19923293,
            emptyList(),
            0,
            false,
            0,
            ResponseCommentDTO(-1, "", FriendDTO(-1, null, null), 1909949324, 1, 0, false)
        )
    )
    val post = _post

    private val _isPostLoading = MutableStateFlow(true)
    val isPostLoading = _isPostLoading

    fun getPost(id:String, context:Context) {
        val postApi = PostApiImpl()
        val response = postApi.getPost(id.toInt(), User.getCredentials())
        response.enqueue(object : Callback<ResponsePostDTO> {
            override fun onResponse(
                call: Call<ResponsePostDTO>,
                response: Response<ResponsePostDTO>
            ) {
                if (response.isSuccessful) {
                    _post.value = response.body()!!
                } else {
                    val jsonObj = if (response.errorBody() != null) {
                        response.errorBody()!!.byteString().utf8()
                    } else {
                        response.code().toString()
                    }

                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT)
                        .show()
                }
                isPostLoading.value = false
            }

            override fun onFailure(call: Call<ResponsePostDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                isPostLoading.value = false
            }
        })
    }
}