package com.messenger.messengerapp.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.messenger.messengerapp.converter.TimeConverter
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.ResponsePostDTO

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Post(post: ResponsePostDTO) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(1f)
            .background(color = Color.DarkGray, shape = MaterialTheme.shapes.medium)
    ) {
        Row(modifier = Modifier.padding(top = 8.dp)) {
            AsyncImage(
                model = post.creator.image
                    ?: "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(
                    text = post.creator.nickname
                        ?: "Альтернативное имя не указано",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = "ID: " + post.creator.id.toString(),
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = TimeConverter.longToLocalTime(post.date),
                    fontSize = 12.sp,
                    color = Color.White
                )

            }
        }

        Divider(
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, end = 16.dp, start = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(8.dp)
        ) {
            Text(
                text = post.text.toString(),
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )
        }
        if (post.attachments.isNotEmpty()) {
            val pagerState = rememberPagerState()
            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                HorizontalPager(
                    pageCount = post.attachments.size,
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState
                ) { page ->
                    Box(Modifier.background(color = Color.Gray)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.attachments[page].url)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RectangleShape)
                                .height(512.dp)
                        )
                    }
                }
                Row(
                    Modifier
                        .padding(4.dp)
                        .background(
                            Color.DarkGray,
                            shape = MaterialTheme.shapes.medium
                        ),
                ) {

                    Text(
                        text = (
                                (pagerState.currentPage + 1).toString()
                                        + " / "
                                        + post.attachments.size),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostPreview() {
    Post(
        post = ResponsePostDTO(
            1,
            "kfdfskjdsfsfskfskjfsmfjsfj",
            FriendDTO(1, "SosoBurger", null),
            1687860434639,
            ArrayList()
        )
    )
}