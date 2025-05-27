package com.example.recipe_book_rgr.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val ingredients: String,
    val instructions: String,
    val type: String,
    val time: String,
    val imageUri: String? = null,
    val isSaved: Boolean = false,
    val ownerEmail: String

)