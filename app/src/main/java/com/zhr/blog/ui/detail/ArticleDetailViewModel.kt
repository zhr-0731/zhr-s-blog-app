package com.zhr.blog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhr.blog.data.repository.BlogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val repository: BlogRepository
) : ViewModel() {

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 详细错误日志（含堆栈）
    private val _errorDetail = MutableStateFlow<String?>(null)
    val errorDetail: StateFlow<String?> = _errorDetail

    fun loadContent(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _errorDetail.value = null
            try {
                val html = repository.getArticleContent(url)
                if (html.isBlank()) {
                    _error.value = "文章内容为空"
                    _errorDetail.value = "服务器返回的 HTML 内容为空，请检查网络或文章链接。"
                } else {
                    _content.value = html
                }
            } catch (e: Exception) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                val stackTrace = sw.toString()
                _error.value = e.message ?: "加载失败，请检查网络"
                _errorDetail.value = "异常类型: ${e.javaClass.simpleName}\n消息: ${e.message}\n堆栈:\n$stackTrace"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 供 AndroidView 内部异常调用
    fun setError(msg: String, detail: String? = null) {
        _error.value = msg
        _errorDetail.value = detail ?: msg
        _isLoading.value = false
    }
}