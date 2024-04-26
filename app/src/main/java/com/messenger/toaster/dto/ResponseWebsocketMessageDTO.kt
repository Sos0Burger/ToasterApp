package com.messenger.toaster.dto

import com.messenger.toaster.data.ActionEnum

class ResponseWebsocketMessageDTO(val id: Int,
                                  val text: String?,
                                  private val sender: FriendDTO,
                                  private val receiver: FriendDTO,
                                  val date: Long,
                                  val attachments: List<FileDTO>,
                                  private val read: Boolean,
                                  val action: ActionEnum){
    fun toMessageDTO():ResponseMessageDTO{
        return ResponseMessageDTO(id, text, sender, receiver, date, attachments, read )
    }
}