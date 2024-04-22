package com.messenger.toaster.dto

import com.messenger.toaster.data.FriendStatus

data class UserProfileDTO(var id:Int, var nickname:String?, var friends:List<FriendDTO>, var image:FileDTO?, var status:FriendStatus )