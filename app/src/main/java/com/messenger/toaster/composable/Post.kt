package com.messenger.toaster.composable

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.messenger.toaster.R
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.impl.PostApiImpl
import com.messenger.toaster.converter.TimeConverter
import com.messenger.toaster.converter.formatNumber
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.ui.theme.Night
import com.messenger.toaster.ui.theme.Orange
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Post(
    post: ResponsePostDTO,
    isLatestComment: Boolean,
    navController: NavController,
    from: String,
    index: Int? = null,
    onPostRemove: () -> Unit,
    onCommentRemove:() -> Unit,
    smashLikePost:() -> Unit,
    smashLikeComment:()->Unit
) {
    val isDropDown = remember {
        mutableStateOf(false)
    }
    val isLatestCommentState = remember {
        mutableStateOf(isLatestComment)
    }
    var isTextExpand by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(1f)
            .background(color = Night, shape = MaterialTheme.shapes.medium)
            .clickable(enabled = isLatestComment) {
                navController.navigate(from + "/post/" + post.id.toString() + "/" + index)
            }
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                AsyncImage(
                    model = if (post.creator.image == null)
                        "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                    else
                        ImageRequest.Builder(LocalContext.current)
                            .data(
                                RetrofitClient.getInstance().baseUrl().toString() +
                                        "file/" +
                                        post.creator.image!!.id
                            )
                            .crossfade(true)
                            .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = Color.DarkGray)
                        .clickable {
                            navController.navigate("profile/" + post.creator.id)
                        }
                )
                Column {
                    Text(
                        text = post.creator.nickname
                            ?: "Альтернативное имя не указано",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "ID: " + post.creator.id.toString(),
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text(
                        text = TimeConverter.longToLocalTime(post.date),
                        fontSize = 12.sp,
                        color = Color.White
                    )

                }
            }
            if (post.creator.id == User.USER_ID) {
                Box {
                    if (isDropDown.value) {
                        DropdownMenu(
                            expanded = isDropDown.value,
                            onDismissRequest = { isDropDown.value = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "Редактировать") },
                                onClick = {
                                    isDropDown.value = false
                                    navController.navigate(
                                        from + "/postEdit/" +
                                                post.id + "/" + index
                                    )
                                })
                            DropdownMenuItem(text = { Text(text = "Удалить") }, onClick = {

                                isDropDown.value = false
                                onPostRemove()
                            })
                        }
                    }
                    IconButton(onClick = { isDropDown.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.more),
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                }
            }

        }

        Divider(
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, end = 16.dp, start = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(8.dp)
        ) {
            Text(
                text = post.text.toString(),
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable(onClick = {
                        isTextExpand = !isTextExpand
                    }),
                maxLines = when (isTextExpand) {
                    false -> 8
                    else -> Int.MAX_VALUE
                },
                overflow = TextOverflow.Ellipsis
            )
        }
        if (post.attachments.isNotEmpty()) {
            val pagerState = rememberPagerState() { post.attachments.size }
            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 1.dp),
                    state = pagerState
                ) { page ->
                    Box(Modifier.background(color = Color.Gray, shape = RoundedCornerShape(5))) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    RetrofitClient.getInstance().baseUrl().toString() +
                                            "file/" +
                                            post.attachments[page].id
                                )
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(5))
                                .height(512.dp)
                                .clickable {
                                    navController.navigate(from + "/post/" + post.id.toString() + "/" + index)
                                }
                        )
                    }
                }
                Row(
                    Modifier
                        .padding(4.dp)
                        .background(
                            Color.DarkGray,
                            shape = MaterialTheme.shapes.medium
                        ),
                ) {

                    Text(
                        text = (
                                (pagerState.currentPage + 1).toString()
                                        + " / "
                                        + post.attachments.size),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 8.dp)
        ) {
            Surface(shape = CircleShape,
                color = Color.Transparent,
                onClick = {
                    val postApi = PostApiImpl()
                    val response = postApi.smashLike(post.id, User.getCredentials())
                    response.enqueue(object : Callback<ResponsePostDTO> {
                        override fun onResponse(
                            call: Call<ResponsePostDTO>,
                            response: Response<ResponsePostDTO>
                        ) {
                            if (response.isSuccessful) {
                                smashLikePost()
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

                        override fun onFailure(call: Call<ResponsePostDTO>, t: Throwable) {
                            Log.d("server", t.message.toString())
                            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                        }
                    })
                }) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (post.isLiked) R.drawable.like else R.drawable.like_outlined
                        ),
                        contentDescription = null,
                        tint = if (post.isLiked) Orange else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))

                    Text(text = formatNumber(post.likes), color = Color.White, maxLines = 1)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (isLatestComment) {
                Surface(shape = CircleShape,
                    color = Color.Transparent,
                    onClick = {
                        navController.navigate(from + "/post/" + post.id.toString() + "/" + index)
                    }) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.comment),
                            contentDescription = null,
                            tint = Orange
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = formatNumber(post.comments), color = Color.White)
                    }
                }
            }

        }
        if (isLatestCommentState.value) {
            Divider(
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, end = 16.dp, start = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (post.popularComment != null) {
                Comment(comment = remember {
                    mutableStateOf(post.popularComment!!)
                }, navController = navController,
                    removeComment = {onCommentRemove()}, smashLikeComment = {smashLikeComment()})
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PostPreview() {
    Post(
        post =
                ResponsePostDTO(
                    1,
                    "Как\nсосать\nогромные\nпенисы\nкаждый\nбожий\nдень\nкрупные\nяйца\nтоже\nобожаю\nглотать\n",
                    FriendDTO(1, "SosoBurger", null),
                    1687860434639,
                    ArrayList(),
                    1313123123,
                    true,
                    15,
                    ResponseCommentDTO(
                        1,
                        "Письки",
                        FriendDTO(1, "SosoBurger", null),
                        1687860434639,
                        1,
                        14,
                        false
                    )
                )
        ,
        true,
        rememberNavController(),
        "",
        onPostRemove = {}, smashLikePost = {}, onCommentRemove = {}, smashLikeComment = {})
}