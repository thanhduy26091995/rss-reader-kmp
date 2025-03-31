package com.densitect.rssreader

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.densitect.rssreader.app.FeedSideEffect
import com.densitect.rssreader.app.FeedStore
import com.densitect.rssreader.screen.MainScreen
import kotlinx.coroutines.flow.filterIsInstance
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DevelopmentEntryPoint {
                MaterialTheme {
                    val store: FeedStore by inject()
                    val scaffoldState = rememberScaffoldState()
                    val error = store.observerSideEffect()
                        .filterIsInstance<FeedSideEffect.Error>()
                        .collectAsState(null)

                    LaunchedEffect(error.value) {
                        error.value?.let { error ->
                            scaffoldState.snackbarHostState.showSnackbar(error.error.message.toString())
                        }
                    }

                    Scaffold(scaffoldState = scaffoldState, snackbarHost = { hostState ->
                        SnackbarHost(
                            hostState = hostState,
                            modifier = Modifier.padding(
                                WindowInsets.systemBars
                                    .only(WindowInsetsSides.Bottom)
                                    .asPaddingValues()
                            )
                        )
                    }) { paddingValues ->
                        MainScreen(onPostClicked = { post ->
                            post.link?.let { url ->
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                startActivity(intent)
                            }
                        }, onEditClicked = {

                        }, modifier = Modifier.padding(paddingValues))
                    }
                }
            }
        }
    }
}
