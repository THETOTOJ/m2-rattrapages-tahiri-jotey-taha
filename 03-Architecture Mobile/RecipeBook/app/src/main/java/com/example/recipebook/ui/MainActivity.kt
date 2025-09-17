package com.example.recipebook.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.databinding.ActivityMainBinding
import com.example.recipebook.model.Recipe
import com.example.recipebook.viewmodel.RecipeViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter

    private val addRecipeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val id = data.getIntExtra("id", -1)
                    val name = data.getStringExtra("name") ?: ""
                    val ingredients = data.getStringExtra("ingredients") ?: ""
                    val instructions = data.getStringExtra("instructions") ?: ""

                    if (id == -1) {
                        // Adding new recipe
                        viewModel.addRecipe(name, ingredients, instructions)
                    } else {
                        // Updating existing recipe
                        viewModel.updateRecipe(
                            Recipe(id, name, ingredients, instructions)
                        )
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecipeAdapter(
            emptyList(),
            onEdit = { recipe ->
                val intent = Intent(this, AddRecipeActivity::class.java).apply {
                    putExtra("id", recipe.id)
                    putExtra("name", recipe.name)
                    putExtra("ingredients", recipe.ingredients)
                    putExtra("instructions", recipe.instructions)
                }
                addRecipeLauncher.launch(intent)
            },
            onDelete = { recipe ->
                viewModel.deleteRecipe(recipe)
            }
        )

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recipeRecyclerView.adapter = adapter

        viewModel.recipes.observe(this) { recipes ->
            adapter.updateData(recipes)
            binding.emptyText.visibility =
                if (recipes.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.addFab.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            addRecipeLauncher.launch(intent)
        }
    }
}
