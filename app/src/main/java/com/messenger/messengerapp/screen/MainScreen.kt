package com.messenger.messengerapp.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.messenger.messengerapp.R
import com.messenger.messengerapp.screen.mainSubscreen.News

@Composable
fun MainScreen() {
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
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            IconButton(
                onClick = { /*TODO*/ },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.DarkGray),
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
                onClick = { /*TODO*/ },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.DarkGray),
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
                onClick = { /*TODO*/ },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.DarkGray),
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
                onClick = { /*TODO*/ },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.DarkGray),
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