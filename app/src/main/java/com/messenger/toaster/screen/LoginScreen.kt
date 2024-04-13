package com.messenger.toaster.screen

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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.infomessage.InfoSnackBar
import com.messenger.toaster.ui.theme.Orange
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
    loginEnabled.value = email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+")) && password.value.matches(
        Regex("[A-z0-9]{8,32}")
    )
    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.Black
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon()

            Spacer(modifier = Modifier.padding(top = 16.dp))

            EmailInput(email = email, inputEnabled)

            PasswordInput(password = password, inputEnabled = inputEnabled)

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
            val response = userApi.auth(User.getCredentials())
            response.enqueue(object : Callback<Int> {
                override fun onResponse(
                    call: Call<Int>, response: Response<Int>
                ) {
                    if (response.code() == 200) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        "Firebase",
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }
                                val token = task.result
                                val tokenResponse =
                                    userApi.updateToken(token, User.getCredentials())
                                tokenResponse.enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        call: Call<Unit>,
                                        tokenResponse: Response<Unit>
                                    ) {
                                        if (tokenResponse.code() == 200) {
                                            Log.d("server", "Токен обновлен")
                                        } else {
                                            Log.d(
                                                "server",
                                                tokenResponse.code().toString()
                                            )
                                        }
                                    }

                                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                                        Log.d("server", t.message.toString())
                                    }
                                })
                                Log.d("firebase", "sendRegistrationTokenToServer($token)")
                                Log.d("firebase", token)
                            })
                        User.USER_ID = response.body()
                        onNavigateToMainScreen()
                    } else {
                        if(response.code() == 401){
                            errorMessage.value = "Ошибка авторизации"
                            snackBarState.value = true
                            inputEnabled.value = true
                        }
                        else {
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
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
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
                User.getCredentials(email.value, password.value)
            )
            response.enqueue(object : Callback<Int> {
                override fun onResponse(
                    call: Call<Int>, response: Response<Int>
                ) {
                    if (response.code() == 200) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        "Firebase",
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }
                                val token = task.result
                                val tokenResponse =
                                    userApi.updateToken(token.toString(), User.getCredentials())
                                tokenResponse.enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        call: Call<Unit>,
                                        tokenResponse: Response<Unit>
                                    ) {
                                        if (tokenResponse.code() == 200) {
                                            Log.d("server", "Токен обновлен")
                                        } else {
                                            Log.d(
                                                "server",
                                                tokenResponse.code().toString()
                                            )
                                        }
                                    }

                                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                                        Log.d("server", t.message.toString())
                                    }
                                })

                                Log.d("firebase", token)
                            })
                        User.USER_ID = (response.body() as? Double?)?.toInt()
                        User.EMAIL = email.value
                        User.PASSWORD = password.value

                        with(User.sharedPrefs.edit()) {
                            putString("email", User.EMAIL)
                            putString("password", User.PASSWORD)
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

                override fun onFailure(call: Call<Int>, t: Throwable) {
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

