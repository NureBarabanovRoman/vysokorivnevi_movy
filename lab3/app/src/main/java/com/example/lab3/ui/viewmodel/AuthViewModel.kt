package com.example.lab3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab3.data.api.RetrofitClient
import com.example.lab3.data.model.AuthResponse
import com.example.lab3.data.model.LoginRequest
import com.example.lab3.data.model.RegisterRequest
import com.example.lab3.data.model.User
import com.example.lab3.data.repository.BlogRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = BlogRepository(RetrofitClient.blogApi)

    var currentUser by mutableStateOf<User?>(null)
        private set

    var token by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(request: LoginRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.login(request)
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    currentUser = authResponse?.user
                    token = authResponse?.token
                    RetrofitClient.setToken(token)
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = "Login failed: ${parseErrorMessage(errorBody) ?: response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(request: RegisterRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.register(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = "Registration failed: ${parseErrorMessage(errorBody) ?: response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody == null) return null
        return try {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val element = json.parseToJsonElement(errorBody)
            val jsonObject = element as? kotlinx.serialization.json.JsonObject
            val messageElement = jsonObject?.get("message") as? kotlinx.serialization.json.JsonPrimitive
            messageElement?.content
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        currentUser = null
        token = null
        RetrofitClient.setToken(null)
    }
}
