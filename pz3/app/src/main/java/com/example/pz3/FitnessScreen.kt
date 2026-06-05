package com.example.pz3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class Workout(val activity: String, val duration: String, val date: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessScreen(modifier: Modifier = Modifier) {
    var workouts by remember { mutableStateOf(listOf(
        Workout("Running", "30", "2026-05-18"),
        Workout("Cycling", "45", "2026-05-17")
    )) }

    var showDialog by remember { mutableStateOf(false) }
    var activity by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Fitness Tracker") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Workout")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Statistics", style = MaterialTheme.typography.titleLarge)
            Text(text = "Total workouts: ${workouts.size}", style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Recent Activities", style = MaterialTheme.typography.titleLarge)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(workouts) { workout ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = workout.activity, style = MaterialTheme.typography.headlineSmall)
                            Text(text = "Duration: ${workout.duration} mins", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Date: ${workout.date}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Record Workout") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = activity,
                            onValueChange = { activity = it },
                            label = { Text("Activity (e.g. Running)") }
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { 
                                if (it.all { char -> char.isDigit() }) duration = it 
                            },
                            label = { Text("Duration (minutes)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = date,
                            onValueChange = { },
                            label = { Text("Date") },
                            readOnly = true,
                            modifier = Modifier.clickable { showDatePicker = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (activity.isNotEmpty()) {
                            workouts = workouts + Workout(activity, duration, date)
                            activity = ""
                            duration = ""
                            showDialog = false
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            date = sdf.format(Date(millis))
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
