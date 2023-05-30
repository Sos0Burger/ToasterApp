package com.messenger.messengerapp.dto

class ResponseMessageDTO(val text:String?, val sender:FriendDTO, val receiver:FriendDTO, val date:Long, val attachments: List<FileDTO>)