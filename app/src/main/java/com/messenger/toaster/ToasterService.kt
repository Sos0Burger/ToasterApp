package com.messenger.toaster

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToasterService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        sendRequestToServer()
        stopSelf()
    }

    private fun sendRequestToServer() {
        if (User.USER_ID != null) {
            val userApi = UserApiImpl()
            val response = userApi.logout(User.getCredentials())
            response.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Log.d("server", "Logout complete")
                    } else {
                        Log.d("server", "Logout incomplete")

                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("server", "Logout error")
                }
            })
        }
    }
}