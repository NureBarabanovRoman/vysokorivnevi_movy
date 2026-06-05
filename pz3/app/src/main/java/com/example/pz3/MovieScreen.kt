package com.example.pz3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class Movie(val name: String, val genre: String, val rating: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(modifier: Modifier = Modifier) {
    var movies by remember { mutableStateOf(listOf(
        Movie("Inception", "Sci-Fi", "8.8"),
        Movie("The Shawshank Redemption", "Drama", "9.3"),
        Movie("The Dark Knight", "Action", "9.0")
    )) }
    
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Movie List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Movie")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies) { movie ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = movie.name, style = MaterialTheme.typography.headlineSmall)
                        Text(text = "Genre: ${movie.genre}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Rating: ${movie.rating}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add New Movie") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") }
                        )
                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Genre") }
                        )
                        OutlinedTextField(
                            value = rating,
                            onValueChange = { input ->
                                val doubleValue = input.toDoubleOrNull()
                                if (input.isEmpty() || 
                                    (doubleValue != null && doubleValue in 0.0..10.0) ||
                                    (input.endsWith(".") && input.count { it == '.' } == 1 && (input.dropLast(1).toDoubleOrNull() ?: 0.0) in 0.0..10.0)) {
                                    rating = input
                                }
                            },
                            label = { Text("Rating (0.0 - 10.0)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (name.isNotEmpty()) {
                            movies = movies + Movie(name, genre, rating)
                            name = ""
                            genre = ""
                            rating = ""
                            showDialog = false
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
