package com.messenger.toaster.screen

import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.messenger.toaster.R
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.converter.TimeConverter
import com.messenger.toaster.converter.monthsBetweenUnixTimes
import com.messenger.toaster.data.FriendStatus
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FriendDTO
import com.messenger.toaster.dto.ResponseMessageDTO
import com.messenger.toaster.dto.UserProfileDTO
import com.messenger.toaster.ui.theme.Graphite
import com.messenger.toaster.ui.theme.Orange
import com.messenger.toaster.viewmodel.MessagesViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatMessagesScreen(
    id: String,
    messagesViewModel: MessagesViewModel = viewModel(),
    navController: NavController,
    onBack: () -> Unit,
    onProfile: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val messages by messagesViewModel.messages.collectAsState()
    val images by messagesViewModel.images.collectAsState()
    val isLoaded by messagesViewModel.isLoaded.collectAsState()

    val imageCount = remember {
        derivedStateOf {
            images.size
        }
    }
    val context = LocalContext.current

    val profile = remember {
        mutableStateOf(
            UserProfileDTO(
                id.toInt(),
                null,
                ArrayList(),
                null,
                FriendStatus.NOTHING,
                false,
                1312341412
            )
        )
    }
    val message = remember {
        mutableStateOf("")
    }
    val messagePage by messagesViewModel.page.collectAsState()
    val isProfileLoading = remember {
        mutableStateOf(true)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size > 10) {
            Toast.makeText(context, "Максимум 10 изображений", Toast.LENGTH_SHORT).show()
        } else {
            messagesViewModel.viewModelScope.launch {
                messagesViewModel.upload(uris, context)
            }
        }
    }

    val scrollState = rememberScrollState(0)
    val messageScrollState = rememberLazyListState()
    val endReached by remember {
        derivedStateOf {
            (
                    messageScrollState.firstVisibleItemIndex == messages.size - 15 ||
                            !messageScrollState.canScrollForward
                    ) && (messages.size >= messagePage * 30)
        }
    }

    val inputEnabled = remember {
        mutableStateOf(true)
    }

    fun getProfile() {
        isProfileLoading.value = false
        val userApi = UserApiImpl()
        val response = userApi.getUserProfile(id.toInt(), User.getCredentials())
        response.enqueue(object : Callback<UserProfileDTO> {
            override fun onResponse(
                call: Call<UserProfileDTO>,
                response: Response<UserProfileDTO>
            ) {
                if (response.isSuccessful) {
                    profile.value = response.body()!!
                } else {
                    val jsonObj =
                        if (response.errorBody() != null) response.errorBody()!!.byteString()
                            .utf8() else response.code().toString()
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }

    if (isProfileLoading.value) {
        getProfile()
    }
    val isEdit = remember {
        mutableStateOf(false)
    }
    var editMessage: MutableState<ResponseMessageDTO>? = null
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp), color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxSize(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onBack() },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Orange)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                OutlinedButton(
                    onClick = { onProfile() },
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(1.dp),
                    border = BorderStroke(0.dp, Color.Transparent),
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = if (profile.value.image == null)
                            "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                        else
                            ImageRequest.Builder(LocalContext.current)
                                .data(
                                    RetrofitClient.getInstance().baseUrl().toString() +
                                            "file/" +
                                            profile.value.image!!.id
                                )
                                .crossfade(true)
                                .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(color = Color.DarkGray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = profile.value.nickname ?: "Альтернативное имя не указано",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ID: " + profile.value.id.toString(),
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (profile.value.isOnline) "В сети" else {
                                    DateUtils.getRelativeTimeSpanString(
                                        profile.value.last_online,
                                        System.currentTimeMillis(),
                                        DateUtils.MINUTE_IN_MILLIS
                                    ).toString()
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Orange
                            )
                        }

                    }
                }
                IconButton(onClick = {
                    getProfile()
                    messagesViewModel.refresh(id.toInt(), context)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.update),
                        contentDescription = null,
                        tint = Orange
                    )
                }
            }
            Divider(
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, end = 16.dp, start = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                state = messageScrollState
            ) {
                items(
                    key = { index -> messages.getOrNull(index)?.id ?: index },
                    count = messages.size
                ) { index ->
                    val isDropDown = remember {
                        mutableStateOf(false)
                    }
                    val isDialog = remember {
                        mutableStateOf(false)
                    }
                    if (messages.getOrNull(index) != null) {
                        Row(
                            horizontalArrangement = if (messages[index].sender.id != profile.value.id)
                                Arrangement.End
                            else
                                Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        if (messages[index].sender.id == User.USER_ID) {
                                            isDropDown.value = true
                                        }
                                    })
                                }
                        ) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = if
                                                             (messages[index].sender.id != profile.value.id)
                                        Color.DarkGray else
                                        Graphite
                                ),
                                modifier = Modifier
                                    .padding(
                                        if (messages[index].sender.id != profile.value.id) PaddingValues(
                                            start = 64.dp,
                                            end = 8.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        ) else PaddingValues(
                                            start = 8.dp,
                                            end = 64.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        )
                                    )
                            ) {
                                Text(
                                    text = messages[index].text ?: "",
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    fontSize = 16.sp
                                )
                                if (messages[index].attachments.isNotEmpty()) {
                                    val pagerState = rememberPagerState(
                                        initialPageOffsetFraction = 0.5F, // Необязательно, для предзагрузки страниц
                                        initialPage = 0 // Индекс начальной страницы
                                    ) {
                                        try {
                                            messages[index].attachments.size
                                        } catch (ex: IndexOutOfBoundsException) {
                                            0
                                        }
                                    }

                                    Box() {
                                        HorizontalPager(
                                            modifier = Modifier.fillMaxSize(),
                                            state = pagerState
                                        ) { page ->
                                            Box(Modifier.background(color = Color.Gray)) {
                                                AsyncImage(
                                                    model = if (messages[index].attachments.isNotEmpty())
                                                        ImageRequest.Builder(LocalContext.current)
                                                            .data(
                                                                RetrofitClient.getInstance()
                                                                    .baseUrl()
                                                                    .toString() +
                                                                        "file/" +
                                                                        messages[index].attachments[page].id
                                                            )
                                                            .crossfade(true)
                                                            .build()
                                                    else
                                                        null,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .clip(RectangleShape)
                                                        .height(512.dp)
                                                        .pointerInput(Unit) {
                                                            detectTapGestures(onTap = {
                                                                navController
                                                                    .navigate(
                                                                        "message/" +
                                                                                messages[index].id +
                                                                                "/images/" +
                                                                                index
                                                                    )
                                                            },
                                                                onLongPress = {
                                                                    isDropDown.value = true
                                                                })

                                                        }
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
                                                                + messages[index].attachments.size),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                        }

                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (monthsBetweenUnixTimes(
                                                messages[index].date,
                                                System.currentTimeMillis()
                                            ) < 1
                                        ) DateUtils.getRelativeTimeSpanString(
                                            messages[index].date,
                                            System.currentTimeMillis(),
                                            DateUtils.MINUTE_IN_MILLIS
                                        ).toString()
                                        else
                                            TimeConverter.longToLocalTime(messages[index].date),
                                        textAlign = TextAlign.End,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(all = 4.dp),
                                        color = Color.White
                                    )
                                    if (messages[index].sender.id == User.USER_ID) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.check_icon),
                                            contentDescription = null,
                                            tint = if (messages[index].read) Orange else Color.Black
                                        )
                                    }

                                }
                                DropdownMenu(
                                    expanded = isDropDown.value,
                                    onDismissRequest = { isDropDown.value = false }) {
                                    DropdownMenuItem(
                                        text = { Text(text = "Редактировать") },
                                        onClick = {
                                            editMessage = mutableStateOf(messages[index])
                                            message.value = messages[index].text.toString()
                                            messagesViewModel.setImages(messages[index].attachments)
                                            isEdit.value = true
                                            isDropDown.value = false
                                        })
                                    DropdownMenuItem(text = { Text(text = "Удалить") }, onClick = {
                                        isDialog.value = true
                                        isDropDown.value = false
                                    })
                                }
                                if (isDialog.value) {
                                    val buttonColor =
                                        ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                    AlertDialog(
                                        onDismissRequest = { isDialog.value = false },
                                        confirmButton = {
                                            Button(onClick = {
                                                messagesViewModel.deleteMessage(
                                                    context,
                                                    messages[index]
                                                ) {
                                                    isDialog.value = false
                                                }
                                            }, colors = buttonColor) {
                                                Text(
                                                    text = "Да",
                                                    color = Orange,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        dismissButton = {
                                            Button(
                                                onClick = { isDialog.value = false },
                                                colors = buttonColor
                                            ) {
                                                Text(text = "Нет", color = Color.White)
                                            }
                                        },
                                        title = {
                                            Text(
                                                text = "Удалить сообщение?",
                                                color = Color.White,
                                                fontSize = 16.sp
                                            )
                                        })
                                }
                            }

                        }
                    }

                }
            }
            LazyRow(
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(imageCount.value) { index ->
                    Button(
                        onClick = {
                            messagesViewModel.removeImage(index)
                        },
                        enabled = inputEnabled.value,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(64.dp, 96.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = RetrofitClient.getInstance()
                                    .baseUrl().toString() + "file/" + images[index].id
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(colors = IconButtonDefaults.iconButtonColors(contentColor = Orange),
                    onClick = {
                        launcher.launch("image/*")
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.attachment_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(45f)
                            .size(48.dp)
                    )
                }
                LaunchedEffect(scrollState.maxValue) {
                    scrollState.scrollTo(scrollState.maxValue)
                }
                TextField(
                    value = message.value,
                    onValueChange = {
                        message.value = it
                    },
                    enabled = inputEnabled.value,
                    shape = MaterialTheme.shapes.extraLarge,
                    label = { Text(text = "Сообщение", color = Color.LightGray) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = Color.DarkGray,
                        cursorColor = Orange,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .heightIn(min = 48.dp, max = 96.dp)
                        .widthIn(max = 256.dp),
                    trailingIcon = {
                        if (isEdit.value) {
                            Icon(
                                painter = painterResource(id = R.drawable.cancel_icon),
                                contentDescription = null,
                                tint = Orange,
                                modifier = Modifier.clickable {
                                    isEdit.value = false
                                    message.value = ""
                                    messagesViewModel.clearImages()
                                }
                            )
                        }
                    }
                )
                IconButton(
                    onClick = {
                        inputEnabled.value = false
                        if (isEdit.value) {
                            if (editMessage != null) {
                                messagesViewModel.editMessage(
                                    context,
                                    editMessage!!.value.id,
                                    message,
                                    images.map { it.id }.toList(),
                                    inputEnabled
                                ) {
                                    coroutineScope.launch {
                                        messagesViewModel.clearImages()
                                        messageScrollState.animateScrollToItem(0)
                                    }
                                    isEdit.value = false
                                }
                            }
                        } else {
                            messagesViewModel.sendMessage(
                                context,
                                message,
                                FriendDTO(
                                    profile.value.id,
                                    profile.value.nickname,
                                    profile.value.image
                                ),
                                images.map { it.id }.toList(),
                                inputEnabled
                            ) {
                                coroutineScope.launch {
                                    messagesViewModel.clearImages()
                                    messageScrollState.animateScrollToItem(0)
                                }
                            }
                        }


                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Orange,
                        disabledContentColor = Color.DarkGray
                    ),
                    enabled = (message.value.isNotEmpty() || imageCount.value > 0) && inputEnabled.value
                ) {
                    if (inputEnabled.value) {
                        Icon(
                            painter = painterResource(R.drawable.send_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    } else {
                        CircularProgressIndicator(color = Orange, modifier = Modifier.size(48.dp))
                    }

                }
            }
        }
    }

    if (endReached) {
        LaunchedEffect(Unit) {
            messagesViewModel.loadNextPage(id.toInt(), context)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatMessagesScreenPreview() {
    ChatMessagesScreen("1", navController = rememberNavController(), onBack = {}, onProfile = {})
}

