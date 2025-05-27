package com.example.recipe_book_rgr.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe_book_rgr.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    // FirebaseAuth instance
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Пошук елементів інтерфейсу
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Обробка натискання кнопки "Зареєструватися"
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirm.text.toString()

            // Перевірка введених даних
            when {
                name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
                }

                password != confirmPassword -> {
                    Toast.makeText(this, "Паролі не збігаються", Toast.LENGTH_SHORT).show()
                }

                password.length < 6 -> {
                    Toast.makeText(this, "Пароль має містити щонайменше 6 символів", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // Реєстрація користувача через Firebase
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Надсилання email для підтвердження
                                firebaseAuth.currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener { verifyTask ->
                                        if (verifyTask.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Перевірте пошту для підтвердження",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            // Локальне збереження імені та паролю (опціонально)
                                            val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
                                            prefs.edit().apply {
                                                putString("user_${email}_name", name)
                                                putString("user_${email}_password", password)
                                                putString("loggedInEmail", email)
                                                apply()
                                            }

                                            // Перехід до екрана входу
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Помилка надсилання підтвердження: ${verifyTask.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                // Обробка помилки реєстрації
                                Toast.makeText(
                                    this,
                                    "Помилка реєстрації: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }
}
