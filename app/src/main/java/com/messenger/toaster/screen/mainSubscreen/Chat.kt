@file:Suppress("DEPRECATION")

package com.messenger.toaster.screen.mainSubscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.composable.ChatItem
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.ResponseChatDTO
import com.messenger.toaster.infomessage.InfoSnackBar
import com.messenger.toaster.ui.theme.Orange
import com.messenger.toaster.viewmodel.UpdateViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun Chat(navController: NavController) {
    val chatList = remember {
        mutableStateListOf<ResponseChatDTO>()
    }
    val isChatListEmpty = remember {
        mutableStateOf(false)
    }
    val chatCount = remember {
        derivedStateOf {
            chatList.size
        }
    }

    val errorMessage = remember {
        mutableStateOf("")
    }

    val snackBarState = remember {
        mutableStateOf(false)
    }
    val isChatLoading = remember{
        mutableStateOf(true)
    }
    fun getChats() {
        isChatLoading.value = false
        chatList.clear()
        val userApi = UserApiImpl()
        val response = userApi.getUserChats(User.getCredentials())
        response.enqueue(object : Callback<List<ResponseChatDTO>> {
            override fun onResponse(
                call: Call<List<ResponseChatDTO>>,
                response: Response<List<ResponseChatDTO>>
            ) {
                if (response.isSuccessful) {
                    chatList.addAll(response.body()!!.toMutableList())
                    if (chatList.isEmpty()) isChatListEmpty.value = true
                } else {
                    val jsonObj = if (response.errorBody() != null) {
                        response.errorBody()!!.byteString().utf8()
                    } else {
                        response.code().toString()
                    }

                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    errorMessage.value = jsonObj
                    snackBarState.value = true
                }
            }

            override fun onFailure(call: Call<List<ResponseChatDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                errorMessage.value = "Ошибка подключения"
                snackBarState.value = true
            }
        })
    }

    val viewModel: UpdateViewModel = viewModel()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    if (isChatLoading.value){
        isChatLoading.value = false
        viewModel.refresh { getChats() }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp), color = Color.Black
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Чаты",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, end = 16.dp, start = 16.dp)
            )

            if (isChatListEmpty.value) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "У вас нет чатов", color = Orange, fontSize = 20.sp)
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    if (snackBarState.value) {
                        InfoSnackBar(text = errorMessage, snackBarState = snackBarState)
                    }
                }
            } else {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = { viewModel.refresh { getChats() } },
                    indicator = { state: SwipeRefreshState, trigger ->
                        SwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = trigger,
                            scale = true,
                            backgroundColor = Color.DarkGray,
                            contentColor = Orange,
                            shape = CircleShape
                        )
                    }
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(count = chatCount.value) { index ->
                            ChatItem(chat = chatList[index]) {
                                navController.navigate("chatMessages/" + chatList[index].user)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    Chat(rememberNavController())
}