package com.messenger.messengerapp.dto

class RequestMessageDTO(val text:String?, val receiver:Int, val date:Long, val attachments: List<Int>) {
}