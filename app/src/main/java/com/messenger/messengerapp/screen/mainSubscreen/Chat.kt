@file:Suppress("DEPRECATION")

package com.messenger.messengerapp.screen.mainSubscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.messenger.messengerapp.api.impl.UserApiImpl
import com.messenger.messengerapp.data.User
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.infomessage.InfoSnackBar
import com.messenger.messengerapp.ui.theme.Orange
import com.messenger.messengerapp.viewmodel.UpdateViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun Chat() {
    var friendList: MutableList<FriendDTO>? = null
    val friendListNullOrEmptyState = remember {
        mutableStateOf(friendList.isNullOrEmpty())
    }
    val isFriendListNull = remember {
        mutableStateOf(true)
    }
    val friendsCount = remember {
        mutableStateOf(0)
    }

    val errorMessage = remember {
        mutableStateOf("")
    }

    val snackBarState = remember {
        mutableStateOf(false)
    }

    fun getFriends() {
        val userApi = UserApiImpl()
        val response = userApi.getFriends(User.USER_ID!!)
        response.enqueue(object : Callback<List<FriendDTO>> {
            override fun onResponse(
                call: Call<List<FriendDTO>>,
                response: Response<List<FriendDTO>>
            ) {
                if (response.code() == 200) {
                    friendList = response.body()!!.toMutableList()
                    friendsCount.value = response.body()!!.size
                    friendListNullOrEmptyState.value = friendList.isNullOrEmpty()
                    isFriendListNull.value = friendList == null
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server",
                        response.code().toString() + " " + jsonObj.getString("message")
                    )
                    isFriendListNull.value = true
                    friendListNullOrEmptyState.value = true
                    friendsCount.value = 0
                    errorMessage.value = jsonObj.getString("message")
                    snackBarState.value = true
                }
            }

            override fun onFailure(call: Call<List<FriendDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                isFriendListNull.value = true
                friendListNullOrEmptyState.value = true
                friendsCount.value = 0
                errorMessage.value = "Ошибка подключения"
                snackBarState.value = true
            }
        })
    }
    getFriends()
    val viewModel: UpdateViewModel = viewModel()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp), color = Color.Black
    ) {
        if (friendListNullOrEmptyState.value) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isFriendListNull.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp, 64.dp),
                        color = Orange
                    )
                } else {
                    Text(text = "У вас нет друзей", color = Orange, fontSize = 20.sp)
                }
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
                onRefresh = { viewModel.refresh { getFriends() } },
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
                    items(count = friendsCount.value) { index ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { /*TODO*/ },
                                shape = RectangleShape,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                AsyncImage(
                                    model = friendList!![index].image
                                        ?: "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                )
                                Column(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .fillMaxWidth(1f)
                                ) {
                                    Text(
                                        text = friendList!![index].nickname
                                            ?: "Альтернативное имя не указано", color = Color.White
                                    )
                                    Text(
                                        text = "ID: " + friendList!![index].id.toString(),
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
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
    Chat()
}