package com.messenger.toaster.dto

class ResponseCommentDTO(
    var id: Int,
    var text: String,
    var creator: FriendDTO,
    var date: Long,
    var post: Int,
    var likes: Int,
    var isLiked: Boolean
)