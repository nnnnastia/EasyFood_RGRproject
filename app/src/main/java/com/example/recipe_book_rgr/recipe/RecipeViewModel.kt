package com.example.recipe_book_rgr.recipe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RecipeDatabase.getDatabase(application).recipeDao()

    fun getAllRecipes(email: String): LiveData<List<Recipe>> = dao.getAllRecipes(email)
    fun getRecipesByType(type: String, email: String): LiveData<List<Recipe>> = dao.getRecipesByType(type, email)
    fun getSavedRecipes(email: String): LiveData<List<Recipe>> = dao.getSavedRecipes(email)

    fun insert(recipe: Recipe) = viewModelScope.launch { dao.insert(recipe) }
    fun update(recipe: Recipe) = viewModelScope.launch { dao.update(recipe) }
    fun delete(recipe: Recipe) = viewModelScope.launch { dao.delete(recipe) }
}