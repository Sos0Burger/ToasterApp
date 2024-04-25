package com.messenger.toaster.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.ResponsePostDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePostViewModel: ViewModel() {
    private val _posts = MutableStateFlow<SnapshotStateList<ResponsePostDTO>>(SnapshotStateList())
    val posts = _posts

    private val _currentPage = MutableStateFlow(-1)
    val currentPage: StateFlow<Int> = _currentPage

    fun update(index: Int, post:ResponsePostDTO){
        _posts.value[index] = post
    }
    fun remove(index: Int){
        _posts.value.removeAt(index)
    }
    fun loadNextPage(id:Int, query:String, context:Context) {
        _currentPage.value++
        loadPosts(id, query, context)
    }
    fun refresh(id:Int, query:String, context:Context){
        _posts.value = SnapshotStateList()
        _currentPage.value = -1
        loadNextPage(id, query, context)
    }
    private fun loadPosts(id:Int, query:String, context: Context) {
        viewModelScope.launch {
            val response = UserApiImpl().getUserPosts(id, query, currentPage.value, User.getCredentials())
            response.enqueue(object:Callback<List<ResponsePostDTO>>{
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
                }

                override fun onFailure(call: Call<List<ResponsePostDTO>>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            })


        }
    }
}