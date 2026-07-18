package com.example.notesapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import java.util.Date

class MainActivity : AppCompatActivity() {
    
    private lateinit var repository: NotesRepository
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSave: Button
    private lateinit var tvTime: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        repository = NotesRepository(this)
        
        initViews()
        updateIntervalDisplay()
    }
    
    private fun initViews() {
        etTitle = findViewById(R.id.et_title)
        etContent = findViewById(R.id.et_content)
        btnSave = findViewById(R.id.btn_save)
        tvTime = findViewById(R.id.tv_time)
        
        btnSave.setOnClickListener {
            saveNote()
        }
    }
    
    private fun updateIntervalDisplay() {
        val intervalMinutes = repository.getIntervalTime()
        if (intervalMinutes > 0) {
            val timeString = intervalMinutes.toIntervalString()
            tvTime.text = "Текущий интервал: $timeString"
        } else {
            tvTime.text = "Выберите время начала заметки"
        }
    }
    
    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Введите заголовок", Toast.LENGTH_SHORT).show()
            return
        }
        
        val intervalMinutes = repository.getIntervalTime()
        if (intervalMinutes <= 0) {
            Toast.makeText(this, "Сначала выберите время интервала", Toast.LENGTH_SHORT).show()
            return
        }
        
        val note = Note.createNote(title, content, intervalMinutes)
        
        // Сохраняем заметку в репозиторий
        val currentNotes = repository.getNotes()
        val updatedNotes = currentNotes + note
        repository.saveNotes(updatedNotes)
        
        Toast.makeText(this, "Заметка сохранена!", Toast.LENGTH_SHORT).show()
        
        etTitle.setText("")
        etContent.setText("")
    }
    
    private fun getAllNotes(): List<Note> {
        return repository.getNotes()
    }
}

// Расширения для работы с интервалами времени
fun Int.toIntervalString(): String {
    val hours = this / 60
    val minutes = this % 60
    
    // Нормализация к диапазону 17:00 - 20:00 (90 минут)
    val normalizedMinutes = this % 90
    
    return when {
        normalizedMinutes == 0 -> "17:00"
        normalizedMinutes < 60 -> "${hours}:${minutes.toString().padStart(2, '0')}"
        else -> "${hours + 1}:${(normalizedMinutes - 60).toString().padStart(2, '0')}"
    }
}

fun Int.toIntervalStringDetailed(): String {
    val hours = this / 60
    val minutes = this % 60
    
    // Диапазон 17:00-20:00 с шагом 15 минут (90 интервалов)
    return when {
        this == 0 -> "17:00"
        this in 1..45 -> "${hours}:${minutes.toString().padStart(2, '0')}"
        else -> "${hours + 1}:${(this - 60).toString().padStart(2, '0')}"
    }
}

fun Int.getIntervalDescription(): String {
    val hours = this / 60
    val minutes = this % 60
    
    return when (this) {
        0 -> "17:00"
        in 1..45 -> "${hours}:${minutes.toString().padStart(2, '0')}"
        else -> "${hours + 1}:${(this - 60).toString().padStart(2, '0')}"
    }
}

fun Int.isValidInterval(): Boolean {
    return this in 0..89 // 90 интервалов от 17:00 до 20:00 включительно
}