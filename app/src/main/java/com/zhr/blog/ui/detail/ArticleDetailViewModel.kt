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

    fun loadContent(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _content.value = try {
                repository.getArticleContent(url)
            } catch (_: Exception) {
                "<p>加载失败，请检查网络</p>"
            }
            _isLoading.value = false
        }
    }
}