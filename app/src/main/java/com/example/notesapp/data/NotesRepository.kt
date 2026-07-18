package com.example.notesapp.data

import android.content.Context
import android.content.SharedPreferences
import java.util.Date

class NotesRepository(private val context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
    
    companion object {
        const val KEY_NOTES = "notes"
        const val KEY_INTERVAL_TIME = "interval_time"
        
        fun createSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                "notes_prefs", 
                Context.MODE_PRIVATE
            )
        }
    }
    
    private var notesList: MutableList<Note> = mutableListOf()
    
    init {
        loadNotesFromPrefs()
    }
    
    fun saveNotes(notes: List<Note>) {
        val json = notes.toJson()
        prefs.edit().putString(KEY_NOTES, json).apply()
        notesList = notes.toMutableList()
    }
    
    fun getNotes(): List<Note> {
        return notesList
    }
    
    private fun loadNotesFromPrefs() {
        val json = prefs.getString(KEY_NOTES, null)
        if (!json.isNullOrEmpty()) {
            try {
                notesList = Note.fromJson(json).notes
            } catch (e: Exception) {
                e.printStackTrace()
                notesList = mutableListOf()
            }
        } else {
            notesList = mutableListOf()
        }
    }
    
    fun saveIntervalTime(minutes: Int) {
        prefs.edit().putInt(KEY_INTERVAL_TIME, minutes).apply()
    }
    
    fun getIntervalTime(): Int {
        return prefs.getInt(KEY_INTERVAL_TIME, 0)
    }
}

// JSON сериализация для Note
fun List<Note>.toJson(): String {
    val sb = StringBuilder("[")
    for (i in indices) {
        if (i > 0) sb.append(",")
        sb.append("{").append("\"id\": ").append(this[i].id).append(", ")
            .append("\"title\": \"").append(escapeString(this[i].title)).append("\", ")
            .append("\"content\": \"").append(escapeString(this[i].content)).append("\", ")
            .append("\"timestamp\": ").append(this[i].timestamp.time)
        if (i < size - 1) sb.append(",")
        sb.append("}")
    }
    sb.append("]")
    return sb.toString()
}

fun String.fromJson(): List<Note> {
    val notes = mutableListOf<Note>()
    try {
        val json = this.replace("[", "").replace("]", "")
            .replace("{", "").replace("}", "")
            .split(",")
        
        for (part in json) {
            if (part.contains("\"id\"")) {
                val id = part.split("\"id\": ")[1].split(",")[0].trim().toLong()
                val title = part.split("\"title\": \"")[1].split("\",")[0].trim()
                val content = part.split("\"content\": \"")[1].split("\",")[0].trim()
                val timestamp = part.split("\"timestamp\": ")[1].trim().toLong()
                
                notes.add(Note(
                    id = id,
                    title = title,
                    content = content,
                    timestamp = Date(timestamp)
                ))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return notes
}

fun escapeString(s: String): String {
    return s.replace("\\", "\\\\")
             .replace("\"", "\\\"")
             .replace("\n", "\\n")
             .replace("\r", "\\r")
             .replace("\t", "\\t")
}