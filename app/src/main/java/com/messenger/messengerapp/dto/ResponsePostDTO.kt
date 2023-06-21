package com.messenger.messengerapp.dto

class ResponsePostDTO(var id:Int, var text:String?, var creator:FriendDTO, date:Long, val attachments: List<FileDTO>)