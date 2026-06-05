package com.example.lab3.data.api

import com.example.lab3.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface BlogApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("posts")
    suspend fun getPosts(
        @Query("search") search: String? = null,
        @Query("category") categoryId: Int? = null
    ): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Response<Post>

    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<Post>

    @POST("posts/{id}/comments")
    suspend fun createComment(
        @Path("id") postId: Int,
        @Body request: CreateCommentRequest
    ): Response<Comment>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>
}
