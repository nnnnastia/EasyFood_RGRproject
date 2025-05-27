package com.example.recipe_book_rgr.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipe_book_rgr.R
import java.io.File

class RecipeAdapter(
    private val recipes: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onSavedClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        val image: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val type: TextView = itemView.findViewById(R.id.tvRecipeType)
        val time: TextView = itemView.findViewById(R.id.tvRecipeTime)
        val savedIcon: ImageView = itemView.findViewById(R.id.ivSaved)

        init {
            // Клік по всьому елементу
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(recipes[position])
                }
            }

            // Клік по іконці збереження
            savedIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val recipe = recipes[position].copy(isSaved = !recipes[position].isSaved)
                    onSavedClick(recipe)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.title.text = recipe.title
        holder.type.text = recipe.type
        holder.time.text = recipe.time

        // Завантаження зображення або дефолтне
        if (!recipe.imageUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(File(recipe.imageUri))
                .centerCrop()
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.sample_food)
        }

        // Відображення іконки збереження
        holder.savedIcon.setImageResource(
            if (recipe.isSaved) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    // Метод оновлення списку рецептів
    fun updateRecipes(newList: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = recipes.size
}
