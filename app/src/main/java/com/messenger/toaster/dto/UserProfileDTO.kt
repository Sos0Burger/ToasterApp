package com.messenger.toaster.dto

import com.messenger.toaster.data.FriendStatus

class UserProfileDTO(var id:Int, var nickname:String?, var friends:List<FriendDTO>, var image:FileDTO?, var status:FriendStatus )