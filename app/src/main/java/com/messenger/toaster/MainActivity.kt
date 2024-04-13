package com.messenger.toaster

import android.content.Context
import android.os.Bundle
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
import com.messenger.toaster.data.User
import com.messenger.toaster.data.User.sharedPrefs
import com.messenger.toaster.screen.LoginScreen
import com.messenger.toaster.screen.MainScreen
import com.messenger.toaster.screen.RegistrationScreen
import com.messenger.toaster.ui.theme.ToasterTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences("user", Context.MODE_PRIVATE)
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
                        popUpTo(navController.graph.id){
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