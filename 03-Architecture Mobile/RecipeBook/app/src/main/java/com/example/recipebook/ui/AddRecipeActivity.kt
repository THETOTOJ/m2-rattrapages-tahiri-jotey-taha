package com.example.recipebook.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.databinding.ActivityAddRecipeBinding

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecipeBinding
    private var editingId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingId = intent.getIntExtra("id", -1)
        if (editingId != -1) {
            binding.nameInput.setText(intent.getStringExtra("name") ?: "")
            binding.ingredientsInput.setText(intent.getStringExtra("ingredients") ?: "")
            binding.instructionsInput.setText(intent.getStringExtra("instructions") ?: "")
        }

        binding.saveButton.setOnClickListener {
            val name = binding.nameInput.text?.toString()?.trim().orEmpty()
            val ingredients = binding.ingredientsInput.text?.toString()?.trim().orEmpty()
            val instructions = binding.instructionsInput.text?.toString()?.trim().orEmpty()

            if (name.isBlank()) {
                Toast.makeText(this, "Please enter a recipe name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setResult(
                RESULT_OK,
                Intent().apply {
                    putExtra("id", editingId) // -1 means new recipe
                    putExtra("name", name)
                    putExtra("ingredients", ingredients)
                    putExtra("instructions", instructions)
                }
            )
            finish()
        }

        binding.cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
