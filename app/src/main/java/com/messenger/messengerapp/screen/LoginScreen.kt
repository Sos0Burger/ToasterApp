package com.messenger.messengerapp.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.messenger.messengerapp.api.impl.UserApiImpl
import com.messenger.messengerapp.data.User
import com.messenger.messengerapp.exception.ExceptionMessage
import com.messenger.messengerapp.infoMessage.InfoSnackBar
import com.messenger.messengerapp.ui.theme.Orange
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun LoginScreen(isReg: Boolean, onNavigateToMainScreen: () -> Unit) {
    val inputEnabled = remember {
        mutableStateOf(!isReg)
    }
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val errorMessage = remember {
        mutableStateOf("")
    }
    val snackBarState = remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon()

            Spacer(modifier = Modifier.padding(top = 16.dp))

            EmailInput(email = email, inputEnabled) {}

            PasswordInput(password = password, inputEnabled = inputEnabled) {}

            Spacer(modifier = Modifier.padding(top = 16.dp))

            LoginScreenButton(
                inputEnabled = inputEnabled,
                errorMessage,
                snackBarState,
                onNavigateToMainScreen
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
    }
    if (isReg) {
        val userApi = UserApiImpl()
        val response = userApi.auth(User.EMAIL.toString(), User.HASH.toString())
        response.enqueue(object : Callback<ExceptionMessage> {
            override fun onResponse(call: Call<ExceptionMessage>, response: Response<ExceptionMessage>) {
                if (response.code()==200) {
                    User.USER_ID = response.body()!!.getMessage().toInt()
                    onNavigateToMainScreen()
                }
                else{
                    Log.d("server", response.code().toString() +" "+response.body())
                    if(response.code()==404){
                        errorMessage.value = "Аккаунт не найден"
                    }
                    snackBarState.value = true
                    inputEnabled.value = true
                }
            }

            override fun onFailure(call: Call<ExceptionMessage>, t: Throwable) {
                Log.d("server", t.message.toString())
                errorMessage.value = "Ошибка сервера"
                snackBarState.value = true
                inputEnabled.value = true
            }

        })
    }
}

@Composable
fun LoginScreenButton(
    inputEnabled: MutableState<Boolean>,
    errorMessage: MutableState<String>,
    snackBarState: MutableState<Boolean>,
    onNavigateToMainScreen: () -> Unit
) {
    TextButton(
        onClick = {
            inputEnabled.value = false
            val userApi = UserApiImpl()
            val response = userApi.auth(User.EMAIL.toString(), User.HASH.toString())
            response.enqueue(object : Callback<ExceptionMessage> {
                override fun onResponse(call: Call<ExceptionMessage>, response: Response<ExceptionMessage>) {
                    if (response.code()==200) {
                        User.USER_ID = response.body()!!.getMessage().toInt()
                        onNavigateToMainScreen()
                    }
                    else{
                        Log.d("server", response.code().toString() +" "+response.body())
                        if(response.code()==404){
                            errorMessage.value = "Аккаунт не найден"
                        }
                        snackBarState.value = true
                        inputEnabled.value = true
                    }
                }

                override fun onFailure(call: Call<ExceptionMessage>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    errorMessage.value = "Ошибка сервера"
                    snackBarState.value = true
                    inputEnabled.value = true
                }

            })
        },
        contentPadding = PaddingValues(start = 64.dp, end = 64.dp),
        enabled = inputEnabled.value,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.DarkGray,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.DarkGray
        )
    ) {
        if (inputEnabled.value) {
            Text(text = "Войти")
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp, 24.dp), color = Orange)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        LoginScreen(false) {}
    }
}