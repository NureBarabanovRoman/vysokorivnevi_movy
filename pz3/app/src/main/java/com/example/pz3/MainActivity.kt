package com.example.pz3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pz3.ui.theme.Pz3Theme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Calculator : Screen("calculator", "Calc", Icons.Default.Build)
    object Movies : Screen("movies", "Movies", Icons.Default.List)
    object Fitness : Screen("fitness", "Fitness", Icons.Default.Star)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pz3Theme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Calculator) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val items = listOf(Screen.Calculator, Screen.Movies, Screen.Fitness)
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentScreen == screen,
                                    onClick = { currentScreen = screen }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.Calculator -> CalculatorScreen()
                            Screen.Movies -> MovieScreen()
                            Screen.Fitness -> FitnessScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Calculator", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = num1,
            onValueChange = { num1 = it },
            label = { Text("Number 1") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = num2,
            onValueChange = { num2 = it },
            label = { Text("Number 2") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf("+", "-", "*", "/").forEach { op ->
                Button(
                    onClick = { result = calculate(num1, num2, op) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(op)
                }
            }
        }

        if (result.isNotEmpty()) {
            Text(text = "Result: $result", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

fun calculate(n1: String, n2: String, op: String): String {
    return try {
        val val1 = n1.toDoubleOrNull() ?: return "Error"
        val val2 = n2.toDoubleOrNull() ?: return "Error"

        val res = when (op) {
            "+" -> val1 + val2
            "-" -> val1 - val2
            "*" -> val1 * val2
            "/" -> if (val2 != 0.0) val1 / val2 else return "Div by 0"
            else -> return "Error"
        }

        if (res % 1.0 == 0.0) res.toInt().toString() else res.toString()
    } catch (e: Exception) {
        "Error"
    }
}
