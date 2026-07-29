package com.zhr.blog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhr.blog.data.repository.BlogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    fun loadContent(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val html = repository.getArticleContent(url)
                if (html.isBlank()) {
                    _error.value = "文章内容为空"
                } else {
                    _content.value = html
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "加载失败，请检查网络"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 供 AndroidView 内部异常调用
    fun setError(msg: String) {
        _error.value = msg
        _isLoading.value = false
    }
}