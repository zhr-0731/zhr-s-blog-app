package com.zhr.blog.ui.detail

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun ArticleDetailScreen(
    url: String,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val content by viewModel.content.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(url) {
        viewModel.loadContent(url)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("文章详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error ?: "加载失败",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadContent(url) }) {
                            Text("重试")
                        }
                    }
                }
                content.isNotEmpty() -> {
                    // 使用 AndroidView 加载 WebView，并捕获可能的异常
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        // 注入 viewport 优化移动端显示
                                        view?.loadUrl("javascript:(function() { " +
                                                "var meta = document.createElement('meta'); " +
                                                "meta.name = 'viewport'; " +
                                                "meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'; " +
                                                "document.head.appendChild(meta); })()")
                                    }
                                }
                            }
                        },
                        update = { webView ->
                            try {
                                webView.loadDataWithBaseURL(
                                    "https://zhr-0731.github.io/",
                                    content,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            } catch (e: Exception) {
                                viewModel.setError("加载文章内容失败: ${e.message}")
                            }
                        }
                    )
                }
            }
        }
    }
}