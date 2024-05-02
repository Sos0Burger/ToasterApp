package com.messenger.toaster.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.toaster.api.impl.FileApiImpl
import com.messenger.toaster.api.impl.MessageApiImpl
import com.messenger.toaster.converter.getFileName
import com.messenger.toaster.data.ActionEnum
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.RequestEditMessageDTO
import com.messenger.toaster.dto.RequestMessageDTO
import com.messenger.toaster.dto.ResponseMessageDTO
import com.messenger.toaster.dto.ResponseWebsocketMessageDTO
import com.messenger.toaster.repository.WebSocketRepository
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
    private val webSocketRepository: WebSocketRepository = WebSocketRepository()

    init {
        viewModelScope.launch {
            webSocketRepository.connectWebSocket(this@MessagesViewModel)
        }
    }

    private val _images = MutableStateFlow<List<FileDTO>>(emptyList())
    val images = _images

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded

    private val _messages =
        MutableStateFlow<SnapshotStateList<ResponseMessageDTO>>(SnapshotStateList())
    val messages = _messages

    private val _page = MutableStateFlow(-1)
    val page = _page

    private fun addImage(image: FileDTO) {
        val currentList = _images.value.toMutableList()
        currentList.add(image)
        _images.value = currentList
    }

    fun addMessage(message: ResponseMessageDTO) {
        _messages.value.add(0, message)
    }

    fun updateMessage(message: ResponseMessageDTO) {
        _messages.value.forEach(action = {
            if (it.id == message.id){
                _messages.value[_messages.value.indexOf(it)] = message
                return
            }
        })


    }

    fun removeMessage(message: ResponseMessageDTO) {
        _messages.value.forEach(action = {
            if (it.id == message.id){
                _messages.value.remove(it)
                return
            }
        })
    }

    fun clearImages() {
        _images.value = emptyList()
    }

    fun removeImage(index: Int) {
        val currentList = _images.value.toMutableList()
        currentList.removeAt(index)
        _images.value = currentList
    }

    fun setImages(images: List<FileDTO>) {
        val currentList = images
        _images.value = currentList
    }

    fun sendMessageToWebsocket(messages: List<ResponseWebsocketMessageDTO>, onEnd: () -> Unit) {
        viewModelScope.launch {
            webSocketRepository.sendMessage(messages, onEnd)
        }
    }

    fun refresh(id:Int, context: Context){
        _messages.value = SnapshotStateList()
        _page.value = 0
        loadMessages(id, context)
    }
    fun loadNextPage(id:Int, context: Context){
        _page.value++
        loadMessages(id, context)
    }
    private fun loadMessages(id:Int, context: Context) {
        viewModelScope.launch {
            val messageApi = MessageApiImpl()
            val response = messageApi.getDialog(id, page.value, User.getCredentials())
            response.enqueue(object : Callback<List<ResponseMessageDTO>> {
                override fun onResponse(
                    call: Call<List<ResponseMessageDTO>>,
                    response: Response<List<ResponseMessageDTO>>
                ) {
                    if (response.isSuccessful) {
                        _messages.value.addAll(response.body()!!)
                        val read = mutableListOf<ResponseWebsocketMessageDTO>()
                        response.body()!!.forEach(action = {
                            if(!it.read && it.receiver.id==User.USER_ID){
                                it.read = true
                                read.add(it.toWebsocketMessage(ActionEnum.UPDATE))
                            }
                        })
                        if (read.isNotEmpty()){
                            sendMessageToWebsocket(read){}
                        }

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
                override fun onFailure(call: Call<List<ResponseMessageDTO>>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            })
        }
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
        message: MutableState<String>,
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
                    sendMessageToWebsocket(
                        listOf(
                            response.body()!!.toWebsocketMessage(ActionEnum.UPDATE)
                        )
                    ) {
                        onEnd()
                    }
                    message.value = ""
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
                    message.value = ""
                    sendMessageToWebsocket(
                        listOf(
                            response.body()!!.toWebsocketMessage(ActionEnum.CREATE)
                        )
                    ) {
                        onEnd()
                    }
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
        message: ResponseMessageDTO,
        onEnd: () -> Unit
    ) {
        val messageApi = MessageApiImpl()
        val response = messageApi.deleteMessage(message.id, User.getCredentials())
        response.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    sendMessageToWebsocket(listOf(message.toWebsocketMessage(ActionEnum.DELETE))) {
                        onEnd()
                    }
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