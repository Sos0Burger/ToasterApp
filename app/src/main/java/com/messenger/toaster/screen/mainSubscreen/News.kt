package com.messenger.toaster.screen.mainSubscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.messenger.toaster.composable.Post
import com.messenger.toaster.data.NewsTab
import com.messenger.toaster.ui.theme.Orange
import com.messenger.toaster.viewmodel.AllNewsViewModel
import com.messenger.toaster.viewmodel.FriendNewsViewModel
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalStdlibApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun News(
    navController: NavController,
    friendNewsViewModel: FriendNewsViewModel = viewModel(),
    allNewsViewModel: AllNewsViewModel = viewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val search = remember {
        mutableStateOf("")
    }
    val friendPosts by friendNewsViewModel.posts.collectAsState()
    val friendCurrentPage by friendNewsViewModel.currentPage.collectAsState()
    val isFriendRefreshing by friendNewsViewModel.isRefreshing.collectAsState()
    val isFriendLoading by friendNewsViewModel.isLoading.collectAsState()
    val friendsPostState = rememberLazyListState()

    val allPosts by allNewsViewModel.posts.collectAsState()
    val allCurrentPage by allNewsViewModel.currentPage.collectAsState()
    val isAllRefreshing by allNewsViewModel.isRefreshing.collectAsState()
    val isAllLoading by allNewsViewModel.isLoading.collectAsState()
    val allPostState = rememberLazyListState()


    val friendsPostCount = remember {
        derivedStateOf { friendPosts.size }
    }
    val allPostCount = remember {
        derivedStateOf { allPosts.size }
    }

    val pagerState = rememberPagerState(0) {
        2
    }

    val endReachedFriends by remember {
        derivedStateOf {
            !friendsPostState.canScrollForward &&
                    !isFriendLoading &&
                    (friendPosts.size >= friendCurrentPage * 15)
        }
    }
    val endReachedAll by remember {
        derivedStateOf {
            !allPostState.canScrollForward &&
                    !isAllLoading &&
                    (allPosts.size >= allCurrentPage * 15)
        }
    }
    var recomposeTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(recomposeTrigger) {
        // Пустой LaunchedEffect для принудительного обновления
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 58.dp), color = Color.Black
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Новости",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                contentColor = Orange,
                containerColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .padding(horizontal = 32.dp),
                        color = Orange
                    )
                }
            ) {
                NewsTab.entries.forEachIndexed { index, newsTab ->
                    Tab(
                        selected = pagerState.currentPage == index, onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selectedContentColor = Orange,
                        unselectedContentColor = Color.White
                    ) {
                        Text(text = newsTab.text, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> {
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isFriendRefreshing),
                            onRefresh = { friendNewsViewModel.refresh("", context) },
                            indicator = { state: SwipeRefreshState, trigger ->
                                SwipeRefreshIndicator(
                                    state = state,
                                    refreshTriggerDistance = trigger,
                                    scale = true,
                                    backgroundColor = Color.DarkGray,
                                    contentColor = Orange,
                                    shape = CircleShape
                                )
                            }
                        ) {
                            LazyColumn(
                                state = friendsPostState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                item {
                                    if (friendPosts.isEmpty() && !isFriendRefreshing) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth(1f)
                                        ) {
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
                                items(count = friendsPostCount.value) { index ->
                                    Post(
                                        post = friendPosts[index],
                                        true,
                                        navController,
                                        "news",
                                        index,
                                        onCommentRemove = {friendPosts[index].popularComment = null},
                                        onPostRemove = {},
                                        smashLikePost = {friendNewsViewModel.smashPostLike(index)},
                                        smashLikeComment = {friendNewsViewModel.smashCommentLike(index)}
                                    )
                                }
                            }
                        }
                        if (endReachedFriends && !isFriendRefreshing) {
                            friendNewsViewModel.loadNextPage("", context)
                        }
                    }

                    1 -> {
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isAllRefreshing),
                            onRefresh = { allNewsViewModel.refresh(search.value, context) },
                            indicator = { state: SwipeRefreshState, trigger ->
                                SwipeRefreshIndicator(
                                    state = state,
                                    refreshTriggerDistance = trigger,
                                    scale = true,
                                    backgroundColor = Color.DarkGray,
                                    contentColor = Orange,
                                    shape = CircleShape
                                )
                            }
                        ) {
                            LazyColumn(
                                state = allPostState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                item {
                                    TextField(
                                        value = search.value,
                                        singleLine = true,
                                        colors = TextFieldDefaults.textFieldColors(
                                            containerColor = Color.DarkGray,
                                            textColor = Color.White,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = Orange
                                        ),
                                        shape = MaterialTheme.shapes.medium,
                                        onValueChange = {
                                            search.value = it
                                            allNewsViewModel.refresh(search.value, context)
                                        },
                                        placeholder = { Text(text = "Поиск", color = Color.White) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                item {
                                    if (allPosts.isEmpty() && !isAllRefreshing) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth(1f)
                                        ) {
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
                                items(
                                    count = allPostCount.value,
                                    key = { index -> allPosts[index].id }) { index ->
                                    Post(
                                        post = allPosts[index],
                                        true,
                                        navController,
                                        "news",
                                        index,
                                        onPostRemove = {
                                            allNewsViewModel.remove(index)
                                        },
                                        smashLikePost = {allNewsViewModel.smashPostLike(index)
                                                        recomposeTrigger++},
                                        smashLikeComment = {allNewsViewModel.smashCommentLike(index)},
                                        onCommentRemove = {allPosts[index].popularComment = null}
                                    )
                                }
                                if (endReachedAll && !isAllRefreshing) {
                                    allNewsViewModel.loadNextPage(search.value, context)
                                }
                            }
                        }
                    }
                }

            }

        }


    }

}

@Preview(showBackground = true)
@Composable
fun NewsPreview() {
    News(rememberNavController())
}