package com.example.notesapp.data

import java.util.Date

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val timestamp: Date = Date(),
    val intervalTime: Int // время в минутах от 17:00 (0-90 минут)
) {
    companion object {
        private const val TABLE_NAME = "notes"
        
        fun createNote(
            title: String,
            content: String,
            intervalMinutes: Int
        ): Note {
            return Note(
                id = System.currentTimeMillis(),
                title = title.ifEmpty { "Новая заметка" },
                content = content.ifEmpty { "" },
                timestamp = Date(),
                intervalTime = (intervalMinutes % 90) // нормализация до диапазона 0-89 минут
            )
        }
    }
}

fun Long.toIntervalString(): String {
    val hours = this / 60
    val minutes = this % 60
    return if (hours == 17 && minutes == 0) "17:00" else 
           if (hours == 20) "20:00" else 
               "${hours}:${minutes.toString().padStart(2, '0')}"
}

fun Int.toIntervalString(): String {
    val hours = this / 60
    val minutes = this % 60
    return if (hours == 17 && minutes == 0) "17:00" else 
           if (hours == 20) "20:00" else 
               "${hours}:${minutes.toString().padStart(2, '0')}"
}

fun Date.toIntervalString(): String {
    val hours = this.time / 3600000
    val minutes = (this.time % 3600000) / 60000
    return if (hours == 17 && minutes == 0) "17:00" else 
           if (hours == 20) "20:00" else 
               "${hours}:${minutes.toString().padStart(2, '0')}"
}

fun Date.toReadableString(): String {
    val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(this)
}