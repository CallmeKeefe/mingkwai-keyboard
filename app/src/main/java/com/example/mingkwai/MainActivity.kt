package com.example.mingkwai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        setContent {
            var inputHistory by remember { mutableStateOf(listOf<String>()) }

            LaunchedEffect(Unit) {
                inputHistory = db.fetchHistory()
            }

            MaterialTheme {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Mingkwai Configuration", fontSize = 22.sp, modifier = Modifier.padding(bottom = 16.dp))

                    Button(onClick = {
                        startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                    }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("1. Turn On Typewriter Engine")
                    }

                    Button(onClick = {
                        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.showInputMethodPicker()
                    }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Text("2. Immediately Set As Active Keyboard")
                    }

                    Text("Live Typing History Log Archive:", fontSize = 14.sp, color = Color.DarkGray)
                    
                    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFEEEEEE)).padding(4.dp)) {
                        items(inputHistory) { entry ->
                            Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                Text("Committed Word: $entry", modifier = Modifier.padding(12.dp), fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}