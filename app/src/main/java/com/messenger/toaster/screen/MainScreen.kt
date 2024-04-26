package com.messenger.toaster.screen

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.messenger.toaster.R
import com.messenger.toaster.data.ImageMode
import com.messenger.toaster.data.PostMode
import com.messenger.toaster.data.User
import com.messenger.toaster.screen.mainSubscreen.Chat
import com.messenger.toaster.screen.mainSubscreen.CreatePostScreen
import com.messenger.toaster.screen.mainSubscreen.Friends
import com.messenger.toaster.screen.mainSubscreen.News
import com.messenger.toaster.screen.mainSubscreen.Profile
import com.messenger.toaster.screen.mainSubscreen.Settings
import com.messenger.toaster.ui.theme.Orange
import com.messenger.toaster.viewmodel.AllNewsViewModel
import com.messenger.toaster.viewmodel.FullPostViewModel
import com.messenger.toaster.viewmodel.ProfilePostViewModel

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
    val currentScreen = remember {
        mutableStateOf("news")
    }
    val iconButtonModifier = Modifier
        .height(48.dp)
        .width(48.dp)
    val iconTextModifier = Modifier.size(32.dp, 32.dp)
    val navController = rememberNavController()
    val newsViewModel: AllNewsViewModel = viewModel()
    var profilePostViewModel: ProfilePostViewModel = viewModel()
    val fullPostViewModel: FullPostViewModel = viewModel()
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        NavHost(
            navController = navController,
            startDestination = "news"
        ) {
            composable("news") {
                News(navController = navController, allNewsViewModel = newsViewModel)
            }
            composable("chat") {
                Chat(navController)
            }
            composable("chatMessages/{userID}") { backStackEntry ->
                ChatMessagesScreen(
                    backStackEntry.arguments?.getString("userID")!!,
                    navController = navController,
                    onBack = {
                        navController.popBackStack()
                    },
                    onProfile = {
                        navController.navigate(
                            "profile/" + backStackEntry.arguments?.getString("userID")!!
                        )
                    })
            }
            composable("friends") {
                Friends(navController)
            }
            composable("profile/{userId}") { backStackEntry ->
                profilePostViewModel = viewModel()
                Profile(
                    backStackEntry.arguments?.getString("userId")!!,
                    navController,
                    profilePostViewModel
                ) {
                    navController.navigate("postCreation")

                }
            }
            composable("settings") {
                Settings() {
                    navController.popBackStack()
                }
            }
            composable("postCreation") {
                CreatePostScreen(mode = PostMode.CREATE, navController = navController)
            }
            composable("news/postEdit/{id}/{index}") { backStackEntry ->
                CreatePostScreen(
                    id = backStackEntry.arguments?.getString("id")!!,
                    mode = PostMode.EDIT,
                    navController = navController,
                    newsViewModel = newsViewModel,
                    index = backStackEntry.arguments?.getString("index")!!.toInt()
                )
            }
            composable("profile/postEdit/{id}/{index}") { backStackEntry ->
                CreatePostScreen(
                    id = backStackEntry.arguments?.getString("id")!!,
                    mode = PostMode.EDIT,
                    navController = navController,
                    profilePostViewModel = profilePostViewModel,
                    index = backStackEntry.arguments?.getString("index")!!.toInt()
                )
            }
            composable("news/post/{postID}/{index}") { backStackEntry ->
                FullPost(
                    id = backStackEntry.arguments?.getString("postID")!!,
                    navController = navController,
                    "news",
                    backStackEntry.arguments?.getString("index")!!.toInt(),
                    fullPostViewModel
                ) {
                    newsViewModel.remove(backStackEntry.arguments?.getString("index")!!.toInt())
                }
            }
            composable("profile/post/{postID}/{index}") { backStackEntry ->
                FullPost(
                    id = backStackEntry.arguments?.getString("postID")!!,
                    navController = navController,
                    "profile",
                    backStackEntry.arguments?.getString("index")!!.toInt(),
                    fullPostViewModel
                ) {
                    profilePostViewModel.remove(
                        backStackEntry.arguments?.getString("index")!!.toInt()
                    )
                }
            }
            composable("post/{postID}/images/{initial}") { backStackEntry ->
                FullScreenImages(
                    id = backStackEntry.arguments?.getString("postID")!!,
                    initial = backStackEntry.arguments?.getString("initial")!!,
                    ImageMode.POST
                ) {
                    navController.popBackStack()
                }
            }
            composable("message/{messageID}/images/{initial}") { backStackEntry ->
                FullScreenImages(
                    id = backStackEntry.arguments?.getString("messageID")!!,
                    initial = backStackEntry.arguments?.getString("initial")!!,
                    ImageMode.MESSAGE
                ) {
                    navController.popBackStack()
                }
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
                    navController.navigate("profile/" + User.USER_ID!!)
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