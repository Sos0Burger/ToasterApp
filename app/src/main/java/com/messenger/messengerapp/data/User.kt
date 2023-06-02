package com.messenger.messengerapp.data

import android.content.SharedPreferences
import com.messenger.messengerapp.dto.ResponseMessageDTO

object User {
    lateinit var sharedPrefs: SharedPreferences
    var  USER_ID:Int? = null
    var EMAIL:String? = null
    var HASH:String? = null
    var messages:MutableList<ResponseMessageDTO>? = null
}