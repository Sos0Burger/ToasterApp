package com.messenger.messengerapp.screen.mainSubscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.messenger.messengerapp.api.impl.UserApiImpl
import com.messenger.messengerapp.dto.UserSettingsDTO
import com.messenger.messengerapp.screen.sendFiles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings() {
    val imageUri: MutableState<Uri> = remember {
        mutableStateOf(Uri.EMPTY)
    }
    val userSettings = remember {
        mutableStateOf<UserSettingsDTO>(UserSettingsDTO(-1, "?", "?", null))
    }

    val uploadedImageIds = remember {
        mutableStateListOf<Int>()
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

    fun updatePicture(){
        val userApi = UserApiImpl()
    }


    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
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
                        userSettings.value.image
                            ?: "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                    else
                        imageUri.value,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(256.dp)
                        .clip(CircleShape)
                )
            }
            Text(text = "Фото профиля", fontSize = 16.sp, color = Color.White)
            Text(text = "Нажимте чтобы изменить", fontSize = 10.sp, color = Color.LightGray)
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
                          //TODO
                },
                enabled = imageUri.value != Uri.EMPTY || nickname.value.length >= 3,
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
    Settings()
}

