package com.messenger.toaster.api.impl

import com.messenger.toaster.api.MessageApi
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.dto.RequestEditMessageDTO
import com.messenger.toaster.dto.RequestMessageDTO
import com.messenger.toaster.dto.ResponseMessageDTO
import retrofit2.Call

private val messageApi = RetrofitClient.getInstance().create(MessageApi::class.java)

class MessageApiImpl : MessageApi {
    override fun send(requestMessageDTO: RequestMessageDTO, token:String) : Call<ResponseMessageDTO> {
        return messageApi.send(requestMessageDTO, token)
    }

    override fun getDialog(companion: Int, page: Int, token: String) : Call<List<ResponseMessageDTO>> {
        return messageApi.getDialog(companion, page, token)
    }

    override fun editMessage(
        id: Int,
        message: RequestEditMessageDTO,
        h1: String
    ): Call<ResponseMessageDTO> {
        return messageApi.editMessage(id, message, h1)
    }

    override fun deleteMessage(id: Int, h1: String): Call<Unit> {
        return messageApi.deleteMessage(id, h1)
    }

    override fun getMessageImages(id: Int, h1: String): Call<List<FileDTO>> {
        return messageApi.getMessageImages(id, h1)
    }
}