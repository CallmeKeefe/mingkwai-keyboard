package com.example.mingkwai

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "mingkwai_local.db"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE dictionary (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                top_left TEXT,
                bottom_right TEXT,
                character TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE typing_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                phrase TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """)
        seedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {}

    private fun seedData(db: SQLiteDatabase) {
        val samplePairs = listOf(
            Triple("口", "亅", "呼"), Triple("口", "亅", "鸣"), Triple("口", "亅", "咙"),
            Triple("亻", "十", "什"), Triple("氵", "宀", "演"), Triple("十", "口", "古")
        )
        samplePairs.forEach { (top, bottom, char) ->
            db.execSQL("INSERT INTO dictionary (top_left, bottom_right, character) VALUES ('$top', '$bottom', '$char')")
        }
    }

    fun findCharacters(top: String, bottom: String): List<String> {
        val list = mutableListOf<String>()
        val cursor = readableDatabase.rawQuery(
            "SELECT character FROM dictionary WHERE top_left = ? AND bottom_right = ? LIMIT 8",
            arrayOf(top, bottom)
        )
        while (cursor.moveToNext()) { list.add(cursor.getString(0)) }
        cursor.close()
        return list
    }

    fun saveToHistory(text: String) {
        val values = ContentValues().apply { put("phrase", text) }
        writableDatabase.insert("typing_history", null, values)
    }

    fun fetchHistory(): List<String> {
        val list = mutableListOf<String>()
        val cursor = readableDatabase.rawQuery("SELECT phrase FROM typing_history ORDER BY id DESC", null)
        while (cursor.moveToNext()) { list.add(cursor.getString(0)) }
        cursor.close()
        return list
    }
}