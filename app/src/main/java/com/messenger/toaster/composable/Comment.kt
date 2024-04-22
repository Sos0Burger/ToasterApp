package com.messenger.toaster.composable

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
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
import com.messenger.toaster.api.impl.CommentApiImpl
import com.messenger.toaster.converter.TimeConverter
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.RequestCommentDTO
import com.messenger.toaster.dto.ResponseCommentDTO
import com.messenger.toaster.ui.theme.Night
import com.messenger.toaster.ui.theme.Orange
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comment(
    comment: MutableState<ResponseCommentDTO>,
    navController: NavController,
    removeComment: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    val editingComment = remember {
        mutableStateOf(comment.value.text)
    }
    val openEdit = remember { mutableStateOf(false) }
    val openDelete = remember { mutableStateOf(false) }
    var isTextExpand by remember {
        mutableStateOf(false)
    }
    val isDropDown = remember {
        mutableStateOf(false)
    }
    if (openEdit.value) {
        AlertDialog(
            onDismissRequest = { openEdit.value = false },
            confirmButton = {
                Button(
                    onClick = {
                        val commentApi = CommentApiImpl()
                        val response = commentApi.editComment(
                            comment.value.id,
                            User.getCredentials(),
                            RequestCommentDTO(editingComment.value, Date().time)
                        )
                        response.enqueue(object : Callback<ResponseCommentDTO> {
                            override fun onResponse(
                                call: Call<ResponseCommentDTO>,
                                response: Response<ResponseCommentDTO>
                            ) {
                                if (response.isSuccessful) {
                                    comment.value = response.body()!!
                                    openEdit.value = false
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

                            override fun onFailure(call: Call<ResponseCommentDTO>, t: Throwable) {
                                Log.d("server", t.message.toString())
                                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        contentColor = Orange,
                        disabledContentColor = Color.Gray
                    ),
                    enabled = editingComment.value.isNotEmpty()
                ) {
                    Text(text = "Подтвердить")
                }
            },
            dismissButton = {
                Button(
                    onClick = { openEdit.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text(text = "Отменить")
                }
            },
            title = { Text(text = "Редактировать комментарий", color = Color.White) },
            text = {
                TextField(
                    value = editingComment.value,
                    onValueChange = { editingComment.value = it },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        textColor = Color.White,
                        cursorColor = Orange
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .heightIn(min = 48.dp, max = 96.dp)
                )
            },
            containerColor = Night
        )
    }
    if (openDelete.value) {
        AlertDialog(
            onDismissRequest = { openDelete.value = false },
            confirmButton = {
                Button(
                    onClick = {
                        val commentApi = CommentApiImpl()
                        val response = commentApi.deleteComment(
                            comment.value.id,
                            User.getCredentials()
                        )
                        response.enqueue(object : Callback<Unit> {
                            override fun onResponse(
                                call: Call<Unit>,
                                response: Response<Unit>
                            ) {
                                if (response.isSuccessful) {
                                    removeComment()
                                    openDelete.value = false
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

                            override fun onFailure(call: Call<Unit>, t: Throwable) {
                                Log.d("server", t.message.toString())
                                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        contentColor = Orange,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text(text = "Да")
                }
            },
            dismissButton = {
                Button(
                    onClick = { openDelete.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text(text = "Нет")
                }
            },
            title = { Text(text = "Вы точно хотите удалить комментарий?", color = Color.White) },
            containerColor = Night
        )
    }
    Column(
        Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 8.dp)
            .clickable(onClick = {
                isDropDown.value = !isDropDown.value
            })
    ) {
        Row(
            Modifier
                .fillMaxWidth(1f)
        ) {
            AsyncImage(
                model = if (comment.value.creator.image == null)
                    "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                else
                    ImageRequest.Builder(LocalContext.current)
                        .data(
                            RetrofitClient.getInstance().baseUrl().toString() +
                                    "file/" +
                                    comment.value.creator.image!!.id
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
                        navController.navigate("profile/" + comment.value.creator.id)
                    }
            )

            Column {
                DropdownMenu(
                    expanded = isDropDown.value,
                    onDismissRequest = { isDropDown.value = false },
                    modifier = Modifier.background(color = Color.DarkGray)
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            text = "Скопировать текст",
                            color = Color.White
                        )
                    }, onClick = {
                        clipboardManager.setText(AnnotatedString(comment.value.text))
                        isDropDown.value = false
                    })
                    if (comment.value.creator.id == User.USER_ID) {
                        DropdownMenuItem(
                            text = { Text("Редактировать", color = Color.White) },
                            onClick = {
                                openEdit.value = true
                                isDropDown.value = false
                            })
                        DropdownMenuItem(text = { Text("Удалить", color = Color.White) },
                            onClick = {
                                openDelete.value = true
                                isDropDown.value = false
                            })
                    }
                }
                Text(text = comment.value.creator.nickname.toString(), color = Color.White)
                Text(
                    text = comment.value.text,
                    color = Color.White,
                    modifier = if (!isTextExpand) {
                        Modifier
                            .padding(end = 16.dp)
                            .clickable(onClick = {
                                isTextExpand = !isTextExpand
                            })
                    } else {
                        Modifier
                            .padding(end = 16.dp)
                    },
                    maxLines = when (isTextExpand) {
                        false -> 8
                        else -> Int.MAX_VALUE
                    },
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            Text(
                text = TimeConverter.longToLocalTime(comment.value.date),
                fontSize = 10.sp,
                color = Color.White
            )
            IconButton(
                onClick = {
                    val commentApi = CommentApiImpl()
                    val response = commentApi.smashLike(comment.value.id, User.getCredentials())
                    response.enqueue(object : Callback<ResponseCommentDTO> {
                        override fun onResponse(
                            call: Call<ResponseCommentDTO>,
                            response: Response<ResponseCommentDTO>
                        ) {
                            if (response.isSuccessful) {
                                comment.value = response.body()!!

                            } else {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
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

                        override fun onFailure(call: Call<ResponseCommentDTO>, t: Throwable) {
                            Log.d("server", t.message.toString())
                            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }, modifier = Modifier
                    .height(18.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceAround) {
                    Icon(
                        painter = painterResource(
                            id = if (comment.value.isLiked) R.drawable.like else R.drawable.like_outlined
                        ),
                        contentDescription = null,
                        tint = if (comment.value.isLiked) Orange else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = comment.value.likes.toString(),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 1250067)
@Composable
fun CommentPreview() {
    Comment(
        comment = remember {
            mutableStateOf(
                ResponseCommentDTO(
                    1,
                    "sdfshhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhfffffffffffffffffffffffffffffffffffffffffff" +
                            "fffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                            "fffffffffsfsfgsgdhhdhd",
                    FriendDTO(1, "SosoBurger", null),
                    1687860434639,
                    1,
                    14,
                    false
                )
            )
        },
        rememberNavController()
    ) {

    }
}