package com.messenger.toaster.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.messenger.toaster.api.impl.FileApiImpl
import com.messenger.toaster.api.impl.PostApiImpl
import com.messenger.toaster.converter.getFileName
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.RequestEditPostDTO
import com.messenger.toaster.dto.RequestPostDTO
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.requestbody.InputStreamRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class PostViewModel : ViewModel() {
    private val _images = MutableStateFlow<List<FileDTO>>(emptyList())
    val images = _images

    private val _isLoaded = MutableStateFlow(true)
    val isLoaded = _isLoaded

    private val _post = MutableStateFlow(
        ResponsePostDTO(
            0,
            null,
            FriendDTO(0, null, null),
            13233124,
            ArrayList(),
            0,
            false,
            0,
            null
        )
    )
    val post = _post

    private fun addImage(image: FileDTO) {
        val currentList = _images.value.toMutableList()
        currentList.add(image)
        _images.value = currentList
    }

    fun clearImages() {
        _images.value = emptyList()
    }

    fun remove(index: Int) {
        val currentList = _images.value.toMutableList()
        currentList.removeAt(index)
        _images.value = currentList
    }

    fun set(images: List<FileDTO>) {
        val currentList = images
        _images.value = currentList
    }

    fun upload(images: List<Uri>, context: Context) {
        viewModelScope.launch {
            _isLoaded.emit(false)
            clearImages()
            val files = images.toList()
            if (files.isNotEmpty()) {
                val fileApi = FileApiImpl()
                val multipartFiles = ArrayList<MultipartBody.Part>()
                val cR = context.contentResolver
                for (item in files) {
                    multipartFiles.add(
                        MultipartBody.Part.createFormData(
                            "attachment",
                            getFileName(cR, item),
                            InputStreamRequestBody(
                                cR.getType(item)!!.toMediaType(),
                                context.contentResolver,
                                item
                            )
                        )
                    )
                }
                for (item in multipartFiles.indices) {
                    val response = fileApi.upload(User.getCredentials(), multipartFiles[item])
                    response.enqueue(object : Callback<FileDTO> {
                        override fun onResponse(call: Call<FileDTO>, response: Response<FileDTO>) {
                            if (response.code() == 201) {
                                addImage(response.body()!!)
                            } else {
                                val jsonObj =
                                    if (response.errorBody() != null) response.errorBody()!!
                                        .byteString()
                                        .utf8() else response.code().toString()
                                Log.d(
                                    "server",
                                    response.code().toString()
                                )
                                Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT).show()

                            }
                            if (item == multipartFiles.size - 1) {
                                viewModelScope.launch {
                                    _isLoaded.emit(true)
                                }
                            }
                        }

                        override fun onFailure(call: Call<FileDTO>, t: Throwable) {
                            Log.d("server", t.message.toString())
                            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                            if (item == multipartFiles.size - 1) {
                                viewModelScope.launch {
                                    _isLoaded.emit(true)
                                }
                            }
                        }
                    })
                }
            }
        }
    }

    fun sendPost(
        context: Context,
        text: MutableState<String>,
        uploadedImageIds: List<Int>,
        inputEnabled: MutableState<Boolean>,
        navController: NavController
    ) {
        val postApi = PostApiImpl()
        val response = postApi.createPost(
            RequestPostDTO(
                text = text.value,
                date = Date().time,
                attachments = uploadedImageIds
            ),
            User.getCredentials()
        )
        response.enqueue(object : Callback<ResponsePostDTO> {
            override fun onResponse(
                call: Call<ResponsePostDTO>,
                response: Response<ResponsePostDTO>
            ) {
                if (response.isSuccessful) {
                    navController.navigate("profile/" + User.USER_ID!!)
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
                    inputEnabled.value = true
                }
            }

            override fun onFailure(call: Call<ResponsePostDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                inputEnabled.value = true
            }

        })

    }

    fun getPost(id: String, context: Context, text: MutableState<String>, onReturn: () -> Unit) {
        val postApi = PostApiImpl()
        val response = postApi.getPost(id.toInt(), User.getCredentials())
        response.enqueue(object : Callback<ResponsePostDTO> {
            override fun onResponse(
                call: Call<ResponsePostDTO>,
                response: Response<ResponsePostDTO>
            ) {
                if (response.isSuccessful) {
                    _post.value = response.body()!!
                    text.value =
                        if (response.body()!!.text != null) response.body()!!.text!! else ""
                    set(response.body()!!.attachments)
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
                    onReturn()
                }

            }

            override fun onFailure(call: Call<ResponsePostDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                onReturn()
            }
        })
    }

    fun updatePost(
        id: Int,
        index:Int,
        post: RequestEditPostDTO,
        context: Context,
        inputEnabled: MutableState<Boolean>,
        allNewsViewModel: AllNewsViewModel? = null,
        profilePostViewModel: ProfilePostViewModel? = null,
        onComplete: () -> Unit
    ) {
        val postApi = PostApiImpl()
        val response = postApi.updatePost(
            id,
            post,
            User.getCredentials()
        )
        response.enqueue(object : Callback<ResponsePostDTO> {
            override fun onResponse(
                call: Call<ResponsePostDTO>,
                response: Response<ResponsePostDTO>
            ) {
                if (response.isSuccessful) {
                    if (allNewsViewModel!=null){
                        allNewsViewModel.update(index, response.body()!!)
                    }
                    else profilePostViewModel?.update(index, response.body()!!)
                    onComplete()
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
                    inputEnabled.value = true
                }
            }

            override fun onFailure(call: Call<ResponsePostDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                inputEnabled.value = true
            }

        })
    }
}