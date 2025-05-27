package com.example.recipe_book_rgr

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipe_book_rgr.profile.ProfileActivity
import com.example.recipe_book_rgr.recipe.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MainActivity : ComponentActivity() {

    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    private lateinit var spinnerFilter: Spinner
    private val recipeTypes = listOf("Усі", "Сніданок", "Обід", "Вечеря", "Закуска", "Десерт")
    private var currentLiveData: LiveData<List<Recipe>>? = null
    private var loggedInEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Отримання поточного акаунту
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        loggedInEmail = prefs.getString("loggedInEmail", "") ?: ""

        setupAvatar(prefs)

        // Ініціалізація основних елементів
        spinnerFilter = findViewById(R.id.spinnerFilter)
        val recyclerView = findViewById<RecyclerView>(R.id.rvRecipes)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val menuIcon = findViewById<ImageView>(R.id.ivMenu)

        // Налаштування адаптера рецептів
        recipeAdapter = RecipeAdapter(
            mutableListOf(),
            onItemClick = { recipe ->
                openRecipeDetails(recipe)
            },
            onSavedClick = { recipe ->
                recipeViewModel.update(recipe)
                Toast.makeText(
                    this,
                    if (recipe.isSaved) "Додано в збережені" else "Видалено з збережених",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = recipeAdapter

        // Початкове завантаження всіх рецептів
        observeRecipes(recipeViewModel.getAllRecipes(loggedInEmail))

        // Обробка вибору типу страви зі спінера
        spinnerFilter.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, recipeTypes)
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = recipeTypes[position]
                val newData: LiveData<List<Recipe>> = if (selected == "Усі") {
                    recipeViewModel.getAllRecipes(loggedInEmail)
                } else {
                    recipeViewModel.getRecipesByType(selected, loggedInEmail)
                }
                observeRecipes(newData)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Додати новий рецепт
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditRecipeActivity::class.java))
        }

        // Тимчасове повідомлення про меню
        menuIcon.setOnClickListener {
            Toast.makeText(this, "Меню ще в розробці", Toast.LENGTH_SHORT).show()
        }

        // Перехід до профілю
        findViewById<ImageView>(R.id.imgMiniAvatar).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    /**
     * Завантаження аватарки при відновленні активності
     */
    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        setupAvatar(prefs)
    }

    /**
     * Оновлення LiveData спостерігача
     */
    private fun observeRecipes(data: LiveData<List<Recipe>>) {
        currentLiveData?.removeObservers(this)
        currentLiveData = data
        currentLiveData?.observe(this) {
            recipeAdapter.updateRecipes(it)
        }

    }

    /**
     * Відображення аватарки користувача у міні-профілі
     */
    private fun setupAvatar(prefs: SharedPreferences) {
        val avatarPath = prefs.getString("user_${loggedInEmail}_avatarUri", null)
        val avatarView = findViewById<ImageView>(R.id.imgMiniAvatar)
        if (!avatarPath.isNullOrEmpty()) {
            val file = File(avatarPath)
            if (file.exists()) {
                Glide.with(this)
                    .load(file)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarView)
            }
        }
    }

    /**
     * Перехід до екрану з деталями рецепта
     */
    private fun openRecipeDetails(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailsActivity::class.java).apply {
            putExtra("recipe_id", recipe.id)
            putExtra("title", recipe.title)
            putExtra("ingredients", recipe.ingredients)
            putExtra("instructions", recipe.instructions)
            putExtra("type", recipe.type)
            putExtra("time", recipe.time)
            putExtra("imageUri", recipe.imageUri)
            putExtra("isSaved", recipe.isSaved)
        }
        startActivity(intent)
    }
}
