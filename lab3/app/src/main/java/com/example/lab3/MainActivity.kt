package com.example.lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab3.ui.screen.*
import com.example.lab3.ui.theme.Lab3Theme
import com.example.lab3.ui.viewmodel.AuthViewModel
import com.example.lab3.ui.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.lab3.data.api.RetrofitClient.init(this)
        enableEdgeToEdge()
        setContent {
            Lab3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BlogApp(authViewModel, postViewModel)
                }
            }
        }
    }
}

@Composable
fun BlogApp(authViewModel: AuthViewModel, postViewModel: PostViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.currentUser == null) "login" else "post_list"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("post_list") { popUpTo("login") { inclusive = true } } }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = { navController.navigate("login") }
            )
        }
        composable("post_list") {
            PostListScreen(
                postViewModel = postViewModel,
                authViewModel = authViewModel,
                onPostClick = { postId -> navController.navigate("post_detail/$postId") },
                onCreatePostClick = { navController.navigate("create_post") },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo("post_list") { inclusive = true } }
                }
            )
        }
        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
            PostDetailScreen(
                postId = postId,
                postViewModel = postViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("create_post") {
            CreatePostScreen(
                viewModel = postViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}