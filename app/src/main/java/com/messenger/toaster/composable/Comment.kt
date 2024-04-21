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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.messenger.toaster.dto.ResponseCommentDTO
import com.messenger.toaster.ui.theme.Orange
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Comment(comment: MutableState<ResponseCommentDTO>, navController:NavController) {
    val context = LocalContext.current
    var isTextExpand by remember {
        mutableStateOf(false)
    }
    Column(
        Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 8.dp)
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
                Text(text = comment.value.creator.nickname.toString(), color = Color.White)
                Text(
                    text = comment.value.text,
                    color = Color.White,
                    modifier = Modifier
                        .padding(end = 16.dp)
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
                            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
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
    )
}