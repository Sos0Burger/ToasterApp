package com.messenger.toaster.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.ResponsePostDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendNewsViewModel: ViewModel() {
    private val _posts = MutableStateFlow<List<ResponsePostDTO>>(emptyList())
    val posts = _posts

    private val _currentPage = MutableStateFlow(-1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _isRefreshing = MutableStateFlow(false)

    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()


    fun loadNextPage(query:String, context: Context) {
        _currentPage.value++
        loadPosts(query, context)
    }

    fun refresh(query:String, context:Context){
        viewModelScope.launch {
            _isRefreshing.emit(true)
            _posts.value = emptyList()
            _currentPage.value = -1
            loadNextPage(query, context)
        }
    }
    private fun loadPosts(query:String, context: Context) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val response = UserApiImpl().getFriendFeed(query, currentPage.value, User.getCredentials())
            response.enqueue(object: Callback<List<ResponsePostDTO>> {
                override fun onResponse(
                    call: Call<List<ResponsePostDTO>>,
                    response: Response<List<ResponsePostDTO>>
                ) {
                    if (response.isSuccessful){
                        _posts.value += response.body()!!
                    }
                    else{
                        _currentPage.value--
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
                    viewModelScope.launch {
                        _isLoading.emit(false)
                        _isRefreshing.emit(false)
                    }
                }

                override fun onFailure(call: Call<List<ResponsePostDTO>>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                    viewModelScope.launch {
                        _currentPage.value--
                        _isLoading.emit(false)
                        _isRefreshing.emit(false)
                    }

                }
            })
        }
    }

}