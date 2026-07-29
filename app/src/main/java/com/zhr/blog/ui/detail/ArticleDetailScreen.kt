package com.zhr.blog.ui.detail

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.PrintWriter
import java.io.StringWriter

@Composable
fun ArticleDetailScreen(
    url: String,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val content by viewModel.content.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val errorDetail by viewModel.errorDetail.collectAsState()

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
                    // 显示错误信息和详细日志
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "❌ 加载失败",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "未知错误",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (errorDetail != null) {
                            Text(
                                text = "📋 详细日志：",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = errorDetail ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    maxLines = 20,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadContent(url) }) {
                            Text("重试")
                        }
                    }
                }
                content.isNotEmpty() -> {
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
                                val sw = StringWriter()
                                val pw = PrintWriter(sw)
                                e.printStackTrace(pw)
                                viewModel.setError(
                                    "WebView 加载异常: ${e.message}",
                                    "异常类型: ${e.javaClass.simpleName}\n消息: ${e.message}\n堆栈:\n${sw.toString()}"
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}