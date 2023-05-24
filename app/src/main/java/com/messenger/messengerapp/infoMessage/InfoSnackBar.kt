package com.messenger.messengerapp.infoMessage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messenger.messengerapp.ui.theme.Orange

@Composable
fun InfoSnackBar(text:MutableState<String>, snackBarState:MutableState<Boolean>){
    if (snackBarState.value) {
        Snackbar(
            modifier = Modifier
                .height(48.dp)
                .width(256.dp),
            shape = MaterialTheme.shapes.extraLarge,
            dismissAction = {
                Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxSize()) {
                    Button(
                        onClick = { snackBarState.value = false },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Orange,
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(all = 0.dp)
                    ) {
                        Text(text = "OK", fontSize = 16.sp)
                    }
                }
            }
        ) {
            Text(text = text.value)
        }
    }
}