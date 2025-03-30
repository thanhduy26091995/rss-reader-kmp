package com.densitect.rssreader.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.densitect.rssreader.app.FeedAction
import com.densitect.rssreader.app.FeedStore
import com.densitect.rssreader.core.entity.Feed
import com.densitect.rssreader.core.entity.Post
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

@Composable
fun MainScreen(onPostClicked: (Post) -> Unit, modifier: Modifier = Modifier) {
    val store: FeedStore by inject(FeedStore::class.java)
    val state = store.observerState().collectAsState()
    val posts = remember(state.value.feeds, state.value.selectedFeed) {
        (state.value.selectedFeed?.posts
            ?: state.value.feeds.flatMap { it.posts }).sortedByDescending {
            it.date
        }
    }

    LaunchedEffect(true) {
        store.dispatch(FeedAction.Refresh(forceLoad = false))
    }

    Column(modifier = modifier) {
        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()

        PostList(
            posts = posts,
            listState = listState,
            onPostClicked = onPostClicked,
            modifier = Modifier.fillMaxWidth()
        )

        MainFeedBottomBar(
            feeds = state.value.feeds,
            selectedFeed = state.value.selectedFeed,
            onFeedClicked = { feed ->
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
                store.dispatch(FeedAction.SelectedFeed(feed))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private sealed class Icon {
    data object All : Icon()
    class FeedIcon(val feed: Feed) : Icon()
    data object Edit : Icon()
}

@Composable
fun MainFeedBottomBar(
    feeds: List<Feed>,
    selectedFeed: Feed?,
    onFeedClicked: (Feed) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = buildList {
        add(Icon.All)
        addAll(feeds.map { Icon.FeedIcon(it) })
        add(Icon.Edit)
    }

    LazyRow(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
        items(items) { item ->
            when (item) {
                Icon.All -> {
                    FeedIcon(feed = null, isSelected = selectedFeed == null) {

                    }
                }

                Icon.Edit -> {
                    FeedIcon(feed = null, isSelected = selectedFeed == null) {

                    }
                }

                is Icon.FeedIcon -> {
                    FeedIcon(feed = item.feed, isSelected = selectedFeed == item.feed) {
                        onFeedClicked(item.feed)
                    }
                }
            }
        }
    }
}