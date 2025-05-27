package com.example.recipe_book_rgr.profile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe_book_rgr.R

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Пошук елементів інтерфейсу
        val etName = findViewById<EditText>(R.id.etEditName)
        val etEmail = findViewById<EditText>(R.id.etEditEmail)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)

        // Отримуємо доступ до SharedPreferences
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val loggedInEmail = prefs.getString("loggedInEmail", null)

        // Якщо користувач не авторизований — завершуємо активність
        if (loggedInEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Користувач не авторизований", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Завантаження поточного імені збереженого для користувача
        val currentName = prefs.getString("user_${loggedInEmail}_name", "")
        etName.setText(currentName)

        // Встановлюємо email в поле, але не дозволяємо редагувати
        etEmail.setText(loggedInEmail)
        etEmail.isEnabled = false

        // Обробка натискання на кнопку "Зберегти"
        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()

            // Перевірка на порожнє ім’я
            if (newName.isEmpty()) {
                Toast.makeText(this, "Ім’я не може бути порожнім", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Збереження нового імені в SharedPreferences
            prefs.edit()
                .putString("user_${loggedInEmail}_name", newName)
                .apply()

            Toast.makeText(this, "Профіль оновлено", Toast.LENGTH_SHORT).show()
            finish() // Повертаємось назад
        }
    }
}
