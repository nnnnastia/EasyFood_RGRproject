package com.example.recipe_book_rgr.recipe

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Оголошення бази даних Room з однією сутністю — Recipe
@Database(entities = [Recipe::class], version = 3)
abstract class RecipeDatabase : RoomDatabase() {

    // Абстрактна функція для доступу до DAO інтерфейсу
    abstract fun recipeDao(): RecipeDao

    companion object {
        // INSTANCE зберігає єдиний екземпляр бази даних
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        // Метод для отримання або створення екземпляра бази даних
        fun getDatabase(context: Context): RecipeDatabase {
            // Повертає існуючий екземпляр або створює новий у безпечному для багатопотоковості режимі
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,         // Використовує ApplicationContext, щоб уникнути витоків пам’яті
                    RecipeDatabase::class.java,        // Клас бази даних
                    "recipe_database"                  // Назва файлу бази даних
                )
                    .fallbackToDestructiveMigration() // У разі зміни версії — знищує та створює нову БД (використовувати обережно!)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
