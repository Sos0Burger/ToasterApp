package com.messenger.toaster.dto

class ResponsePostDTO(
    var id: Int,
    var text: String?,
    var creator: FriendDTO,
    var date: Long,
    var attachments: List<FileDTO>,
    var likes: Int,
    var isLiked: Boolean,
    var comments: Int,
    var popularComment:ResponseCommentDTO?
)