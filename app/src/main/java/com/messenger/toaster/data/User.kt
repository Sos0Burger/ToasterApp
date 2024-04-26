package com.messenger.toaster.data

import android.content.SharedPreferences
import okhttp3.Credentials

object User {
    lateinit var sharedPrefs: SharedPreferences
    var EMAIL:String? = null
    var PASSWORD:String? = null
    var USER_ID:Int? = null
    fun getCredentials():String{
        if (EMAIL!= null && PASSWORD!=null) {
            return Credentials.basic(EMAIL!!, PASSWORD!!)
        }
        return ""
    }
    fun getCredentials(email:String, password:String):String{
        return Credentials.basic(email, password)
    }
}