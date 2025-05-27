package com.example.recipe_book_rgr.recipe

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.recipe_book_rgr.R
import java.io.File
import java.io.FileOutputStream

class AddEditRecipeActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private val recipeTypes = listOf("Сніданок", "Обід", "Вечеря", "Закуска", "Десерт")

    private var selectedImageUri: Uri? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_recipe)

        // Ініціалізація UI-компонентів
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etIngredients = findViewById<EditText>(R.id.etIngredients)
        val etInstructions = findViewById<EditText>(R.id.etInstructions)
        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val etTime = findViewById<EditText>(R.id.etTime)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val ivPhoto = findViewById<ImageView>(R.id.ivPhoto)
        val btnChooseImage = findViewById<Button>(R.id.btnChooseImage)

        // ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Адаптер для типів страв
        spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            recipeTypes
        )

        // Отримання параметрів редагування
        val recipeId = intent.getIntExtra("recipe_id", -1)
        val isEditMode = recipeId != -1

        // Якщо редагування, заповнюємо поля
        if (isEditMode) {
            etTitle.setText(intent.getStringExtra("title"))
            etIngredients.setText(intent.getStringExtra("ingredients"))
            etInstructions.setText(intent.getStringExtra("instructions"))
            etTime.setText(intent.getStringExtra("time"))

            // Обираємо тип страви у Spinner
            val selectedType = intent.getStringExtra("type")
            val index = recipeTypes.indexOf(selectedType)
            if (index >= 0) spinnerType.setSelection(index)

            // Встановлюємо зображення, якщо є
            val imageUriStr = intent.getStringExtra("imageUri")
            if (!imageUriStr.isNullOrEmpty()) {
                val file = File(imageUriStr)
                if (file.exists()) {
                    imagePath = imageUriStr
                    Glide.with(this)
                        .load(file)
                        .centerCrop()
                        .into(ivPhoto)
                }
            }
        }

        // Вибір зображення
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                ivPhoto.setImageURI(it)
            }
        }

        // Обробка кнопки вибору зображення
        btnChooseImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Збереження рецепту
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val ingredients = etIngredients.text.toString().trim()
            val instructions = etInstructions.text.toString().trim()
            val type = spinnerType.selectedItem?.toString() ?: ""
            val time = etTime.text.toString().trim()

            // Перевірка заповнення всіх полів
            if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty() || type.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Будь ласка, заповніть усі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Збереження зображення (якщо вибрано нове)
            if (selectedImageUri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                    val file = File(filesDir, "recipe_${System.currentTimeMillis()}.jpg")
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    imagePath = file.absolutePath
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Помилка при збереженні зображення", Toast.LENGTH_SHORT).show()
                }
            }

            // Отримання пошти поточного користувача
            val loggedInEmail = getSharedPreferences("UserData", MODE_PRIVATE)
                .getString("loggedInEmail", "") ?: ""

            // Створення об'єкта Recipe
            val recipe = Recipe(
                id = if (isEditMode) recipeId else 0,
                title = title,
                ingredients = ingredients,
                instructions = instructions,
                type = type,
                time = time,
                imageUri = imagePath,
                ownerEmail = loggedInEmail
            )

            // Додавання або оновлення в БД
            if (isEditMode) {
                viewModel.update(recipe)
                Toast.makeText(this, "Рецепт оновлено", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insert(recipe)
                Toast.makeText(this, "Рецепт збережено", Toast.LENGTH_SHORT).show()
            }

            finish() // Закриваємо екран після збереження
        }
    }
}
