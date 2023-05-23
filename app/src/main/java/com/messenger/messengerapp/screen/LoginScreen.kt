package com.messenger.messengerapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.messenger.messengerapp.ui.theme.Orange

@Composable
fun LoginScreen(onNavigateToMainScreen: () -> Unit) {
    val inputEnabled = remember {
        mutableStateOf(true)
    }
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIcon()

        Spacer(modifier = Modifier.padding(top = 16.dp))

        EmailInput(email = email, inputEnabled){}

        PasswordInput(password = password, inputEnabled = inputEnabled ) {}

        Spacer(modifier = Modifier.padding(top = 16.dp))

        LoginScreenButton(inputEnabled = inputEnabled)

    }
}
@Composable
fun LoginScreenButton(inputEnabled: MutableState<Boolean>) {
    TextButton(
        onClick = {
                  inputEnabled.value = false
                  /*TODO*/ },
        contentPadding = PaddingValues(start = 64.dp, end = 64.dp),
        enabled = inputEnabled.value,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.DarkGray,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.DarkGray
        )
    ) {
        if(inputEnabled.value){
            Text(text = "Войти")
        }
        else{
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
        LoginScreen({})
    }
}