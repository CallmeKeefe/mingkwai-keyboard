package com.example.mingkwai

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MingkwaiIME : InputMethodService() {
    private lateinit var db: DatabaseHelper
    private var selectedTop = ""
    private var selectedBottom = ""

    override fun onCreate() {
        super.onCreate()
        db = DatabaseHelper(applicationContext)
    }

    override fun onCreateInputView(): View {
        return object : AbstractComposeView(this) {
            @Composable
            override fun Content() {
                var candidatesList by remember { mutableStateOf(List(8) { "" }) }
                var guideText by remember { mutableStateOf("Tap Top-Left Element") }

                val refreshUI = {
                    if (selectedTop.isNotEmpty() && selectedBottom.isNotEmpty()) {
                        val results = db.findCharacters(selectedTop, selectedBottom)
                        candidatesList = List(8) { i -> results.getOrNull(i) ?: "" }
                        guideText = "Select Character (1-8)"
                    } else if (selectedTop.isNotEmpty()) {
                        guideText = "Selected: [$selectedTop] -> Choose Bottom-Right Element"
                        candidatesList = List(8) { "" }
                    } else {
                        guideText = "Tap Top-Left Element"
                        candidatesList = List(8) { "" }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().height(55.dp).background(Color(0xFF2B2B2B))) {
                        for (i in 0 until 8) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${i + 1}", color = Color.Gray, fontSize = 10.sp)
                                    Text(candidatesList[i], color = Color.Green, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    Text(guideText, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        (1..8).forEach { num ->
                            Box(modifier = Modifier.weight(1f).height(45.dp).background(Color(0xFF333333))
                                .clickable {
                                    val choice = candidatesList.getOrNull(num - 1) ?: ""
                                    if (choice.isNotEmpty()) {
                                        currentInputConnection?.commitText(choice, 1)
                                        db.saveToHistory(choice)
                                        selectedTop = ""; selectedBottom = ""
                                        refreshUI()
                                    }
                                }, contentAlignment = Alignment.Center) {
                                Text("$num", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val primaryRows = listOf(
                        listOf("丨", "亅", "丿", "丶", "口", "Reset"),
                        listOf("十", "亻", "氵", "宀", "⌫", "Space")
                    )

                    primaryRows.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            row.forEach { key ->
                                Box(modifier = Modifier.weight(1f).height(48.dp).background(Color(0xFF444444))
                                    .clickable {
                                        when (key) {
                                            "⌫" -> currentInputConnection?.deleteSurroundingText(1, 0)
                                            "Space" -> currentInputConnection?.commitText(" ", 1)
                                            "Reset" -> { selectedTop = ""; selectedBottom = ""; refreshUI() }
                                            else -> {
                                                if (selectedTop.isEmpty()) selectedTop = key
                                                else if (selectedBottom.isEmpty()) selectedBottom = key
                                                refreshUI()
                                            }
                                        }
                                    }, contentAlignment = Alignment.Center) {
                                    Text(key, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}