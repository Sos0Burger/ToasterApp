package com.messenger.toaster

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.data.User.sharedPrefs
import com.messenger.toaster.screen.LoginScreen
import com.messenger.toaster.screen.MainScreen
import com.messenger.toaster.screen.RegistrationScreen
import com.messenger.toaster.ui.theme.ToasterTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences("user", MODE_PRIVATE)
        User.EMAIL = sharedPrefs.getString("email", null)
        User.PASSWORD = sharedPrefs.getString("password", null)
        User.USER_ID = sharedPrefs.getInt("user_id", -1)
        if (User.USER_ID == -1) User.USER_ID = null

        setContent {
            ToasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Screen()
                }
            }
        }
    }

    override fun onPause() {
        logout()
        super.onPause()
    }

    override fun onResume() {
        if (User.USER_ID != null) {
            val userApi = UserApiImpl()
            val response = userApi.auth(User.getCredentials())
            response.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    if (response.isSuccessful) {
                        Log.d("server", "Login complete")
                    } else {
                        Log.d("server", "Login incomplete")
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.d("server", "Login error")
                }
            })
        }
        super.onResume()
    }
    override fun onDestroy() {
        logout()
        super.onDestroy()
    }

    private fun logout() {
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

@Composable
fun Screen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (User.EMAIL == null) "Registration" else "Login"
    ) {
        composable("Registration") {
            RegistrationScreen {
                navController.navigate("Login")
            }
        }
        composable("Login") {
            LoginScreen(User.EMAIL != null,
                {
                    navController.navigate("Main") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                { navController.navigate("Registration") })
        }
        composable("Main") {
            MainScreen()
        }
    }
}