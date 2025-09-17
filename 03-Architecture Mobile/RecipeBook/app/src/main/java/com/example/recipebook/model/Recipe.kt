package com.example.recipebook.model

data class Recipe(
    val id: Int,
    val name: String,
    val ingredients: String,
    val instructions: String
)