package com.messenger.messengerapp.screen.mainSubscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.messenger.messengerapp.R
import com.messenger.messengerapp.api.impl.PostApiImpl
import com.messenger.messengerapp.api.impl.UserApiImpl
import com.messenger.messengerapp.composable.Post
import com.messenger.messengerapp.data.User
import com.messenger.messengerapp.dto.ResponsePostDTO
import com.messenger.messengerapp.dto.UserProfileDTO
import com.messenger.messengerapp.ui.theme.Orange
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Profile(id: String, onCreatePost: () -> Unit) {

    val context = LocalContext.current

    val profile = remember {
        mutableStateOf(UserProfileDTO(-1, "Не загружен", ArrayList(), null))
    }

    val posts: MutableList<ResponsePostDTO> = remember {
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
    val isPostLoading = remember {
        mutableStateOf(false)
    }

    fun getProfile() {
        val userApi = UserApiImpl()
        val response = userApi.getUser(id.toInt())

        response.enqueue(object : Callback<UserProfileDTO> {
            override fun onResponse(
                call: Call<UserProfileDTO>, response: Response<UserProfileDTO>
            ) {
                if (response.isSuccessful) {
                    profile.value = response.body()!!
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server", response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
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
        val postApi = PostApiImpl()
        val response = postApi.getPost(id.toInt(), postPage.value)

        response.enqueue(object : Callback<List<ResponsePostDTO>> {
            override fun onResponse(
                call: Call<List<ResponsePostDTO>>,
                response: Response<List<ResponsePostDTO>>
            ) {
                if (response.isSuccessful) {
                    posts.addAll(response.body()!!)
                    postPage.value++
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server", response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
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
            IconButton(
                onClick = { /*TODO*/ },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Orange)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            LazyColumn(modifier = Modifier.fillMaxSize(1f)) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        AsyncImage(
                            model = profile.value.image
                                ?: "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 16.dp)
                                .size(96.dp)
                                .clip(CircleShape)
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
                                        model = profile.value.friends[index].image
                                            ?: "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg",
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .size(64.dp)
                                            .clip(CircleShape)
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
                    Post(post = posts[index])
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
    Profile("0") {}
}