package com.messenger.messengerapp.screen

import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.messenger.messengerapp.R
import com.messenger.messengerapp.ui.theme.Orange

@Composable
fun RegistrationScreen(onNavigateToLogin: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordRepeat = remember { mutableStateOf("") }
    val enabled = remember {
        mutableStateOf(
            false
        )
    }
    val inputEnabled = remember {
        mutableStateOf(true)
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIcon()

        Spacer(modifier = Modifier.padding(top = 16.dp))

        EmailInput(email = email, inputEnabled) {
            if (email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))
                && password.value.matches(Regex("[A-z0-9]{8,32}"))
                && password.value == passwordRepeat.value
            ) {
                enabled.value = true
            }
        }

        PasswordInput(password = password, inputEnabled) {
            if (email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))
                && password.value.matches(Regex("[A-z0-9]{8,32}"))
                && password.value == passwordRepeat.value
            ) {
                enabled.value = true
            }
        }

        PasswordRepeatInput(password = password, passwordRepeat = passwordRepeat, inputEnabled) {
            if (email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))
                && password.value.matches(Regex("[A-z0-9]{8,32}"))
                && password.value == passwordRepeat.value
            ) {
                enabled.value = true
            }
        }

        Spacer(modifier = Modifier.padding(top = 16.dp))

        RegistrationButton(
            enabled = enabled,
            inputEnabled = inputEnabled
        )
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {


            LoginSuggestionText()

            Spacer(modifier = Modifier.padding(top = 4.dp))

            LoginButton(inputEnabled)
        }

    }
}

@Composable
fun AppIcon() {
    Image(
        painter = painterResource(id = R.drawable.icon),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp, 100.dp)
            .padding(top = 4.dp, bottom = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInput(email: MutableState<String>, inputEnabled: MutableState<Boolean>, checkInput: () -> Unit) {
    TextField(
        value = email.value,
        singleLine = true,
        enabled = inputEnabled.value,
        shape = MaterialTheme.shapes.medium,
        onValueChange = {
            email.value = it
            checkInput()
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
            errorCursorColor = Color.Gray,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color.White
        ),
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(password: MutableState<String>, inputEnabled: MutableState<Boolean>, checkInput: () -> Unit) {
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
            checkInput()
        },
        label = { Text(text = "Пароль") },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Color.Gray,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color.White
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
    inputEnabled: MutableState<Boolean>,
    checkInput: () -> Unit
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
            checkInput()
        },
        label = { Text(text = "Повторите пароль") },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Color.Gray,
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
    inputEnabled: MutableState<Boolean>
) {
    val clicked = remember { mutableStateOf(false) }

    TextButton(
        onClick = {
            clicked.value = true
            enabled.value = false
            inputEnabled.value = false
            /*TODO*/
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
fun LoginButton(inputEnabled: MutableState<Boolean>) {
    TextButton(
        onClick = { /*TODO*/ },
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