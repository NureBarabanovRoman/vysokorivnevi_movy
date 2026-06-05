package com.example.lab3.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String
)

@Serializable
data class Category(
    val id: Int,
    val name: String
)

@Serializable
data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val author_id: Int,
    val author_name: String? = null,
    val category_names: String? = null,
    val created_at: String,
    val comments: List<Comment>? = null,
    val categories: List<Category>? = null
)

@Serializable
data class Comment(
    val id: Int,
    val post_id: Int,
    val author_id: Int,
    val author_name: String? = null,
    val content: String,
    val created_at: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val categoryIds: List<Int>? = null
)

@Serializable
data class CreateCommentRequest(
    val content: String
)
