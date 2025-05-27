package com.example.recipe_book_rgr.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.recipe_book_rgr.R
import com.example.recipe_book_rgr.recipe.Recipe
import com.example.recipe_book_rgr.recipe.RecipeDetailsActivity
import com.example.recipe_book_rgr.recipe.RecipeViewModel

class MyRecipesActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var recipes: List<Recipe> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_recipes)

        // Ініціалізація елементів інтерфейсу
        listView = findViewById(R.id.listViewRecipes)

        // Ініціалізація ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Отримання email користувача з SharedPreferences
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val email = prefs.getString("loggedInEmail", "") ?: ""

        // Спостереження за всіма рецептами, що належать поточному користувачу
        viewModel.getAllRecipes(email).observe(this) { result ->
            recipes = result

            // Формування списку назв рецептів для відображення
            val titles = recipes.map { it.title }

            // Створення адаптера зі стандартним макетом для списку
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, titles)

            listView.adapter = adapter
        }

        // Обробка натискання на рецепт у списку
        listView.setOnItemClickListener { _, _, position, _ ->
            val recipe = recipes[position]

            // Перехід до деталей рецепта, передаючи всі необхідні дані
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
}
