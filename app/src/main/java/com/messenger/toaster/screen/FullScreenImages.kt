package com.messenger.toaster.screen

import android.Manifest
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.messenger.toaster.R
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.api.impl.FileApiImpl
import com.messenger.toaster.api.impl.PostApiImpl
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.FileDTO
import com.messenger.toaster.ui.theme.Orange
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullScreenImages(post: String, initial: String, onBack: () -> Unit) {
    val context = LocalContext.current

    val zoomableState = rememberZoomableState()
    var attachments by remember { mutableStateOf(mutableListOf<FileDTO>()) }
    val attachmentsCount = attachments.size
    val pagerState = rememberPagerState(initial.toInt()) {
        attachmentsCount
    }

    fun downloadImage(id: Int, name: String) {
        val fileApi = FileApiImpl()
        val response = fileApi.download(id)
        response.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val directory =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(directory, name)
                    if (file.exists()){
                        Toast.makeText(
                            context,
                            "Файл " + file.absolutePath + " уже существует",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return
                    }
                    val inputStream = response.body()?.byteStream()
                    val outputStream = FileOutputStream(file)


                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    Toast.makeText(
                        context,
                        "Файл сохранен в " + file.absolutePath,
                        Toast.LENGTH_SHORT
                    )
                        .show()

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
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })

    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            downloadImage(
                attachments[pagerState.currentPage].id,
                attachments[pagerState.currentPage].name
            )
        } else if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            downloadImage(
                attachments[pagerState.currentPage].id,
                attachments[pagerState.currentPage].name
            )
        } else {
            Toast.makeText(context, "Разрешение не предоставлено", Toast.LENGTH_SHORT)
                .show()
        }
    }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp)
            .background(Color.Black),
        shape = RectangleShape,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = null,
                        tint = Orange
                    )
                }
                Text(
                    text = (pagerState.currentPage + 1).toString() + " из " + attachments.size,
                    color = Orange,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.download), contentDescription = null,
                    tint = Orange
                )
            }

        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            AsyncImage(
                model =
                ImageRequest.Builder(LocalContext.current)
                    .data(
                        RetrofitClient.getInstance().baseUrl().toString() +
                                "file/" +
                                attachments[page].id
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.None,
                modifier = Modifier
                    .background(color = Color.Black)
                    .fillMaxSize()
                    .zoomable(zoomableState)
            )
        }
    }
    fun getImages() {
        val postApi = PostApiImpl()
        val response = postApi.getPostImages(post.toInt(), User.getCredentials())
        response.enqueue(object : Callback<List<FileDTO>> {
            override fun onResponse(call: Call<List<FileDTO>>, response: Response<List<FileDTO>>) {
                if (response.isSuccessful) {
                    attachments = response.body()!!.toMutableList()
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
                }
            }

            override fun onFailure(call: Call<List<FileDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }
    LaunchedEffect(Unit) {
        getImages()
    }

}

@Preview(showBackground = true)
@Composable
fun FullScreenImagesPreview() {
    FullScreenImages("1", "1") {
    }
}
