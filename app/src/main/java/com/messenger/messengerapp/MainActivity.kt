package com.messenger.messengerapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.messenger.messengerapp.data.User
import com.messenger.messengerapp.data.User.sharedPrefs
import com.messenger.messengerapp.screen.LoginScreen
import com.messenger.messengerapp.screen.MainScreen
import com.messenger.messengerapp.screen.RegistrationScreen
import com.messenger.messengerapp.ui.theme.MessengerAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        User.USER_ID = sharedPrefs.getInt("user_id", -1)
        if (User.USER_ID == -1) User.USER_ID = null
        User.EMAIL = sharedPrefs.getString("email", null)
        User.HASH = sharedPrefs.getString("hash", null)

        setContent {
            MessengerAppTheme {
                // A surface container using the 'background' color from the theme
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