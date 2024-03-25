package com.messenger.messengerapp.data

import android.content.SharedPreferences
import com.messenger.messengerapp.dto.ResponseMessageDTO
import okhttp3.Credentials

object User {
    lateinit var sharedPrefs: SharedPreferences
    var EMAIL:String? = null
    var PASSWORD:String? = null
    var USER_ID:Int? = null
    var messages:MutableList<ResponseMessageDTO>? = null
    fun getCredentials():String{
        if (EMAIL == null && PASSWORD!=null) {
            return Credentials.basic(EMAIL!!, PASSWORD!!);
        }
        return ""
    }
}