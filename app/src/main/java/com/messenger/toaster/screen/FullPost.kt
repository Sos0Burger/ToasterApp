package com.messenger.toaster.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.messenger.toaster.R
import com.messenger.toaster.api.impl.PostApiImpl
import com.messenger.toaster.composable.Comment
import com.messenger.toaster.composable.Post
import com.messenger.toaster.data.SortingEnum
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.RequestCommentDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.ui.theme.Orange
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPost(id: String, navController: NavController) {
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    val comment = remember {
        mutableStateOf("")
    }
    val isPostLoading = remember {
        mutableStateOf(true)
    }
    val isCommentsLoading = remember {
        mutableStateOf(true)
    }
    val isDropDown = remember {
        mutableStateOf(false)
    }
    val dropDownItems = remember {
        mutableStateListOf("Сначала популярные", "Сначала старые", "Сначала новые")
    }
    val currentItem = remember {
        mutableStateOf(0)
    }
    val scrollState = rememberScrollState()
    var post = remember {
        mutableStateOf(
            ResponsePostDTO(
                1,
                null,
                FriendDTO(-1, null, null),
                19923293,
                emptyList(),
                0,
                false,
                0,
                ResponseCommentDTO(-1, "", FriendDTO(-1, null, null), 1909949324, 1, 0, false)
            )
        )
    }
    var comments = remember {
        mutableStateListOf<MutableState<ResponseCommentDTO>>()
    }

    fun getPost() {
        isPostLoading.value = false
        val postApi = PostApiImpl()
        val response = postApi.getPost(id.toInt(), User.getCredentials())
        response.enqueue(object : Callback<ResponsePostDTO> {
            override fun onResponse(
                call: Call<ResponsePostDTO>,
                response: Response<ResponsePostDTO>
            ) {
                if (response.isSuccessful) {
                    post.value = response.body()!!
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server", response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                }
                isPostLoading.value = false
            }

            override fun onFailure(call: Call<ResponsePostDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                isPostLoading.value = false
            }
        })
    }

    fun getComments() {
        isCommentsLoading.value = false
        val postApi = PostApiImpl()
        val response = postApi.getPostComments(
            id.toInt(),
            SortingEnum.values()[currentItem.value],
            User.getCredentials()
        )
        response.enqueue(object : Callback<List<ResponseCommentDTO>> {
            override fun onResponse(
                call: Call<List<ResponseCommentDTO>>,
                response: Response<List<ResponseCommentDTO>>
            ) {
                if (response.isSuccessful) {
                    comments.clear()
                    response.body()!!.forEach { comments.add(mutableStateOf(it)) }
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server", response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                }
                isCommentsLoading.value = false
            }

            override fun onFailure(call: Call<List<ResponseCommentDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                isCommentsLoading.value = false
            }
        })
    }
    if (isPostLoading.value) {
        getPost()
        isPostLoading.value = false
    }
    if (isCommentsLoading.value) {
        getComments()
        isCommentsLoading.value = false
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = null,
                        tint = Orange
                    )
                }
                Text(
                    text = "Пост",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { getPost() }) {
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(state = lazyListState, modifier = Modifier.weight(1f)) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Post(post = post, isLatestComment = false, navController)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = comments.size.toString() + " Комментариев",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Box {
                                TextButton(
                                    onClick = { isDropDown.value = true },
                                    modifier = Modifier.height(24.dp),
                                    contentPadding = PaddingValues(1.dp)
                                ) {
                                    Text(
                                        text = dropDownItems[currentItem.value],
                                        color = Orange,
                                        fontSize = 16.sp
                                    )
                                }
                                DropdownMenu(
                                    expanded = isDropDown.value,
                                    onDismissRequest = { isDropDown.value = false },
                                    offset = DpOffset(0.dp, (-64).dp),
                                    modifier = Modifier.background(color = Color.DarkGray)
                                ) {
                                    for (i in dropDownItems.indices)
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = dropDownItems[i],
                                                    color = if (currentItem.value == i) Orange else Color.White
                                                )
                                            },
                                            onClick = {
                                                currentItem.value = i
                                                isDropDown.value = false
                                                isCommentsLoading.value = true
                                            })

                                }
                            }

                        }
                    }
                    items(count = comments.size) { index ->
                        Comment(comment = comments[index], navController){
                            comments.remove(comments[index])
                        }
                    }

                }
                TextField(
                    value = comment.value,
                    onValueChange = { comment.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .heightIn(min = 48.dp, max = 96.dp),
                    placeholder = { Text(text = "Комментарий", color = Color.White) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val postApi = PostApiImpl()
                                val response = postApi.createComment(
                                    id.toInt(),
                                    RequestCommentDTO(comment.value, Date().time),
                                    User.getCredentials()
                                )
                                response.enqueue(object : Callback<ResponseCommentDTO> {
                                    override fun onResponse(
                                        call: Call<ResponseCommentDTO>,
                                        response: Response<ResponseCommentDTO>
                                    ) {
                                        if (response.isSuccessful) {
                                            comments.add(0, mutableStateOf(response.body()!!))
                                            comment.value = ""
                                        } else {
                                            val jsonObj = JSONObject(
                                                response.errorBody()!!.charStream().readText()
                                            )
                                            Log.d(
                                                "server", response.code().toString()
                                            )
                                            Toast.makeText(
                                                context,
                                                jsonObj.getString("message"),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ResponseCommentDTO>,
                                        t: Throwable
                                    ) {
                                        Log.d("server", t.message.toString())
                                        Toast.makeText(
                                            context,
                                            "Ошибка подключения",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            },
                            enabled = comment.value.isNotEmpty()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.send_icon),
                                contentDescription = null,
                                tint = if (comment.value.isNotEmpty()) Orange else Color.Gray
                            )
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        textColor = Color.White,
                        cursorColor = Orange
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }


    }
}

@Preview(showBackground = true, backgroundColor = 1250067)
@Composable
fun FullPostPreview() {
    FullPost(id = "1", navController = rememberNavController())
}