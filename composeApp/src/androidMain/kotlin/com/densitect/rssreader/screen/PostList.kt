package com.densitect.rssreader.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.densitect.rssreader.core.entity.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostList(
    posts: List<Post>,
    listState: LazyListState,
    onPostClicked: (Post) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, state = listState, contentPadding = PaddingValues(16.dp)) {
        items(posts.size) { index ->
            PostItem(item = posts[index], onPostClicked = onPostClicked)
        }
    }
}

@Composable
fun PostItem(item: Post, onPostClicked: (Post) -> Unit) {
    val padding = 16.dp
    Box(modifier = Modifier.padding(bottom = 8.dp)) {
        Card(
            elevation = 16.dp,
            shape = RoundedCornerShape(padding)
        ) {
            Column(
                modifier = Modifier.clickable(onClick = {
                    onPostClicked.invoke(item)
                })
            ) {
                Spacer(modifier = Modifier.size(padding))
                Text(
                    modifier = Modifier.padding(start = padding, end = padding),
                    style = MaterialTheme.typography.h6,
                    text = item.title
                )
                item.imageUrl?.let { url ->
                    Spacer(modifier = Modifier.size(padding))
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth(),
                        contentDescription = null
                    )
                }
                item.desc?.let { desc ->
                    Spacer(modifier = Modifier.size(padding))
                    Text(
                        modifier = Modifier.padding(start = padding, end = padding),
                        style = MaterialTheme.typography.body1,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        text = desc
                    )
                }
                Spacer(modifier = Modifier.size(padding))
                Text(
                    modifier = Modifier.padding(start = padding, end = padding),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                    text = dateFormatter.format(Date(item.date))
                )
                Spacer(modifier = Modifier.size(padding))
            }
        }
    }
}

@SuppressLint("ConstantLocale")
private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())