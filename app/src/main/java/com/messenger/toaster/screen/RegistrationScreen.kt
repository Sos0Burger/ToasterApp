package com.messenger.toaster.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.messenger.toaster.R
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.UserDTO
import com.messenger.toaster.dto.UserProfileDTO
import com.messenger.toaster.infomessage.InfoSnackBar
import com.messenger.toaster.ui.theme.Orange
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun RegistrationScreen(onNavigateToLogin: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordRepeat = remember { mutableStateOf("") }
    val token = remember {
        mutableStateOf("")
    }
    val enabled = remember {
        mutableStateOf(
            false
        )
    }
    val inputEnabled = remember {
        mutableStateOf(true)
    }
    val snackBarState = remember { mutableStateOf(false) }
    val errorMessage = remember {
        mutableStateOf("")
    }
    val timer = remember {
        mutableStateOf(0F)
    }
    enabled.value = email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))
            && password.value.matches(Regex("[A-z0-9]{8,32}"))
            && password.value == passwordRepeat.value
            && token.value.isNotEmpty()
    LaunchedEffect(timer.value) {
        while (timer.value > 0F) {
            timer.value -= 0.01F
            delay(100)
        }
        timer.value = 0F
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 128.dp)
        ) {
            AppIcon()

            Spacer(modifier = Modifier.padding(top = 16.dp))

            EmailInput(email = email, inputEnabled = inputEnabled, timer = timer, snackBarState, errorMessage)

            EmailCodeInput(token = token)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordInput(password = password, inputEnabled)

            PasswordRepeatInput(
                password = password,
                passwordRepeat = passwordRepeat,
                inputEnabled
            )


            Spacer(modifier = Modifier.padding(top = 16.dp))

            RegistrationButton(
                enabled = enabled,
                inputEnabled = inputEnabled,
                email = email,
                password = password,
                snackBarState = snackBarState,
                errorMessage = errorMessage,
                token = token
            ) { onNavigateToLogin() }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {


                LoginSuggestionText()

                Spacer(modifier = Modifier.padding(top = 4.dp))

                LoginButton(inputEnabled) { onNavigateToLogin() }

            }

        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            InfoSnackBar(text = errorMessage, snackBarState = snackBarState)
        }
    }
}

@Composable
fun AppIcon() {
    Image(
        painter = painterResource(id = R.drawable.icon),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp, 150.dp)
            .padding(top = 4.dp, bottom = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInput(
    email: MutableState<String>,
    inputEnabled: MutableState<Boolean>,
    timer: MutableState<Float>? = null,
    snackBarState: MutableState<Boolean>? = null,
    errorMessage: MutableState<String>? = null
) {
    val isEnabled = remember {
        mutableStateOf(true)
    }
    TextField(
        value = email.value,
        singleLine = true,
        enabled = inputEnabled.value,
        shape = MaterialTheme.shapes.medium,
        onValueChange = {
            email.value = it
        },
        label = { Text(text = "Почта") },
        supportingText = {
            if (!email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))) {
                Text(text = "Введите корректный email")
            }
        },
        isError = !email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+")),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Orange,
            cursorColor = Orange,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color.White
        ),
        trailingIcon = {
            if (timer != null) {
                if (timer.value == 0F) {
                    Icon(
                        painter = painterResource(id = R.drawable.send_icon),
                        contentDescription = null,
                        tint = if (
                            isEnabled.value &&
                            email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))) Orange
                        else Color.LightGray,
                        modifier = Modifier.clickable {
                            if (isEnabled.value &&
                                email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))) {
                                val userApi = UserApiImpl()
                                isEnabled.value = false
                                var response = userApi.sendCode(email.value)
                                response.enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        call: Call<Unit>,
                                        response: Response<Unit>
                                    ) {
                                        if (response.code() == 200) {
                                            timer.value = 120F
                                        } else {
                                            errorMessage!!.value = "Не удалось отправить код"
                                            snackBarState!!.value = true
                                        }
                                        isEnabled.value = true
                                    }

                                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                                        errorMessage!!.value = "Не удалось отправить код"
                                        snackBarState!!.value = true
                                        isEnabled.value = true
                                    }

                                })
                            }
                        }
                    )
                } else {
                    CircularProgressIndicator(
                        progress = timer.value / 120F,
                        color = Orange,
                        strokeWidth = 4.dp
                    )
                }
            }


        },
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailCodeInput(token: MutableState<String>) {
    TextField(
        value = token.value, onValueChange = { token.value = it },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Orange,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color.White,
            cursorColor = Orange
        ),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        placeholder = { Text(text = "Код") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    password: MutableState<String>,
    inputEnabled: MutableState<Boolean>
) {
    TextField(
        value = password.value,
        singleLine = true,
        enabled = inputEnabled.value,
        supportingText = {
            if (!password.value.matches(Regex("[A-z0-9]{8,32}"))) {
                Text(text = "Пароль от 8 до 32 символов латиницей")
            }
        },
        shape = MaterialTheme.shapes.medium,
        onValueChange = {
            password.value = it
        },
        label = { Text(text = "Пароль") },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Orange,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color.White,
            cursorColor = Orange
        ),
        isError = !password.value.matches(Regex("[A-z0-9]{8,32}")),
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRepeatInput(
    password: MutableState<String>,
    passwordRepeat: MutableState<String>,
    inputEnabled: MutableState<Boolean>
) {
    TextField(
        value = passwordRepeat.value,
        singleLine = true,
        enabled = inputEnabled.value,
        supportingText = {
            if (passwordRepeat.value != password.value) {
                Text(text = "Пароли не совпадают")
            }
        },
        shape = MaterialTheme.shapes.medium,
        onValueChange = {
            passwordRepeat.value = it
        },
        label = { Text(text = "Повторите пароль") },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Orange,
            cursorColor = Orange,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color.White
        ),
        isError = passwordRepeat.value != password.value,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@Composable
fun RegistrationButton(
    enabled: MutableState<Boolean>,
    inputEnabled: MutableState<Boolean>,
    email: MutableState<String>,
    password: MutableState<String>,
    snackBarState: MutableState<Boolean>,
    errorMessage: MutableState<String>,
    token: MutableState<String>,
    onNavigateToLogin: () -> Unit
) {
    val clicked = remember { mutableStateOf(false) }
    TextButton(
        onClick = {
            clicked.value = true
            enabled.value = false
            inputEnabled.value = false
            val userApi = UserApiImpl()
            val response =
                userApi.registration(
                    token = token.value,
                    UserDTO(
                        email = email.value,
                        password = password.value
                    )
                )
            response.enqueue(object : Callback<UserProfileDTO> {
                override fun onResponse(
                    call: Call<UserProfileDTO>,
                    response: Response<UserProfileDTO>
                ) {
                    if (response.code() == 201) {
                        Log.d("server", response.code().toString())
                        User.EMAIL = email.value
                        User.PASSWORD = password.value
                        with(User.sharedPrefs.edit()) {
                            putString("email", User.EMAIL)
                            putString("password", User.PASSWORD)
                            apply()
                        }
                        onNavigateToLogin()
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
                        errorMessage.value = jsonObj
                        snackBarState.value = true
                        clicked.value = false
                        enabled.value = true
                        inputEnabled.value = true
                    }
                }

                override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                    errorMessage.value = "Ошибка подключения"
                    snackBarState.value = true
                    Log.d("server", t.message.toString())
                    clicked.value = false
                    enabled.value = true
                    inputEnabled.value = true
                }

            })

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = Color.Black,
            contentColor = Color.DarkGray
        ),
        enabled = enabled.value,
        modifier = Modifier.size(256.dp, 40.dp),
    ) {
        if (clicked.value) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp, 24.dp), color = Orange)
        } else {
            Text(text = "Зарегистрироваться")
        }
    }
}

@Composable
fun LoginSuggestionText() {
    Text(text = "Уже зарегистрированы?", color = Color.White)
}

@Composable
fun LoginButton(inputEnabled: MutableState<Boolean>, onNavigateToLogin: () -> Unit) {
    TextButton(
        onClick = { onNavigateToLogin() },
        contentPadding = PaddingValues(start = 64.dp, end = 64.dp),
        enabled = inputEnabled.value,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.DarkGray,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.DarkGray
        )
    ) {
        Text(text = "Войти")
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        RegistrationScreen({})
    }
}