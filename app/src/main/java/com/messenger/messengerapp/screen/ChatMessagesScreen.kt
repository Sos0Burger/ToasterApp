package com.messenger.messengerapp.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.messenger.messengerapp.R
import com.messenger.messengerapp.api.impl.FileApiImpl
import com.messenger.messengerapp.api.impl.MessageApiImpl
import com.messenger.messengerapp.converter.TimeConverter
import com.messenger.messengerapp.data.User
import com.messenger.messengerapp.dto.FileDTO
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.RequestMessageDTO
import com.messenger.messengerapp.dto.ResponseMessageDTO
import com.messenger.messengerapp.requestbody.InputStreamRequestBody
import com.messenger.messengerapp.ui.theme.Graphite
import com.messenger.messengerapp.ui.theme.Orange
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatMessagesScreen(friendDTO: FriendDTO) {
    val messages: MutableList<ResponseMessageDTO> = remember {
        mutableStateListOf()
    }
    User.messages = messages

    val imageUris: MutableList<Uri> = remember {
        mutableStateListOf()
    }
    val imageCount = remember {
        derivedStateOf {
            imageUris.size
        }
    }
    val context = LocalContext.current


    val message = remember {
        mutableStateOf("")
    }
    val messagePage = remember {
        mutableStateOf(0)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size > 10) {
            Toast.makeText(context, "Максимум 10 изображений", Toast.LENGTH_SHORT).show()
        } else {
            imageUris.addAll(uris)
        }
    }

    val scrollState = rememberScrollState(0)
    val messageScrollState = rememberLazyListState()
    val endReached by remember {
        derivedStateOf {
            !messageScrollState.canScrollForward && (messages.size >= messagePage.value * 15)
        }
    }
    val inputEnabled = remember {
        mutableStateOf(true)
    }

    fun getMessages(page: Int) {
        val messageApi = MessageApiImpl()

        val response = messageApi.getDialog(User.USER_ID!!, friendDTO.id, page)
        response.enqueue(object : Callback<List<ResponseMessageDTO>> {
            override fun onResponse(
                call: Call<List<ResponseMessageDTO>>,
                response: Response<List<ResponseMessageDTO>>
            ) {
                if (response.code() == 200) {
                    messages.addAll(response.body()!!)
                    messagePage.value += 1
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ResponseMessageDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }

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
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = {/*todo*/ },
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    AsyncImage(
                        model = if (friendDTO.image == null)
                            "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg"
                        else
                            ImageRequest.Builder(LocalContext.current)
                                .data(friendDTO.image)
                                .crossfade(true)
                                .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(color = Color.DarkGray)
                    )
                    Column {
                        Text(
                            text = friendDTO.nickname ?: "Альтернативное имя не указано",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "ID: " + friendDTO.id.toString(),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                state = messageScrollState
            ) {
                items(count = messages.size) { index ->
                    Row(
                        horizontalArrangement = if (messages[index].sender.id == User.USER_ID) Arrangement.End else Arrangement.Start,
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = if (messages[index].sender.id == User.USER_ID) Color.DarkGray else Graphite),
                            modifier = Modifier
                                .padding(
                                    if (messages[index].sender.id == User.USER_ID) PaddingValues(
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
                            if(messages[index].attachments.isNotEmpty()) {
                                val pagerState = rememberPagerState()
                                Box() {
                                    HorizontalPager(
                                        pageCount = messages[index].attachments.size,
                                        modifier = Modifier.fillMaxSize(),
                                        state = pagerState
                                    ) { page ->
                                        Box(Modifier.background(color = Color.Gray)) {
                                            AsyncImage(
                                                model = if(messages[index].attachments.isNotEmpty())
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(messages[index].attachments[page].url)
                                                    .crossfade(true)
                                                    .build()
                                                else
                                                    null,
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
                                                            + messages[index].attachments.size),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }

                                }
                            }
                            Text(
                                text = TimeConverter.longToLocalTime(messages[index].date),
                                textAlign = TextAlign.End,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(all = 4.dp),
                                color = Color.White
                            )
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
                            imageUris.removeAt(index)
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
                            painter = rememberAsyncImagePainter(model = imageUris[index]),
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
                        .widthIn(max = 256.dp)
                )
                IconButton(
                    onClick = {
                        inputEnabled.value = false
                        if (imageUris.isEmpty()) {
                            sendMessage(
                                context,
                                message,
                                friendDTO,
                                messages,
                                ArrayList(),
                                imageUris,
                                inputEnabled
                            )
                        } else {
                            sendFileMessage(
                                context = context,
                                imageUris = imageUris,
                                messages = messages,
                                message = message,
                                inputEnabled = inputEnabled,
                                friendDTO = friendDTO
                            )
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
            getMessages(messagePage.value)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ChatMessagesScreenPreview() {
    ChatMessagesScreen(FriendDTO(16, "Анонимус228", null))
}

fun sendMessage(
    context: Context,
    message: MutableState<String>,
    friendDTO: FriendDTO,
    messages: MutableList<ResponseMessageDTO>,
    uploadedImageIds: MutableList<Int>,
    imageUris: MutableList<Uri>,
    inputEnabled: MutableState<Boolean>
) {
    val messageApi = MessageApiImpl()
    val requestMessage = RequestMessageDTO(
        message.value,
        User.USER_ID!!,
        friendDTO.id,
        Date().time,
        uploadedImageIds
    )
    val response = messageApi.send(requestMessage)
    response.enqueue(object : Callback<ResponseMessageDTO> {
        override fun onResponse(
            call: Call<ResponseMessageDTO>,
            response: Response<ResponseMessageDTO>
        ) {
            if (response.code() == 201) {
                messages.add(0, response.body()!!)
                message.value = ""
                imageUris.clear()
            } else {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                Log.d(
                    "server",
                    response.code().toString()
                )
                Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
            }
            inputEnabled.value = true
        }

        override fun onFailure(call: Call<ResponseMessageDTO>, t: Throwable) {
            Log.d("server", t.message.toString())
            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            inputEnabled.value = true
        }
    })

}

fun sendFileMessage(
    context: Context,
    imageUris: MutableList<Uri>,
    message: MutableState<String>,
    friendDTO: FriendDTO,
    messages: MutableList<ResponseMessageDTO>,
    inputEnabled: MutableState<Boolean>
) {
    val files = imageUris.toList()
    if (files.isNotEmpty()) {
        val fileApi = FileApiImpl()
        val multipartFiles = ArrayList<MultipartBody.Part>()
        val cR = context.contentResolver
        for (item in files) {
            multipartFiles.add(
                MultipartBody.Part.createFormData(
                    "attachment",
                    item.path,
                    InputStreamRequestBody(
                        cR.getType(item)!!.toMediaType(),
                        context.contentResolver,
                        item
                    )
                )
            )
        }
        val uploadedImageIds = ArrayList<Int>()
        for (item in multipartFiles) {
            val response = fileApi.upload(item)
            response.enqueue(object : Callback<FileDTO> {
                override fun onResponse(call: Call<FileDTO>, response: Response<FileDTO>) {
                    if (response.code() == 201) {
                        uploadedImageIds.add(response.body()!!.id)
                        if (uploadedImageIds.size == files.size) {
                            sendMessage(
                                context,
                                message,
                                friendDTO,
                                messages,
                                uploadedImageIds,
                                imageUris,
                                inputEnabled
                            )
                        }
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        Log.d(
                            "server",
                            response.code().toString()
                        )
                        Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT)
                            .show()

                    }
                }

                override fun onFailure(call: Call<FileDTO>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
