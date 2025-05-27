package com.example.recipe_book_rgr.recipe

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE ownerEmail = :email")
    fun getAllRecipes(email: String): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE type = :type AND ownerEmail = :email")
    fun getRecipesByType(type: String, email: String): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isSaved = 1 AND ownerEmail = :email")
    fun getSavedRecipes(email: String): LiveData<List<Recipe>>

    @Query("UPDATE recipes SET isSaved = :saved WHERE id = :id")
    suspend fun updateSaved(id: Int, saved: Boolean)

}