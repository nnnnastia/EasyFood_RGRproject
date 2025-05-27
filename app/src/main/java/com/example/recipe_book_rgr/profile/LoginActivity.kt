package com.example.recipe_book_rgr.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe_book_rgr.MainActivity
import com.example.recipe_book_rgr.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // Ініціалізація FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Пошук елементів інтерфейсу
        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLoginNow)

        auth = FirebaseAuth.getInstance() // Ініціалізуємо Firebase

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Перевірка на порожні поля
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заповніть всі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Авторизація через Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            // Зберігаємо вхід та email в SharedPreferences
                            val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
                            prefs.edit()
                                .putBoolean("isLoggedIn", true)
                                .putString("loggedInEmail", email)
                                .apply()

                            Toast.makeText(this, "Вхід успішний", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Підтвердіть email перед входом",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Невірний email або пароль",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
