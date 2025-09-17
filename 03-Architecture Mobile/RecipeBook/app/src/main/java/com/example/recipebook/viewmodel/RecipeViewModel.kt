package com.example.recipebook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipebook.data.RecipeStorage
import com.example.recipebook.model.Recipe

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = RecipeStorage(application)

    private val _recipes = MutableLiveData<List<Recipe>>(storage.loadRecipes())
    val recipes: LiveData<List<Recipe>> get() = _recipes

    fun addRecipe(name: String, ingredients: String, instructions: String) {
        val current = _recipes.value?.toMutableList() ?: mutableListOf()
        val newRecipe = Recipe(
            id = current.size + 1,
            name = name,
            ingredients = ingredients,
            instructions = instructions
        )
        current.add(newRecipe)
        _recipes.value = current
        storage.saveRecipes(current)
    }

    fun deleteRecipe(recipe: Recipe) {
        val updatedList = _recipes.value.orEmpty().toMutableList()
        updatedList.remove(recipe)
        _recipes.value = updatedList
        storage.saveRecipes(updatedList)
    }

    fun updateRecipe(updated: Recipe) {
        val updatedList = _recipes.value.orEmpty().toMutableList()
        val index = updatedList.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            updatedList[index] = updated
            _recipes.value = updatedList
            storage.saveRecipes(updatedList)
        }
    }
}
