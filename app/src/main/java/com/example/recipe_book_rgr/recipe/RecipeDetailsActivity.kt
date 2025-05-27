package com.example.recipe_book_rgr.recipe

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.recipe_book_rgr.R
import java.io.File

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var btnSaved: ImageView
    private var currentRecipe: Recipe? = null
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Ініціалізація елементів інтерфейсу
        val ivImage = findViewById<ImageView>(R.id.ivDetailImage)
        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvType = findViewById<TextView>(R.id.tvDetailType)
        val tvIngredients = findViewById<TextView>(R.id.tvDetailIngredients)
        val tvInstructions = findViewById<TextView>(R.id.tvDetailInstructions)
        val tvTime = findViewById<TextView>(R.id.tvPreparingTime)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        btnSaved = findViewById(R.id.ivSaved)

        // Отримання ID рецепта з Intent
        recipeId = intent.getIntExtra("recipe_id", -1)
        if (recipeId == -1) {
            Toast.makeText(this, "Рецепт не знайдено", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Отримання email користувача
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val email = prefs.getString("loggedInEmail", "") ?: ""

        // Ініціалізація ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Спостереження за списком рецептів і пошук потрібного за ID
        viewModel.getAllRecipes(email).observe(this) { recipes ->
            currentRecipe = recipes.find { it.id == recipeId }

            currentRecipe?.let { recipe ->
                // Заповнення полів інформацією
                tvTitle.text = recipe.title
                tvType.text = "Тип: ${recipe.type}"
                tvIngredients.text = "Готуємо: ${recipe.ingredients}"
                tvInstructions.text = recipe.instructions
                tvTime.text = "Час приготування: ${recipe.time}"

                // Відображення зображення (якщо є)
                loadImage(recipe.imageUri, ivImage)

                // Встановлення іконки збереження
                updateHeartIcon(recipe.isSaved)

                // Кнопка редагування рецепта
                btnEdit.setOnClickListener {
                    val intent = Intent(this, AddEditRecipeActivity::class.java).apply {
                        putExtra("recipe_id", recipe.id)
                        putExtra("title", recipe.title)
                        putExtra("ingredients", recipe.ingredients)
                        putExtra("instructions", recipe.instructions)
                        putExtra("type", recipe.type)
                        putExtra("time", recipe.time)
                        putExtra("imageUri", recipe.imageUri)
                    }
                    startActivity(intent)
                    finish()
                }

                // Кнопка видалення рецепта
                btnDelete.setOnClickListener {
                    viewModel.delete(recipe)
                    Toast.makeText(this, "Рецепт видалено", Toast.LENGTH_SHORT).show()
                    finish()
                }

                // Кнопка "Зберегти"/"Скасувати збереження"
                btnSaved.setOnClickListener {
                    val updatedRecipe = recipe.copy(isSaved = !recipe.isSaved)
                    viewModel.update(updatedRecipe)
                    updateHeartIcon(updatedRecipe.isSaved)
                    Toast.makeText(
                        this,
                        if (updatedRecipe.isSaved) "Додано до збережених" else "Видалено зі збережених",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Завантаження зображення з файлу або встановлення зображення за замовчуванням
     */
    private fun loadImage(imageUri: String?, imageView: ImageView) {
        if (!imageUri.isNullOrEmpty()) {
            val file = File(imageUri)
            if (file.exists()) {
                Glide.with(this)
                    .load(file)
                    .centerCrop()
                    .transform(RoundedCorners(32))
                    .into(imageView)
                return
            }
        }
        imageView.setImageResource(R.drawable.sample_food)
    }

    /**
     * Встановлення іконки збереження (сердечко)
     */
    private fun updateHeartIcon(isSaved: Boolean) {
        btnSaved.setImageResource(
            if (isSaved) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        )
    }
}
