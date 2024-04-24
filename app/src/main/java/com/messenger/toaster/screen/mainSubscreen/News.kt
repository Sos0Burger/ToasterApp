package com.messenger.toaster.screen.mainSubscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.composable.Post
import com.messenger.toaster.data.NewsTab
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.ResponsePostDTO
import com.messenger.toaster.ui.theme.Orange
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalFoundationApi::class, ExperimentalStdlibApi::class)
@Composable
fun News(navController: NavController) {

    val context = LocalContext.current

    val posts: MutableList<MutableState<ResponsePostDTO>> = remember {
        mutableStateListOf()
    }
    val postCount = remember {
        derivedStateOf { posts.size }
    }

    val postPage = remember {
        mutableStateOf(0)
    }
    val pagerState = rememberPagerState(0) {
        2
    }

    val postScrollState = rememberLazyListState()
    val endReached by remember {
        derivedStateOf {
            !postScrollState.canScrollForward && (posts.size >= postCount.value * 15) && (posts.size != 0 || postPage.value == 0)
        }
    }
    val isPostLoading = remember {
        mutableStateOf(false)
    }

    fun getPosts() {
        isPostLoading.value = true
        val userApi = UserApiImpl()
        val response = userApi.getFeed(User.getCredentials(), postPage.value)

        response.enqueue(object : Callback<List<ResponsePostDTO>> {
            override fun onResponse(
                call: Call<List<ResponsePostDTO>>,
                response: Response<List<ResponsePostDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()!!.forEach { posts.add(mutableStateOf(it)) }
                    postPage.value++
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
                isPostLoading.value = false
            }

            override fun onFailure(call: Call<List<ResponsePostDTO>>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                isPostLoading.value = false
            }

        })
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp), color = Color.Black
    ) {
        TabRow(selectedTabIndex = pagerState.currentPage,
            ) {
            NewsTab.entries.forEachIndexed { index, newsTab ->
                Tab(selected = pagerState.currentPage == index, onClick = { /*TODO*/ }) {

            }  }
        }
        LazyColumn(modifier = Modifier
            .fillMaxSize(1f)
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)) {
            item {
                if (posts.size == 0 && !isPostLoading.value) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(1f)) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Здесь пока пусто T-T",
                            color = Orange,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            items(count = postCount.value) { index ->
                Post(post = posts[index], true, navController)
            }
        }

    }

    if (endReached && !isPostLoading.value) {
        getPosts()
    }

}

@Preview(showBackground = true)
@Composable
fun NewsPreview() {
    News(rememberNavController())
}