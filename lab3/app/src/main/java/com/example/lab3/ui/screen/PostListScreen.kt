package com.example.lab3.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab3.data.model.Post
import com.example.lab3.ui.viewmodel.AuthViewModel
import com.example.lab3.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    postViewModel: PostViewModel,
    authViewModel: AuthViewModel,
    onPostClick: (Int) -> Unit,
    onCreatePostClick: () -> Unit,
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        postViewModel.fetchPosts()
        postViewModel.fetchCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blog") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            if (authViewModel.currentUser != null) {
                FloatingActionButton(onClick = onCreatePostClick) {
                    Icon(Icons.Default.Add, contentDescription = "Create Post")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    postViewModel.fetchPosts(it, selectedCategoryId)
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                placeholder = { Text("Search posts...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            // Categories Filter
            LazyRow(modifier = Modifier.padding(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { 
                            selectedCategoryId = null
                            postViewModel.fetchPosts(searchQuery, null)
                        },
                        label = { Text("All") },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                items(postViewModel.categories) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { 
                            selectedCategoryId = category.id
                            postViewModel.fetchPosts(searchQuery, category.id)
                        },
                        label = { Text(category.name) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            if (postViewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(postViewModel.posts) { post ->
                        PostItem(post = post, onClick = { onPostClick(post.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.title, style = MaterialTheme.typography.titleLarge)
            Text("By ${post.author_name ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.content.take(100) + "...", style = MaterialTheme.typography.bodyMedium)
            if (post.category_names != null) {
                Text("Categories: ${post.category_names}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
