package com.messenger.toaster.screen.mainSubscreen

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.messenger.toaster.R
import com.messenger.toaster.api.impl.FileApiImpl
import com.messenger.toaster.api.impl.PostApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.dto.RequestPostDTO
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.requestbody.InputStreamRequestBody
import com.messenger.toaster.ui.theme.Orange
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController:NavController) {

    val scrollState = rememberScrollState()
    val text = remember {
        mutableStateOf("")
    }
    LaunchedEffect(scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    val imageUris: MutableList<Uri> = remember {
        mutableStateListOf()
    }
    val imageCount = remember {
        derivedStateOf {
            imageUris.size
        }
    }

    val context = LocalContext.current

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

    val inputEnabled = remember {
        mutableStateOf(true)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp),
        color = Color.Black
    ) {
        Column(modifier = Modifier.fillMaxSize(1f)) {
            Row(modifier = Modifier.fillMaxWidth(1f)) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                              },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cancel_icon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { sendFilePost(context, imageUris , text, inputEnabled, navController ) },
                    colors = IconButtonDefaults.iconButtonColors(
                        disabledContentColor = Color.DarkGray,
                        disabledContainerColor = Color.Transparent,
                        containerColor = Color.Transparent,
                        contentColor = Orange
                    ),
                    enabled = text.value.isNotEmpty()||imageUris.isNotEmpty()&&inputEnabled.value
                ) {

                    if (inputEnabled.value) {
                        Icon(
                            painter = painterResource(id = R.drawable.check_icon),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    else{
                        CircularProgressIndicator(color = Orange, modifier = Modifier.size(32.dp))
                    }
                }
            }
            Divider(
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            TextField(
                value = text.value,
                onValueChange = { text.value = it },
                placeholder = { Text(text = "Что у вас нового?", fontSize = 20.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Black,
                    textColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(fontSize = 20.sp),
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth(1f)
                    .padding(8.dp)
                    .height(
                        LocalConfiguration
                            .current
                            .screenHeightDp
                            .dp / 2
                    ),
                shape = MaterialTheme.shapes.medium,
                enabled = inputEnabled.value
            )
            LazyRow(
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                item {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxHeight(1f)
                            .width(128.dp),
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        ),
                        enabled = inputEnabled.value
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_photo_icon),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                items(count = imageCount.value) { index ->
                    Button(
                        onClick = { imageUris.removeAt(index) },
                        modifier = Modifier
                            .fillMaxHeight(1f)
                            .width(128.dp),
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                        ),
                        enabled = inputEnabled.value
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUris[index]),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

            }
        }
    }
}

fun sendFilePost(
    context: Context,
    imageUris: MutableList<Uri>,
    text: MutableState<String>,
    inputEnabled: MutableState<Boolean>,
    navController: NavController
) {
    inputEnabled.value = false
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
            val response = fileApi.upload(User.getCredentials(), item)
            response.enqueue(object : Callback<FileDTO> {
                override fun onResponse(call: Call<FileDTO>, response: Response<FileDTO>) {
                    if (response.isSuccessful) {
                        uploadedImageIds.add(response.body()!!.id)
                        if (uploadedImageIds.size == files.size) {
                            sendPost(context, text, uploadedImageIds, inputEnabled, navController)
                        }
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
                        Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT)
                            .show()
                        inputEnabled.value = true
                    }
                }

                override fun onFailure(call: Call<FileDTO>, t: Throwable) {
                    Log.d("server", t.message.toString())
                    Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                    inputEnabled.value = true
                }
            })
        }
    } else {
        sendPost(context, text, ArrayList(), inputEnabled, navController)
    }
}

private fun sendPost(
    context: Context,
    text: MutableState<String>,
    uploadedImageIds: MutableList<Int>,
    inputEnabled: MutableState<Boolean>,
    navController: NavController
) {
    val postApi = PostApiImpl()

    val response = postApi.createPost(
        RequestPostDTO(
            text = text.value,
            date = Date().time,
            attachments = uploadedImageIds
        ),
        User.getCredentials()
    )
    response.enqueue(object : Callback<ResponsePostDTO>{
        override fun onResponse(call: Call<ResponsePostDTO>, response: Response<ResponsePostDTO>) {
            if (response.isSuccessful) {
                navController.navigate("profile/"+ User.USER_ID!!)
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
                Toast.makeText(context, jsonObj, Toast.LENGTH_SHORT)
                    .show()
                inputEnabled.value = true
            }
        }

        override fun onFailure(call: Call<ResponsePostDTO>, t: Throwable) {
            Log.d("server", t.message.toString())
            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            inputEnabled.value = true
        }

    })

}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    CreatePostScreen(NavController(LocalContext.current))
}