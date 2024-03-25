@file:Suppress("DEPRECATION")

package com.messenger.messengerapp.screen.mainSubscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.messenger.messengerapp.R
import com.messenger.messengerapp.api.impl.UserApiImpl
import com.messenger.messengerapp.data.User
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.infomessage.InfoSnackBar
import com.messenger.messengerapp.ui.theme.Orange
import com.messenger.messengerapp.viewmodel.UpdateViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Friends() {
    val friendId = remember {
        mutableStateOf("")
    }
    val friendList = remember {
        mutableStateListOf<FriendDTO>()
    }
    val friendPendingList = remember {
        mutableStateListOf<FriendDTO>()
    }
    val friendSentList = remember {
        mutableStateListOf<FriendDTO>()
    }
    val isFriendPendingListEmpty = remember {
        derivedStateOf {
            friendPendingList.isEmpty()
        }
    }
    val isFriendSentListEmpty = remember {
        derivedStateOf {
            friendSentList.isEmpty()
        }
    }
    val isFriendListEmpty = remember {
        derivedStateOf {
            friendList.isEmpty()
        }
    }

    val friendsCount = remember {
        derivedStateOf {
            friendList.size
        }
    }
    val friendsPendingCount = remember {
        derivedStateOf {
            friendPendingList.size
        }
    }
    val friendsSentCount = remember {
        derivedStateOf {
            friendSentList.size
        }
    }
    val clickedItem = remember {
        mutableStateOf(-1)
    }

    val errorMessage = remember {
        mutableStateOf("")
    }

    val snackBarState = remember {
        mutableStateOf(false)
    }
    val pageState = rememberPagerState()

    val coroutineScope = rememberCoroutineScope()
    val buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    val context = LocalContext.current

    fun getFriends() {
        val userApi = UserApiImpl()
        val response =
            when (pageState.currentPage) {
                2 -> userApi.getSent(User.getCredentials())
                1 -> userApi.getPending(
                    User.getCredentials()
                )

                else -> userApi.getFriends(User.getCredentials())
            }
        response.enqueue(object : Callback<List<FriendDTO>> {
            override fun onResponse(
                call: Call<List<FriendDTO>>,
                response: Response<List<FriendDTO>>
            ) {
                if (response.code() == 200) {
                    when (pageState.currentPage) {
                        2 -> {
                            friendSentList.clear()
                            friendSentList.addAll(response.body()!!)
                        }

                        1 -> {
                            friendPendingList.clear()
                            friendPendingList.addAll(response.body()!!)
                        }

                        else -> {
                            friendList.clear()
                            friendList.addAll(response.body()!!)
                        }
                    }

                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server",
                        response.code().toString() + " " + jsonObj.getString("message")
                    )
                    errorMessage.value = jsonObj.getString("message")
                    snackBarState.value = true
                }
            }

            override fun onFailure(call: Call<List<FriendDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                errorMessage.value = "Ошибка подключения"
                snackBarState.value = true
            }
        })
    }

    fun sendFriendRequest() {
        val userApi = UserApiImpl()
        val response = userApi.sendFriendRequest(friendId.value.toInt(), User.getCredentials())
        response.enqueue(object : Callback<FriendDTO> {
            override fun onResponse(
                call: Call<FriendDTO>,
                response: Response<FriendDTO>
            ) {
                if (response.code() == 201) {
                    friendSentList.add(response.body()!!)
                    friendId.value = ""
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT)
                        .show()

                }
            }

            override fun onFailure(call: Call<FriendDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun acceptFriendRequest() {
        val userApi = UserApiImpl()
        val response = userApi.acceptFriendRequest(friendPendingList[clickedItem.value].id, User.getCredentials() )
        response.enqueue(object : Callback<FriendDTO> {
            override fun onResponse(
                call: Call<FriendDTO>,
                response: Response<FriendDTO>
            ) {
                if (response.code() == 201) {
                    friendPendingList.removeAt(clickedItem.value)
                    friendList.add(response.body()!!)
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT)
                        .show()

                }
            }

            override fun onFailure(call: Call<FriendDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }

        })
    }
    if (pageState.currentPage <= 2) {
        getFriends()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp), color = Color.Black
    ) {
        Column(modifier = Modifier.fillMaxSize(1f)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                Button(
                    onClick = { coroutineScope.launch { pageState.scrollToPage(0) } },
                    colors = buttonColors
                ) {
                    Text(
                        text = "Друзья",
                        color = if (pageState.currentPage == 0) Orange else Color.DarkGray
                    )
                }
                Button(
                    onClick = { coroutineScope.launch { pageState.scrollToPage(1) } },
                    colors = buttonColors
                ) {
                    Text(
                        text = "Входящие",
                        color = if (pageState.currentPage == 1) Orange else Color.DarkGray
                    )
                }
                Button(
                    onClick = { coroutineScope.launch { pageState.scrollToPage(2) } },
                    colors = buttonColors
                ) {
                    Text(
                        text = "Исходящие",
                        color = if (pageState.currentPage == 2) Orange else Color.DarkGray
                    )
                }
            }
            HorizontalPager(
                pageCount = 3,
                state = pageState,
                modifier = Modifier.fillMaxSize(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        FriendList(
                            friendList = friendList,
                            friendsCount = friendsCount,
                            isFriendListEmpty = isFriendListEmpty,
                            snackBarState = snackBarState,
                            errorMessage = errorMessage,
                            clickedItem = clickedItem,
                            getFriends = { getFriends() }) {
                            //todo пока ничего

                        }
                    }

                    1 -> {
                        FriendList(
                            friendList = friendPendingList,
                            friendsCount = friendsPendingCount,
                            isFriendListEmpty = isFriendPendingListEmpty,
                            snackBarState = snackBarState,
                            errorMessage = errorMessage,
                            clickedItem = clickedItem,
                            getFriends = { getFriends() }) {
                            acceptFriendRequest()
                        }
                    }

                    2 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(1f)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = friendId.value,
                                    singleLine = true,
                                    modifier = Modifier.height(48.dp),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.DarkGray,
                                        textColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    shape = MaterialTheme.shapes.medium,
                                    onValueChange = { friendId.value = it })
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                IconButton(
                                    onClick = {
                                        sendFriendRequest()
                                    }, enabled = friendId.value.toIntOrNull() != null,
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Orange,
                                        disabledContentColor = Color.DarkGray
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.send_icon),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp),
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(1f)
                            ) {
                                Text(
                                    text = "Введите ID друга, чтобы добавить его!",
                                    color = Color.White
                                )
                            }

                            FriendList(
                                friendList = friendSentList,
                                friendsCount = friendsSentCount,
                                isFriendListEmpty = isFriendSentListEmpty,
                                snackBarState = snackBarState,
                                errorMessage = errorMessage,
                                clickedItem = clickedItem,
                                getFriends = { getFriends() }) {
                                //Todo отменить заявку
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FriendList(
    friendList: MutableList<FriendDTO>,
    friendsCount: State<Int>,
    isFriendListEmpty: State<Boolean>,
    snackBarState: MutableState<Boolean>,
    errorMessage: MutableState<String>,
    clickedItem: MutableState<Int>,
    getFriends: () -> Unit,
    onClick: () -> Unit
) {
    val viewModel: UpdateViewModel = viewModel()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp), color = Color.Black
    ) {
        if (isFriendListEmpty.value) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Здесь пока пусто...", color = Orange, fontSize = 20.sp)
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
                                onClick = {
                                    clickedItem.value = index
                                    onClick()
                                },
                                shape = RectangleShape,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                AsyncImage(
                                    model = if (friendList[index].image == null)
                                        "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                                    else
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(friendList[index].image)
                                            .crossfade(true)
                                            .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                        .background(color = Color.DarkGray)
                                )
                                Column(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .fillMaxWidth(1f)
                                ) {
                                    Text(
                                        text = friendList[index].nickname
                                            ?: "Альтернативное имя не указано",
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "ID: " + friendList[index].id.toString(),
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
fun FriendsPreview() {
    Friends()
}