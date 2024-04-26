package com.messenger.toaster.dto

import com.messenger.toaster.data.ActionEnum

class ResponseMessageDTO(
    val id: Int,
    val text: String?,
    val sender: FriendDTO,
    val receiver: FriendDTO,
    val date: Long,
    val attachments: List<FileDTO>,
    val read: Boolean
){
    fun toWebsocketMessage(actionEnum: ActionEnum):ResponseWebsocketMessageDTO{
        return ResponseWebsocketMessageDTO(id, text, sender, receiver, date, attachments, read, actionEnum)
    }
}