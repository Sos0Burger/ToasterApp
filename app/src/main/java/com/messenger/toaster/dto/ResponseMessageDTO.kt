package com.messenger.toaster.dto

class ResponseMessageDTO(val id:Int, val text:String?, val sender:FriendDTO, val receiver:FriendDTO, val date:Long, val attachments: List<FileDTO>)