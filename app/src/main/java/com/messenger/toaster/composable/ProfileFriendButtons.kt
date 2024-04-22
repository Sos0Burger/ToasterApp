package com.messenger.toaster.composable

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.FriendStatus
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.UserProfileDTO
import com.messenger.toaster.ui.theme.Orange
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileFriendButtons(
    state: MutableState<UserProfileDTO>,
    profile: Int
) {
    val context = LocalContext.current
    Row(Modifier.fillMaxWidth()) {
        when (state.value.status) {
            FriendStatus.NOTHING -> {
                Button(
                    onClick = {
                        val userApi = UserApiImpl()
                        val response = userApi.sendFriendRequest(profile, User.getCredentials())
                        response.enqueue(object : Callback<FriendDTO> {
                            override fun onResponse(
                                call: Call<FriendDTO>,
                                response: Response<FriendDTO>
                            ) {
                                if (response.isSuccessful) {
                                    state.value.status = FriendStatus.SENT
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

                            override fun onFailure(call: Call<FriendDTO>, t: Throwable) {
                                Log.d("server", t.message.toString())
                                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Добавить в друзья", color = Orange)
                }
            }

            FriendStatus.FRIEND -> {
                Button(
                    onClick = {
                        val userApi = UserApiImpl()
                        val response = userApi.deleteFriend(profile, User.getCredentials())
                        response.enqueue(object : Callback<Unit> {
                            override fun onResponse(
                                call: Call<Unit>,
                                response: Response<Unit>
                            ) {
                                if (response.isSuccessful) {
                                    state.value.status = FriendStatus.NOTHING
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Удалить из друзей", color = Orange, textAlign = TextAlign.Center)
                }
            }

            FriendStatus.PENDING -> {
                Button(
                    onClick = {
                        val userApi = UserApiImpl()
                        val response = userApi.acceptFriendRequest(profile, User.getCredentials())
                        response.enqueue(object : Callback<FriendDTO> {
                            override fun onResponse(
                                call: Call<FriendDTO>,
                                response: Response<FriendDTO>
                            ) {
                                if (response.isSuccessful) {
                                    state.value.status = FriendStatus.NOTHING
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

                            override fun onFailure(call: Call<FriendDTO>, t: Throwable) {
                                Log.d("server", t.message.toString())
                                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Принять заявку в друзья",
                        color = Orange,
                        textAlign = TextAlign.Center
                    )
                }
            }

            FriendStatus.SENT -> {
                Button(
                    onClick = {
                        val userApi = UserApiImpl()
                        val response = userApi.deleteFriendRequest(profile, User.getCredentials())
                        response.enqueue(object : Callback<Unit> {
                            override fun onResponse(
                                call: Call<Unit>,
                                response: Response<Unit>
                            ) {
                                if (response.isSuccessful) {
                                    state.value.status = FriendStatus.NOTHING
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Отменить заявку в друзья",
                        color = Orange,
                        textAlign = TextAlign.Center
                    )
                }
            }

            FriendStatus.SELF -> {

            }
        }
        if (state.value.status != FriendStatus.SELF) {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Сообщения", color = Orange, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 1250067)
@Composable
fun ProfileButtonPreview() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        for (i in FriendStatus.values()) {
            ProfileFriendButtons(state = remember {
                mutableStateOf(
                    UserProfileDTO(
                        1,
                        "Masunya",
                        ArrayList(),
                        null,
                        FriendStatus.SELF
                    )
                )
            }, 1)
        }
    }


}