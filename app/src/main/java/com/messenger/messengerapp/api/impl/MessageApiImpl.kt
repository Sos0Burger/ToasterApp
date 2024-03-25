package com.messenger.messengerapp.api.impl

import com.messenger.messengerapp.api.MessageApi
import com.messenger.messengerapp.api.RetrofitClient
import com.messenger.messengerapp.dto.RequestMessageDTO
import com.messenger.messengerapp.dto.ResponseMessageDTO
import retrofit2.Call

private val messageApi = RetrofitClient.getInstance().create(MessageApi::class.java)

class MessageApiImpl : MessageApi {
    override fun send(requestMessageDTO: RequestMessageDTO, token:String) : Call<ResponseMessageDTO> {
        return messageApi.send(requestMessageDTO, token)
    }

    override fun getDialog(companion: Int, page: Int, token: String) : Call<List<ResponseMessageDTO>> {
        return messageApi.getDialog(companion, page, token)
    }
}