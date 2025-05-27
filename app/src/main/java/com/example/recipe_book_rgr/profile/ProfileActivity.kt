package com.example.recipe_book_rgr.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipe_book_rgr.R
import com.example.recipe_book_rgr.WelcomeActivity
import com.example.recipe_book_rgr.recipe.RecipeViewModel
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var imgAvatar: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvRecipeCount: TextView
    private lateinit var tvSavedCount: TextView

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var currentEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Ініціалізація елементів інтерфейсу
        imgAvatar = findViewById(R.id.imgAvatar)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvRecipeCount = findViewById(R.id.tvAddedCount)
        tvSavedCount = findViewById(R.id.tvSavedCount)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val btnChangeAvatar = findViewById<ImageView>(R.id.btnChangeAvatar)

        // Отримання поточного email користувача
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        currentEmail = prefs.getString("loggedInEmail", null) ?: ""

        // Встановлення імені та email у профілі
        tvUserName.text = prefs.getString("user_${currentEmail}_name", "Користувач")
        tvUserEmail.text = currentEmail

        // Завантаження аватара з внутрішнього сховища
        loadAvatar()

        // Ініціалізація ViewModel
        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Підрахунок доданих рецептів
        recipeViewModel.getAllRecipes(currentEmail).observe(this) { recipes ->
            tvRecipeCount.text = recipes.size.toString()
        }

        // Підрахунок збережених рецептів
        recipeViewModel.getSavedRecipes(currentEmail).observe(this) { saved ->
            tvSavedCount.text = saved.size.toString()
        }

        // Кнопка виходу з акаунту
        btnLogout.setOnClickListener {
            prefs.edit()
                .putBoolean("isLoggedIn", false)
                .remove("loggedInEmail")
                .apply()

            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        // Перехід до редагування профілю
        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Вибір нової аватарки
        btnChangeAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Реєстрація обробника вибору зображення
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) saveImageToInternalStorage(uri)
        }

        // Перехід до списку доданих рецептів
        findViewById<LinearLayout>(R.id.btnMyAdded).setOnClickListener {
            val intent = Intent(this, MyRecipesActivity::class.java)
            intent.putExtra("type", "added")
            startActivity(intent)
        }

        // Перехід до списку збережених рецептів
        findViewById<LinearLayout>(R.id.btnMySaved).setOnClickListener {
            val intent = Intent(this, MyRecipesActivity::class.java)
            intent.putExtra("type", "saved")
            startActivity(intent)
        }
    }

    /**
     * Завантаження аватарки з локального файлу
     */
    private fun loadAvatar() {
        val avatarPath = getSharedPreferences("UserData", MODE_PRIVATE)
            .getString("user_${currentEmail}_avatarUri", null)

        if (!avatarPath.isNullOrEmpty()) {
            val file = File(avatarPath)
            if (file.exists()) {
                Glide.with(this)
                    .load(file)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgAvatar)
            }
        }
    }

    /**
     * Збереження вибраного зображення у внутрішнє сховище та оновлення аватарки
     */
    private fun saveImageToInternalStorage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, "avatar_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            // Збереження шляху до файлу в SharedPreferences
            getSharedPreferences("UserData", MODE_PRIVATE).edit()
                .putString("user_${currentEmail}_avatarUri", file.absolutePath)
                .apply()

            // Встановлення нового зображення
            Glide.with(this)
                .load(file)
                .apply(RequestOptions.circleCropTransform())
                .into(imgAvatar)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Помилка при збереженні аватара", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Повторне завантаження даних при поверненні до активності
     */
    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        currentEmail = prefs.getString("loggedInEmail", "") ?: ""

        tvUserName.text = prefs.getString("user_${currentEmail}_name", "Ім’я")
        tvUserEmail.text = currentEmail

        loadAvatar()

        // Оновлення кількості збережених рецептів
        recipeViewModel.getSavedRecipes(currentEmail).observe(this) { saved ->
            tvSavedCount.text = saved.size.toString()
        }
    }
}
