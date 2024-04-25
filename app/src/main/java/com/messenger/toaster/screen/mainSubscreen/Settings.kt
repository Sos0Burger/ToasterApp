package com.messenger.toaster.screen.mainSubscreen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.messenger.toaster.R
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.impl.FileApiImpl
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.converter.getFileName
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.dto.UserSettingsDTO
import com.messenger.toaster.requestbody.InputStreamRequestBody
import com.messenger.toaster.ui.theme.Orange
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(back:()->Unit) {
    val imageUri: MutableState<Uri> = remember {
        mutableStateOf(Uri.EMPTY)
    }
    val userSettings = remember {
        mutableStateOf(UserSettingsDTO(-1, "?", "?", null))
    }

    val nickname = remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri ?: Uri.EMPTY
    }

    fun updatePicture() {
        val fileApi = FileApiImpl()
        val cR = context.contentResolver
        val fileResponse = fileApi.upload(
            User.getCredentials(),
            MultipartBody.Part.createFormData(
                "attachment",
                getFileName(cR, imageUri.value),
                InputStreamRequestBody(
                    cR.getType(imageUri.value)!!.toMediaType(),
                    context.contentResolver,
                    imageUri.value
                )
            )
        )
        fileResponse.enqueue(object : Callback<FileDTO> {
            override fun onResponse(call: Call<FileDTO>, fileResponse: Response<FileDTO>) {
                if (fileResponse.code() == 201) {
                    val userApi = UserApiImpl()
                    val userResponse = userApi.updatePicture(
                        fileResponse.body()!!.id,
                        User.getCredentials()
                    )
                    userResponse.enqueue(object : Callback<Unit> {
                        override fun onResponse(call: Call<Unit>, userResponse: Response<Unit>) {
                            if (userResponse.code() == 200) {
                                userSettings.value.image!!.id = fileResponse.body()!!.id
                                imageUri.value = Uri.EMPTY
                            } else {
                                val jsonObj = if (userResponse.errorBody() != null) {
                                    userResponse.errorBody()!!.byteString().utf8()
                                } else {
                                    userResponse.code().toString()
                                }

                                Log.d(
                                    "server",
                                    userResponse.code().toString()
                                )
                                Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Log.d("server", t.message.toString())
                            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    val jsonObj = if (fileResponse.errorBody() != null) {
                        fileResponse.errorBody()!!.byteString().utf8()
                    } else {
                        fileResponse.code().toString()
                    }

                    Log.d(
                        "server",
                        fileResponse.code().toString()
                    )
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT)
                        .show()

                }
            }

            override fun onFailure(call: Call<FileDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun updateNickname() {
        val userApi = UserApiImpl()
        val response = userApi.updateNickname(
            nickname.value,
            User.getCredentials()
        )
        response.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 200) {
                    userSettings.value.nickName = nickname.value
                    nickname.value = ""
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
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun getSettings() {
        val userApi = UserApiImpl()
        val response = userApi.getSettings(User.getCredentials())
        response.enqueue(object : Callback<UserSettingsDTO> {
            override fun onResponse(
                call: Call<UserSettingsDTO>,
                response: Response<UserSettingsDTO>
            ) {
                if (response.code() == 200) {
                    userSettings.value = response.body()!!
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

            override fun onFailure(call: Call<UserSettingsDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }
    LaunchedEffect(Unit){
        getSettings()
    }
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Column (Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top){
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { back() },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Orange)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = "Настройки",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {
                    getSettings()
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
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                AsyncImage(
                    model =
                    if (imageUri.value == Uri.EMPTY)
                        if (userSettings.value.image == null)
                            "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                        else
                            ImageRequest.Builder(LocalContext.current)
                                .data(
                                    RetrofitClient.getInstance().baseUrl().toString() +
                                            "file/" +
                                            userSettings.value.image!!.id
                                )
                                .crossfade(true)
                                .build()
                    else
                        imageUri.value,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(256.dp)
                        .clip(CircleShape)
                        .background(color = Color.DarkGray)
                )
            }
            Text(text = "Фото профиля", fontSize = 16.sp, color = Color.White)
            Text(text = "Нажмите, чтобы изменить", fontSize = 10.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(text = "Ваш ID: " + userSettings.value.id, fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.padding(top = 16.dp))

            Text(text = "Отображаемое имя: ", color = Color.White)
            TextField(
                value = nickname.value,
                onValueChange = { nickname.value = it },
                placeholder = {
                    Text(
                        text = userSettings.value.nickName ?: "Имя не указано",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                },
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.DarkGray,
                    textColor = Color.White
                ),
                modifier = Modifier.size(196.dp, 58.dp),
            )
            Spacer(modifier = Modifier.padding(top = 32.dp))
            Button(
                onClick = {
                    if (imageUri.value != Uri.EMPTY) {
                        updatePicture()
                    }
                    if (nickname.value.length in 3..20) {
                        updateNickname()
                    } else {
                        if (nickname.value.length<3 && nickname.value.isNotEmpty()) {
                            Toast.makeText(
                                context,
                                "Имя должно быть от 3 до 20 символов",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                enabled = imageUri.value != Uri.EMPTY || (nickname.value.length in 3..19),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.DarkGray,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Black
                )
            ) {
                Text(text = "Сохранить изменения", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    Settings(){}
}

