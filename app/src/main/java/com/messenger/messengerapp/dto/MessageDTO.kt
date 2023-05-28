package com.messenger.messengerapp.dto

class MessageDTO(val text:String?, val sender:FriendDTO, val receiver:FriendDTO, date:Long, attachments: List<FileDTO>)