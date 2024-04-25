package com.messenger.toaster.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.toaster.api.impl.FileApiImpl
import com.messenger.toaster.api.impl.MessageApiImpl
import com.messenger.toaster.converter.getFileName
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.RequestEditMessageDTO
import com.messenger.toaster.dto.RequestMessageDTO
import com.messenger.toaster.dto.ResponseMessageDTO
import com.messenger.toaster.requestbody.InputStreamRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class MessagesViewModel : ViewModel() {
    private val _images = MutableStateFlow<List<FileDTO>>(emptyList())
    val images = _images

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded

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

    fun editMessage(
        context: Context,
        id: Int,
        index: Int,
        message: MutableState<String>,
        messages: MutableList<ResponseMessageDTO>,
        uploadedImageIds: List<Int>,
        inputEnabled: MutableState<Boolean>,
        onEnd: () -> Unit
    ) {
        val messageApi = MessageApiImpl()
        val response = messageApi.editMessage(
            id,
            RequestEditMessageDTO(message.value, uploadedImageIds),
            User.getCredentials()
        )
        response.enqueue(object : Callback<ResponseMessageDTO> {
            override fun onResponse(
                call: Call<ResponseMessageDTO>,
                response: Response<ResponseMessageDTO>
            ) {
                if (response.isSuccessful) {
                    messages[index] = response.body()!!
                    message.value = ""
                    onEnd()
                } else {
                    val jsonObj =
                        if (response.errorBody() != null) response.errorBody()!!.byteString()
                            .utf8() else response.code().toString()
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT).show()
                }
                inputEnabled.value = true
            }

            override fun onFailure(call: Call<ResponseMessageDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                inputEnabled.value = true
            }
        })
    }

    fun sendMessage(
        context: Context,
        message: MutableState<String>,
        friendDTO: FriendDTO,
        messages: MutableList<ResponseMessageDTO>,
        uploadedImageIds: List<Int>,
        inputEnabled: MutableState<Boolean>,
        onEnd: () -> Unit
    ) {
        val messageApi = MessageApiImpl()
        val requestMessage = RequestMessageDTO(
            message.value,
            friendDTO.id,
            Date().time,
            uploadedImageIds
        )
        val response = messageApi.send(requestMessage, User.getCredentials())
        response.enqueue(object : Callback<ResponseMessageDTO> {
            override fun onResponse(
                call: Call<ResponseMessageDTO>,
                response: Response<ResponseMessageDTO>
            ) {
                if (response.isSuccessful) {
                    messages.add(0, response.body()!!)
                    message.value = ""
                    onEnd()
                } else {
                    val jsonObj =
                        if (response.errorBody() != null) response.errorBody()!!.byteString()
                            .utf8() else response.code().toString()
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT).show()
                }
                inputEnabled.value = true
            }

            override fun onFailure(call: Call<ResponseMessageDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                inputEnabled.value = true
            }
        })

    }

    fun deleteMessage(
        context: Context,
        id: Int,
        onEnd: () -> Unit
    ) {
        val messageApi = MessageApiImpl()
        val response = messageApi.deleteMessage(id, User.getCredentials())
        response.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onEnd()
                } else {
                    val jsonObj =
                        if (response.errorBody() != null) response.errorBody()!!.byteString()
                            .utf8() else response.code().toString()
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }
}