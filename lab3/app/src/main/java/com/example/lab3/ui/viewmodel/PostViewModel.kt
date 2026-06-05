package com.example.lab3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab3.data.api.RetrofitClient
import com.example.lab3.data.model.*
import com.example.lab3.data.repository.BlogRepository
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val repository = BlogRepository(RetrofitClient.blogApi)

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var selectedPost by mutableStateOf<Post?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchPosts(search: String? = null, categoryId: Int? = null) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.getPosts(search, categoryId)
                if (response.isSuccessful) {
                    posts = response.body() ?: emptyList()
                } else {
                    errorMessage = "Failed to fetch posts: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                if (response.isSuccessful) {
                    categories = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Ignore category fetch errors for now
            }
        }
    }

    fun fetchPost(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.getPost(id)
                if (response.isSuccessful) {
                    selectedPost = response.body()
                } else {
                    errorMessage = "Failed to fetch post: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createPost(title: String, content: String, categoryIds: List<Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.createPost(CreatePostRequest(title, content, categoryIds))
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = "Failed to create post: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addComment(postId: Int, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            errorMessage = null
            try {
                val response = repository.createComment(postId, CreateCommentRequest(content))
                if (response.isSuccessful) {
                    fetchPost(postId) // Refresh post to show new comment
                    onSuccess()
                } else {
                    errorMessage = "Failed to add comment: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }
}
