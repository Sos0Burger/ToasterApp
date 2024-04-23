package com.messenger.toaster.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.messenger.toaster.R
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.converter.formatNumber
import com.messenger.toaster.dto.ResponseChatDTO
import com.messenger.toaster.ui.theme.Orange

@Composable
fun ChatItem(chat: ResponseChatDTO, onClick:()->Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = {
                      onClick()
            },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Box {
                AsyncImage(
                    model = if (chat.image == null)
                        "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                    else
                        ImageRequest.Builder(LocalContext.current)
                            .data(
                                RetrofitClient.getInstance().baseUrl().toString() +
                                        "file/" +
                                        chat.image!!.id
                            )
                            .crossfade(true)
                            .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(color = Color.DarkGray)
                )
                Icon(
                    painter = painterResource(id = R.drawable.status),
                    contentDescription = null,
                    tint = if (chat.online) Orange else Color.Gray,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = chat.nickname ?: "Альтернативное имя не указано",
                    color = Color.White,
                    fontSize = 20.sp
                )
                Text(
                    text = "ID: " + chat.user.toString(),
                    fontSize = 10.sp,
                    color = Color.White
                )
                Text(
                    text = chat.latest,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (chat.unread != 0) {
                Text(
                    text = formatNumber(chat.unread),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Orange, shape = CircleShape)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 1250067)
@Composable
fun CommentItemPreview() {
    Column {
        ChatItem(
            chat = ResponseChatDTO(
                1,
                "Masunya",
                "Привет",
                1999943L,
                15,
                null,
                true
            )
        ){}
        Divider(
            color = Color.Gray,
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.CenterHorizontally)
        )
        ChatItem(
            chat = ResponseChatDTO(
                1,
                "Masunya",
                "Привет sifdsfisdfisdfisdfisdifisfjsifisfsifsifsdjfsfsdpofsdoijfsdoiffiodsiofdsoifds",
                1999943L,
                20000,
                null,
                false
            )
        ){}
    }

}