package com.messenger.toaster.dto

class ResponseChatDTO (
    val user: Int,
    var nickname: String?,
    var latest: String,
    var date: Long,
    var unread: Int,
    var image: FileDTO?,
    var online: Boolean
)