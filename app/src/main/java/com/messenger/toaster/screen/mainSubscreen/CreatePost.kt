package com.messenger.toaster.screen.mainSubscreen

import android.net.Uri
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.messenger.toaster.R
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.data.PostMode
import com.messenger.toaster.dto.RequestEditPostDTO
import com.messenger.toaster.ui.theme.Orange
import com.messenger.toaster.viewmodel.AllNewsViewModel
import com.messenger.toaster.viewmodel.PostViewModel
import com.messenger.toaster.viewmodel.ProfilePostViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    id: String? = null,
    mode: PostMode,
    postViewModel: PostViewModel = viewModel(),
    navController: NavController,
    index:Int? = null,
    newsViewModel: AllNewsViewModel? = null,
    profilePostViewModel: ProfilePostViewModel? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val text = remember {
        mutableStateOf("")
    }
    LaunchedEffect(scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }
    LaunchedEffect(Unit) {
        if (mode == PostMode.EDIT && id != null) {
            postViewModel.getPost(
                id = id,
                context = context,
                text = text
            ) { navController.popBackStack() }
        }
    }
    val images by postViewModel.images.collectAsState()
    val isLoaded by postViewModel.isLoaded.collectAsState()
    val post by postViewModel.post.collectAsState()
    val imageCount = remember {
        derivedStateOf {
            images.size
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size > 10) {
            Toast.makeText(context, "Максимум 10 изображений", Toast.LENGTH_SHORT).show()
        } else {
            postViewModel.viewModelScope.launch {
                postViewModel.upload(uris, context)
            }
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
                    onClick = {
                        if (mode == PostMode.CREATE) {
                            postViewModel.sendPost(
                                context,
                                text,
                                images.map { it.id },
                                inputEnabled,
                                navController
                            )
                        } else if (mode == PostMode.EDIT && id != null && index!=null) {
                            if (newsViewModel!=null){
                                postViewModel.updatePost(
                                    id = id.toInt(),
                                    post= RequestEditPostDTO(text = text.value, images.map { it.id }),
                                    context = context,
                                    inputEnabled = inputEnabled,
                                    index = index,
                                    allNewsViewModel = newsViewModel
                                ) {
                                    navController.popBackStack()
                                }
                            }
                            else if(profilePostViewModel!=null){
                                postViewModel.updatePost(
                                    id = id.toInt(),
                                    post= RequestEditPostDTO(text = text.value, images.map { it.id }),
                                    context = context,
                                    inputEnabled = inputEnabled,
                                    index = index,
                                    profilePostViewModel = profilePostViewModel
                                ) {
                                    navController.popBackStack()
                                }
                            }

                        }

                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        disabledContentColor = Color.DarkGray,
                        disabledContainerColor = Color.Transparent,
                        containerColor = Color.Transparent,
                        contentColor = Orange
                    ),
                    enabled = text.value.isNotEmpty() || images.isNotEmpty() && inputEnabled.value
                ) {

                    if (inputEnabled.value && isLoaded) {
                        Icon(
                            painter = painterResource(id = R.drawable.check_icon),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
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
                        onClick = { postViewModel.remove(index) },
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
                            painter = rememberAsyncImagePainter(
                                model = RetrofitClient.getInstance()
                                    .baseUrl().toString() + "file/" + images[index].id
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    CreatePostScreen(mode = PostMode.CREATE, navController = rememberNavController())
}