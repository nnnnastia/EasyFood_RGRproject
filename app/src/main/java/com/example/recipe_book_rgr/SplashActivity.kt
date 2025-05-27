package com.example.recipe_book_rgr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Підключаємо макет splash-екрану

        // Затримка в 2 секунди перед переходом на наступну активність
        Handler(Looper.getMainLooper()).postDelayed({

            // Отримуємо статус авторизації з SharedPreferences
            val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

            // Якщо користувач увійшов — переходимо на головний екран, інакше — на Welcome
            if (isLoggedIn) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }

            // Завершуємо splash-екран, щоб його не можна було повернути кнопкою "Назад"
            finish()

        }, 2000) // 2000 мс = 2 секунди затримки
    }
}
