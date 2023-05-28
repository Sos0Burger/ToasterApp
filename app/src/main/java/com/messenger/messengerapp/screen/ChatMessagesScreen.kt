package com.messenger.messengerapp.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.messenger.messengerapp.R
import com.messenger.messengerapp.api.impl.FileApiImpl
import com.messenger.messengerapp.dto.FileDTO
import com.messenger.messengerapp.dto.FriendDTO
import com.messenger.messengerapp.dto.MessageDTO
import com.messenger.messengerapp.requestbody.InputStreamRequestBody
import com.messenger.messengerapp.ui.theme.Orange
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMessagesScreen(friendDTO: FriendDTO) {
    var messages: MutableList<MessageDTO> = remember {
        mutableListOf()
    }


    var imageUris: MutableList<Uri> = remember {
        mutableListOf()
    }
    val imageCount = remember {
        mutableStateOf(0)
    }
    val uploadedImageIds: MutableList<Int> = remember {
        mutableListOf()
    }
    val context = LocalContext.current


    val message = remember {
        mutableStateOf("")
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size > 10) {
            Toast.makeText(context, "Максимум 10 изображений", Toast.LENGTH_SHORT).show()
        } else {
            imageUris = uris.toMutableList()
            imageCount.value = uris.size
        }
    }

    val scrollState = rememberScrollState(0)



    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxSize(1f)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = { /*TODO*/ },
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    AsyncImage(
                        model = friendDTO.image
                            ?: "https://memepedia.ru/wp-content/uploads/2021/01/anonimus-mem-6.jpg",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Column {
                        Text(
                            text = friendDTO.nickname ?: "Альтернативное имя не указано",
                            fontSize = 20.sp
                        )
                        Text(text = "ID: " + friendDTO.id.toString(), fontSize = 12.sp)
                    }
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {

                //todo
            }
            LazyRow(
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(imageCount.value) { index ->
                    Button(
                        onClick = {
                            imageUris.removeAt(index)
                            imageCount.value -= 1
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(64.dp, 96.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
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
                    shape = MaterialTheme.shapes.extraLarge,
                    label = { Text(text = "Сообщение", color = Color.DarkGray) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = Color.Gray,
                        cursorColor = Orange
                    ),
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .heightIn(min = 48.dp, max = 96.dp)
                        .widthIn(max = 256.dp)
                )
                IconButton(
                    onClick = {
                        sendFiles(
                            imageUris,
                            context,
                            uploadedImageIds
                        )
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Orange,
                        disabledContentColor = Color.DarkGray
                    ),
                    enabled = message.value.isNotEmpty()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.send_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatMessagesScreenPreview() {
    ChatMessagesScreen(FriendDTO(16, "Анонимус228", null))
}

fun sendFiles(
    files: List<Uri>,
    context: Context,
    uploadedImageIds: MutableList<Int>
) {
    val fileApi = FileApiImpl()
    val multipartFiles = ArrayList<MultipartBody.Part>()
    val cR = context.contentResolver
    for (item in files) {
        multipartFiles.add(
            MultipartBody.Part.createFormData(
                "attachment",
                item.path,
                InputStreamRequestBody(cR.getType(item)!!.toMediaType(), context.contentResolver, item)
            )
        )
    }

    for (item in multipartFiles) {
        val response = fileApi.upload(item)

        response.enqueue(object : Callback<FileDTO> {
            override fun onResponse(call: Call<FileDTO>, response: Response<FileDTO>) {
                if (response.code() == 201) {
                    uploadedImageIds.add(response.body()!!.id)
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                    Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()

                }
            }
            override fun onFailure(call: Call<FileDTO>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }

}