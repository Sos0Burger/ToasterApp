package com.messenger.toaster.screen.mainSubscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.messenger.toaster.R
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.composable.Post
import com.messenger.toaster.composable.ProfileFriendButtons
import com.messenger.toaster.data.FriendStatus
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.dto.UserProfileDTO
import com.messenger.toaster.ui.theme.Orange
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Profile(id: String, navController: NavController, onCreatePost: () -> Unit) {

    val context = LocalContext.current

    val profile = remember {
        mutableStateOf(UserProfileDTO(-1, "Не загружен", ArrayList(), null, FriendStatus.SELF))
    }

    val posts: MutableList<MutableState<ResponsePostDTO>> = remember {
        mutableStateListOf()
    }

    val postCount = remember {
        derivedStateOf { posts.size }
    }

    val postPage = remember {
        mutableStateOf(0)
    }

    val postScrollState = rememberLazyListState()
    val endReached by remember {
        derivedStateOf {
            !postScrollState.canScrollForward && (posts.size >= postCount.value * 15) && (posts.size != 0 || postPage.value == 0)
        }
    }
    val isPostLoading = rememberSaveable {
        mutableStateOf(false)
    }
    val search = remember {
        mutableStateOf("")
    }

    fun getProfile() {
        val userApi = UserApiImpl()
        val response = userApi.getUserProfile(id.toInt(), User.getCredentials())
        response.enqueue(object : Callback<UserProfileDTO> {
            override fun onResponse(
                call: Call<UserProfileDTO>, response: Response<UserProfileDTO>
            ) {
                if (response.isSuccessful) {
                    profile.value = response.body()!!
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
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun getPosts() {
        isPostLoading.value = true
        val userApi = UserApiImpl()
        val response =
            userApi.getUserPosts(id.toInt(), search.value, postPage.value, User.getCredentials())
        response.enqueue(object : Callback<List<ResponsePostDTO>> {
            override fun onResponse(
                call: Call<List<ResponsePostDTO>>,
                response: Response<List<ResponsePostDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()!!.forEach { posts.add(mutableStateOf(it)) }
                    postPage.value++
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
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT)
                        .show()
                }
                isPostLoading.value = false
            }

            override fun onFailure(call: Call<List<ResponsePostDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                isPostLoading.value = false
            }

        })
    }

    if (profile.value.id == -1) {
        getProfile()
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(1f)
                .padding(horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Orange)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = "Профиль",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {
                    getProfile()
                    getPosts()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.update),
                        contentDescription = null,
                        tint = Orange
                    )
                }
            }
            Divider(
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, end = 16.dp, start = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxSize(1f)) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        AsyncImage(
                            model = if (profile.value.image == null)
                                "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                            else
                                ImageRequest.Builder(LocalContext.current)
                                    .data(
                                        RetrofitClient.getInstance().baseUrl().toString() +
                                                "file/" +
                                                profile.value.image!!.id
                                    )
                                    .crossfade(true)
                                    .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 16.dp)
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(color = Color.DarkGray)
                        )
                        Column {
                            Text(
                                text = profile.value.nickname ?: "Альтернативное имя не указано",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                            Text(
                                text = "ID: " + profile.value.id.toString(),
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }
                item {
                    Column {

                        Text(
                            text = "Друзья(" + profile.value.friends.size + ")",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        LazyRow(contentPadding = PaddingValues(8.dp)) {
                            items(count = profile.value.friends.size) { index ->
                                Column() {
                                    AsyncImage(
                                        model = if (profile.value.friends[index].image == null)
                                            "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                                        else
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(
                                                    RetrofitClient.getInstance().baseUrl()
                                                        .toString() +
                                                            "file/" +
                                                            profile.value.friends[index].image!!.id
                                                )
                                                .crossfade(true)
                                                .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(color = Color.DarkGray)
                                    )
                                    Column {
                                        Text(
                                            text = profile.value.friends[index].nickname
                                                ?: "Альтернативное имя не указано",
                                            fontSize = 20.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "ID: " + profile.value.friends[index].id.toString(),
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    ProfileFriendButtons(
                        state = profile
                    ){
                        navController.navigate("chatMessages/$id")
                    }
                }
                if (id.toInt() == User.USER_ID) {
                    item {
                        Button(
                            onClick = { onCreatePost() },
                            modifier = Modifier.fillMaxWidth(1f),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Создать новую запись", fontSize = 14.sp)
                        }
                    }
                }

                items(count = postCount.value) { index ->
                    Post(post = posts[index], true, navController = navController)
                }

                item {
                    if (isPostLoading.value)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(1f)
                        ) {
                            CircularProgressIndicator(
                                color = Orange,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                }
            }
        }
    }
    if (endReached && !isPostLoading.value && profile.value.id != -1) {
        getPosts()
    }

}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    Profile("0", rememberNavController()) {}
}