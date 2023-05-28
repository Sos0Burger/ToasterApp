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
import androidx.compose.runtime.LaunchedEffect
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
import com.messenger.messengerapp.hasher.Hasher
import com.messenger.messengerapp.infomessage.InfoSnackBar
import com.messenger.messengerapp.ui.theme.Orange
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun LoginScreen(
    isReg: Boolean, onNavigateToMainScreen: () -> Unit, onNavigateToRegistration: () -> Unit
) {
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
    val loginEnabled = remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.Black
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon()

            Spacer(modifier = Modifier.padding(top = 16.dp))

            EmailInput(email = email, inputEnabled) {
                if (email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+")) && password.value.matches(
                        Regex("[A-z0-9]{8,32}")
                    )
                ) {
                    loginEnabled.value = true
                }
            }

            PasswordInput(password = password, inputEnabled = inputEnabled) {
                if (email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+")) && password.value.matches(
                        Regex("[A-z0-9]{8,32}")
                    )
                ) {
                    loginEnabled.value = true
                }
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            LoginScreenButton(
                inputEnabled = inputEnabled,
                errorMessage = errorMessage,
                snackBarState = snackBarState,
                loginEnabled = loginEnabled,
                email = email,
                password = password,
                onNavigateToMainScreen = onNavigateToMainScreen
            )

        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Еще не зарегистрированы?", color = Color.White)
                Spacer(modifier = Modifier.padding(top = 4.dp))
                BackToRegistration(inputEnabled = inputEnabled) {
                    onNavigateToRegistration()
                }
            }
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


    LaunchedEffect(Unit) {
        delay(500L)
        if (isReg) {
            val userApi = UserApiImpl()
            val response = userApi.auth(User.EMAIL.toString(), User.HASH.toString())
            response.enqueue(object : Callback<Any> {
                override fun onResponse(
                    call: Call<Any>, response: Response<Any>
                ) {
                    if (response.code() == 200) {
                        User.USER_ID = (response.body() as? Double?)?.toInt()
                        onNavigateToMainScreen()
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        Log.d(
                            "server",
                            response.code().toString() + " " + jsonObj.getString("message")
                        )
                        errorMessage.value = jsonObj.getString("message")
                        snackBarState.value = true
                        inputEnabled.value = true
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    errorMessage.value = "Ошибка подключения"
                    snackBarState.value = true
                    inputEnabled.value = true
                }

            })
        }
    }
}

@Composable
fun LoginScreenButton(
    inputEnabled: MutableState<Boolean>,
    errorMessage: MutableState<String>,
    snackBarState: MutableState<Boolean>,
    loginEnabled: MutableState<Boolean>,
    password: MutableState<String>,
    email: MutableState<String>,
    onNavigateToMainScreen: () -> Unit
) {
    TextButton(
        onClick = {
            inputEnabled.value = false
            val userApi = UserApiImpl()
            val response = userApi.auth(
                email.value, Hasher.hash(password.value)
            )
            response.enqueue(object : Callback<Any> {
                override fun onResponse(
                    call: Call<Any>, response: Response<Any>
                ) {
                    if (response.code() == 200) {
                        User.USER_ID = (response.body() as? Double?)?.toInt()
                        User.EMAIL = email.value
                        User.HASH = Hasher.hash(password.value)

                        with(User.sharedPrefs.edit()) {
                            putString("email", User.EMAIL)
                            putString("hash", User.HASH)
                            apply()
                        }
                        onNavigateToMainScreen()
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        Log.d(
                            "server",
                            response.code().toString() + " " + jsonObj.getString("message")
                        )
                        errorMessage.value = jsonObj.getString("message")
                        snackBarState.value = true
                        inputEnabled.value = true
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    errorMessage.value = "Ошибка подключения"
                    snackBarState.value = true
                    inputEnabled.value = true
                }

            })
        },
        contentPadding = PaddingValues(start = 64.dp, end = 64.dp),
        enabled = inputEnabled.value && loginEnabled.value,
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
@Composable
fun BackToRegistration(inputEnabled: MutableState<Boolean>, onNavigateToRegistration: () -> Unit) {
    TextButton(
        onClick = { onNavigateToRegistration() },
        contentPadding = PaddingValues(start = 64.dp, end = 64.dp),
        enabled = inputEnabled.value,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.DarkGray,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.DarkGray
        )
    ) {
        Text(text = "Зарегистрироваться")
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.Black
    ) {
        LoginScreen(false, {}, {})
    }
}

