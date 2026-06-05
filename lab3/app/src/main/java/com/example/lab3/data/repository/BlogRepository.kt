package com.example.lab3.data.repository

import com.example.lab3.data.api.BlogApi
import com.example.lab3.data.model.*
import retrofit2.Response

class BlogRepository(private val api: BlogApi) {
    suspend fun register(request: RegisterRequest) = api.register(request)
    suspend fun login(request: LoginRequest) = api.login(request)
    suspend fun getPosts(search: String? = null, categoryId: Int? = null) = api.getPosts(search, categoryId)
    suspend fun getPost(id: Int) = api.getPost(id)
    suspend fun createPost(request: CreatePostRequest) = api.createPost(request)
    suspend fun createComment(postId: Int, request: CreateCommentRequest) = api.createComment(postId, request)
    suspend fun getCategories() = api.getCategories()
}
