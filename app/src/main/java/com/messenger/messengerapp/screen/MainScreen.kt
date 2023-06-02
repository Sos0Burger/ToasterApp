package com.messenger.messengerapp.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.messenger.messengerapp.R
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.screen.mainSubscreen.Chat
import com.messenger.messengerapp.screen.mainSubscreen.Friends
import com.messenger.messengerapp.screen.mainSubscreen.News
import com.messenger.messengerapp.screen.mainSubscreen.Settings
import com.messenger.messengerapp.ui.theme.Orange

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "Харош", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Даууун", Toast.LENGTH_SHORT).show()
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            SideEffect {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    val friendDTOShare = remember {
        mutableStateOf(FriendDTO(-1, null, null))
    }
    val currentScreen = remember {
        mutableStateOf("news")
    }
    val iconButtonModifier = Modifier
        .height(48.dp)
        .width(48.dp)
    val iconTextModifier = Modifier.size(32.dp, 32.dp)
    val navController = rememberNavController()
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        NavHost(
            navController = navController,
            startDestination = "news"
        ) {
            composable("news") {
                News()
            }
            composable("chat") {
                Chat(friendDTOShare) {
                    navController.navigate("chatMessages")
                }
            }
            composable("chatMessages") {
                ChatMessagesScreen(friendDTO = friendDTOShare.value)
            }
            composable("friends") {
                Friends()
            }
            composable("profile") {
                News()
            }
            composable("settings") {
                Settings()
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            IconButton(
                onClick = {
                    currentScreen.value = "news"
                    navController.navigate("news")
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor =
                    if (currentScreen.value == "news")
                        Orange
                    else
                        Color.DarkGray
                ),
                modifier = iconButtonModifier
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.news_icon),
                    contentDescription = null,
                    modifier = iconTextModifier
                )

                Text(
                    text = "Новости",
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 34.dp)
                )
            }
            IconButton(
                onClick = {
                    currentScreen.value = "chat"
                    navController.navigate("chat")
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor =
                    if (currentScreen.value == "chat")
                        Orange
                    else
                        Color.DarkGray
                ),
                modifier = iconButtonModifier
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.chat_icon),
                    contentDescription = null,
                    modifier = iconTextModifier
                )

                Text(text = "Чат", fontSize = 10.sp, modifier = Modifier.padding(top = 34.dp))
            }
            IconButton(
                onClick = {
                    currentScreen.value = "friends"
                    navController.navigate("friends")
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor =
                    if (currentScreen.value == "friends")
                        Orange
                    else
                        Color.DarkGray
                ),
                modifier = iconButtonModifier
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.friend_icon),
                    contentDescription = null,
                    modifier = iconTextModifier
                )
                Text(
                    text = "Друзья",
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 34.dp)
                )
            }
            IconButton(
                onClick = {
                    currentScreen.value = "profile"
                    /*TODO*/
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor =
                    if (currentScreen.value == "profile")
                        Orange
                    else
                        Color.DarkGray
                ),
                modifier = iconButtonModifier
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.account_icon),
                    contentDescription = null,
                    modifier = iconTextModifier
                )
                Text(
                    text = "Профиль",
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 34.dp)
                )
            }
            IconButton(
                onClick = {
                    currentScreen.value = "settings"
                    navController.navigate("settings")
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor =
                    if (currentScreen.value == "settings")
                        Orange
                    else
                        Color.DarkGray
                ),
                modifier = iconButtonModifier
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.settings_icon),
                    contentDescription = null,
                    modifier = iconTextModifier
                )
                Text(
                    text = "Опции",
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 34.dp)
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        MainScreen()
    }
}