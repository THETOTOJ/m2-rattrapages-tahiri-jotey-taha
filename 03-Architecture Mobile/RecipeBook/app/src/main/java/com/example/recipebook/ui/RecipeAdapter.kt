package com.example.recipebook.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.model.Recipe

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onEdit: (Recipe) -> Unit,
    private val onDelete: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.recipeName)
        val ingredients: TextView = view.findViewById(R.id.recipeIngredients)
        val instructions: TextView = view.findViewById(R.id.recipeInstructions)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.name.text = recipe.name
        holder.ingredients.text = recipe.ingredients
        holder.instructions.text = recipe.instructions

        holder.editButton.setOnClickListener { onEdit(recipe) }
        holder.deleteButton.setOnClickListener { onDelete(recipe) }
    }
    override fun getItemCount() = recipes.size

    fun updateData(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
