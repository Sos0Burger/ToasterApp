@file:Suppress("DEPRECATION")

package com.messenger.toaster.screen.mainSubscreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.infomessage.InfoSnackBar
import com.messenger.toaster.ui.theme.Orange
import com.messenger.toaster.viewmodel.UpdateViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Friends(navController: NavController) {
    val search = remember {
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
    val friendSearchList = remember {
        mutableStateListOf<FriendDTO>()
    }
    val isFriendPendingListEmpty = remember {
        derivedStateOf {
            friendPendingList.isEmpty()
        }
    }
    val isFriendSearchListEmpty = remember {
        derivedStateOf {
            friendSearchList.isEmpty()
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
    val friendsSearchCount = remember {
        derivedStateOf {
            friendSearchList.size
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
    val pageState = rememberPagerState() { 4 }

    val coroutineScope = rememberCoroutineScope()
    val buttonColors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
    val context = LocalContext.current

    fun getFriends() {
        val userApi = UserApiImpl()
        if (pageState.currentPage == 3 &&
            !(search.value.length >= 3 || search.value.toIntOrNull() != null)
        ) {
            friendSearchList.clear()
            return
        } else {
            val response =
                when (pageState.currentPage) {
                    2 -> userApi.getSent(User.getCredentials())
                    1 -> userApi.getPending(
                        User.getCredentials()
                    )

                    3 -> userApi.searchUsers(search.value, User.getCredentials())

                    else -> userApi.getFriends(User.getCredentials())
                }
            response.enqueue(object : Callback<List<FriendDTO>> {
                override fun onResponse(
                    call: Call<List<FriendDTO>>,
                    response: Response<List<FriendDTO>>
                ) {
                    if (response.isSuccessful) {
                        when (pageState.currentPage) {
                            2 -> {
                                friendSentList.clear()
                                friendSentList.addAll(response.body()!!)
                            }

                            1 -> {
                                friendPendingList.clear()
                                friendPendingList.addAll(response.body()!!)
                            }

                            3 -> {
                                friendSearchList.clear()
                                friendSearchList.addAll(response.body()!!)
                            }

                            else -> {
                                friendList.clear()
                                friendList.addAll(response.body()!!)
                            }
                        }

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

                override fun onFailure(call: Call<List<FriendDTO>>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    errorMessage.value = "Ошибка подключения"
                    snackBarState.value = true
                }
            })
        }
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
                OutlinedButton(
                    onClick = { coroutineScope.launch { pageState.scrollToPage(0) } },
                    colors = buttonColors,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(1.dp),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Text(
                        text = "Друзья",
                        color = if (pageState.currentPage == 0) Orange else Color.DarkGray
                    )
                }
                OutlinedButton(
                    onClick = { coroutineScope.launch { pageState.scrollToPage(1) } },
                    colors = buttonColors,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(1.dp),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Text(
                        text = "Входящие",
                        color = if (pageState.currentPage == 1) Orange else Color.DarkGray
                    )
                }
                OutlinedButton(
                    onClick = { coroutineScope.launch { pageState.scrollToPage(2) } },
                    colors = buttonColors,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(1.dp),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Text(
                        text = "Исходящие",
                        color = if (pageState.currentPage == 2) Orange else Color.DarkGray
                    )
                }
                OutlinedButton(
                    onClick = { coroutineScope.launch { pageState.scrollToPage(3) } },
                    colors = buttonColors,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(1.dp),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Text(
                        text = "Поиск",
                        color = if (pageState.currentPage == 3) Orange else Color.DarkGray,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = search.value,
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.DarkGray,
                        textColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Orange
                    ),
                    shape = MaterialTheme.shapes.medium,
                    onValueChange = {
                        search.value = it
                        getFriends()
                    },
                    placeholder = { Text(text = "Поиск", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
            HorizontalPager(
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
                            search = search,
                            navController = navController,
                            page,
                            getFriends = { getFriends() })
                    }

                    1 -> {
                        FriendList(
                            friendList = friendPendingList,
                            friendsCount = friendsPendingCount,
                            isFriendListEmpty = isFriendPendingListEmpty,
                            snackBarState = snackBarState,
                            errorMessage = errorMessage,
                            clickedItem = clickedItem,
                            search = search,
                            navController = navController,
                            page,
                            getFriends = { getFriends() })
                    }

                    2 -> {
                        FriendList(
                            friendList = friendSentList,
                            friendsCount = friendsSentCount,
                            isFriendListEmpty = isFriendSentListEmpty,
                            snackBarState = snackBarState,
                            errorMessage = errorMessage,
                            clickedItem = clickedItem,
                            search = search,
                            navController = navController,
                            page,
                            getFriends = { getFriends() })
                    }

                    3 -> {
                        FriendList(
                            friendList = friendSearchList,
                            friendsCount = friendsSearchCount,
                            isFriendListEmpty = isFriendSearchListEmpty,
                            snackBarState = snackBarState,
                            errorMessage = errorMessage,
                            clickedItem = clickedItem,
                            search = search,
                            navController = navController,
                            page,
                            getFriends = { getFriends() })
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
    search: MutableState<String>,
    navController: NavController,
    page: Int,
    getFriends: () -> Unit
) {
    val viewModel: UpdateViewModel = viewModel()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    LaunchedEffect(Unit){
        getFriends()
    }
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
                Text(
                    text = if (page != 3) "Здесь пока пусто..." else "Найдите кого-нибудь!",
                    color = Orange,
                    fontSize = 20.sp
                )
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
                        if (search.value.isEmpty() ||
                            friendList[index].nickname?.contains(search.value, true) == true ||
                            friendList[index].id == search.value.toIntOrNull()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = {
                                        clickedItem.value = index
                                        navController.navigate("profile/" + friendList[index].id)
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
                                                .data(
                                                    RetrofitClient.getInstance().baseUrl()
                                                        .toString() +
                                                            "file/" +
                                                            friendList[index].image!!.id
                                                )
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
}


@Preview(showBackground = true)
@Composable
fun FriendsPreview() {
    Friends(rememberNavController())
}