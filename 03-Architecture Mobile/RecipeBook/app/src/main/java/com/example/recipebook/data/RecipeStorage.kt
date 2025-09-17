package com.example.recipebook.data

import android.content.Context
import com.example.recipebook.model.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeStorage(context: Context) {
    private val prefs = context.getSharedPreferences("recipes_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveRecipes(recipes: List<Recipe>) {
        val json = gson.toJson(recipes)
        prefs.edit().putString("recipes", json).apply()
    }

    fun loadRecipes(): List<Recipe> {
        val json = prefs.getString("recipes", null) ?: return emptyList()
        val type = object : TypeToken<List<Recipe>>() {}.type
        return gson.fromJson(json, type)
    }
}
