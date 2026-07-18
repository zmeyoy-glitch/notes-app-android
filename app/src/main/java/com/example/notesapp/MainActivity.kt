package com.example.notesapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "notes_prefs"
    private val KEY_SELECTED_TIME = "selected_time"
    private val KEY_NOTES = "notes_list"
    
    // Конфигурация времени
    private val START_HOUR = 17
    private val END_HOUR = 20
    private val STEP_MINUTES = 15
    
    // Форматирование времени
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Настройка окна на весь экран
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        
        setContentView(R.layout.activity_main)

        // Инициализация UI элементов
        initViews()
        
        // Загрузка сохраненных данных
        loadSavedData()
    }

    private fun initViews() {
        val tvTimeHint = findViewById<TextView>(R.id.tv_time_hint)
        val btnSelectTime = findViewById<Button>(R.id.btn_select_time)
        val tvNotesCount = findViewById<TextView>(R.id.tv_notes_count)
        
        // Генерация списка времени с шагом 15 минут от 17:00 до 20:00
        val timeOptions = generateTimeOptions()
        
        // Отображение количества доступных временных интервалов
        tvNotesCount.text = "Доступные интервалы: ${timeOptions.size}"
        tvTimeHint.text = "Выберите время начала интервала"

        btnSelectTime.setOnClickListener {
            showTimePickerDialog(timeOptions)
        }
    }

    private fun generateTimeOptions(): List<String> {
        val times = mutableListOf<String>()
        
        // Генерируем время с шагом 15 минут от 17:00 до 20:00
        var currentHour = START_HOUR
        while (currentHour < END_HOUR) {
            for (minute in 0..STEP_MINUTES until 60 step STEP_MINUTES) {
                val timeStr = String.format("%02d:%02d", currentHour, minute)
                times.add(timeStr)
            }
            
            // Переход к следующему часу
            if (currentHour < END_HOUR - 1) {
                currentHour++
            } else {
                break
            }
        }
        
        return times
    }

    private fun showTimePickerDialog(timeOptions: List<String>) {
        val selectedTime = prefs.getString(KEY_SELECTED_TIME, "") ?: timeOptions.first()
        
        // Простой диалог выбора времени (можно заменить на TimePicker)
        android.app.AlertDialog.Builder(this)
            .setTitle("Выберите время")
            .setSingleChoiceItems(timeOptions.toTypedArray(), -1) { _, position, _ ->
                val selected = timeOptions[position]
                prefs.edit().putString(KEY_SELECTED_TIME, selected).apply()
                
                // Сохраняем текущую заметку с выбранным временем
                saveCurrentNoteWithTime(selected)
            }
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun loadSavedData() {
        val notes = prefs.getString(KEY_NOTES, "[]") ?: "[]"
        
        // Здесь можно добавить логику загрузки заметок из SharedPreferences
        // и отображения их в UI (например, в RecyclerView)
    }

    private fun saveCurrentNoteWithTime(time: String) {
        // Сохраняем текущую заметку с выбранным временем
        val noteData = JSONObject()
        
        try {
            noteData.put("time", time)
            noteData.put("timestamp", System.currentTimeMillis())
            
            // Здесь можно добавить сохранение заголовка и контента заметки
            // если они уже были введены пользователем
            
            prefs.edit().putString(KEY_NOTES, noteData.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSavedData()
    }
}