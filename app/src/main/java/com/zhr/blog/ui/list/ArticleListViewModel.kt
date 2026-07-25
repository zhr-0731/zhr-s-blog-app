package com.zhr.blog.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhr.blog.data.model.Article
import com.zhr.blog.data.repository.BlogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val repository: BlogRepository
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _articles.value = repository.fetchArticles()
            } catch (e: Exception) {
                _error.value = e.message ?: "加载失败"
                repository.getArticles().collect { cached ->
                    if (cached.isNotEmpty()) _articles.value = cached
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() = loadArticles()
}