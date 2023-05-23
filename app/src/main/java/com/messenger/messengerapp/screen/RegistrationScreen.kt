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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIcon()

        Spacer(modifier = Modifier.padding(top = 16.dp))

        EmailInput(email = email)
        PasswordInput(password = password)
        PasswordRepeatInput(password = password, passwordRepeat = passwordRepeat)
    }
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RegistrationButton(email = email, password = password, passwordRepeat = passwordRepeat)
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
fun EmailInput(email: MutableState<String>) {
    TextField(
        value = email.value,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        onValueChange = { email.value = it },
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
            errorCursorColor = Color.Gray
        ),
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(password: MutableState<String>) {
    TextField(
        value = password.value,
        singleLine = true,
        supportingText = {
            if (!password.value.matches(Regex("[A-z0-9]{8,32}"))) {
                Text(text = "Пароль от 8 до 32 символов латиницей")
            }
        },
        shape = MaterialTheme.shapes.medium,
        onValueChange = { password.value = it },
        label = { Text(text = "Пароль") },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Color.Gray
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
    passwordRepeat: MutableState<String>
) {
    TextField(
        value = passwordRepeat.value,
        singleLine = true,
        supportingText = {
            if (passwordRepeat.value != password.value) {
                Text(text = "Пароли не совпадают")
            }
        },
        shape = MaterialTheme.shapes.medium,
        onValueChange = { passwordRepeat.value = it },
        label = { Text(text = "Повторите пароль") },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Gray,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = Orange,
            errorLabelColor = Color.DarkGray,
            errorCursorColor = Color.Gray
        ),
        isError = passwordRepeat.value != password.value,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@Composable
fun RegistrationButton(
    email: MutableState<String>,
    password: MutableState<String>,
    passwordRepeat: MutableState<String>
) {
    TextButton(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = Color.Black,
            contentColor = Color.DarkGray
        ),
        contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
        enabled = email.value.matches(Regex("[A-z0-9]{3,}@[a-z0-9]+\\.[a-z]+"))
                && password.value.matches(Regex("[A-z0-9]{8,32}"))
                && password.value == passwordRepeat.value,
    ) {
        Text(text = "Зарегистрироваться")
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