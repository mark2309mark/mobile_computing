package com.example.mobile_handson_assignment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mobile_handson_assignment.data.Flashcard
import com.example.mobile_handson_assignment.data.FlashcardDatabase
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    // --- UI Views ---
    private lateinit var spinnerCategory: Spinner
    private lateinit var tvQuestion: TextView
    private lateinit var tvAnswer: TextView
    private lateinit var btnShowQuestion: Button
    private lateinit var btnShowAnswer: Button
    private lateinit var btnGoBack: Button


    // --- Database and Data ---
    private val flashcardDao by lazy {
        FlashcardDatabase.getDatabase(application).flashcardDao()
    }
    private var allFlashcards: List<Flashcard> = emptyList()
    private var currentFilteredList: List<Flashcard> = emptyList()
    private var currentCard: Flashcard? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz) // Ensure you have 'activity_quiz.xml' in res/layout

        // --- Initialize Views ---
        spinnerCategory = findViewById(R.id.spinnerCategory)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvAnswer = findViewById(R.id.tvAnswer)
        btnShowQuestion = findViewById(R.id.btnShowQuestion)
        btnShowAnswer = findViewById(R.id.btnShowAnswer)
        btnGoBack = findViewById(R.id.btnGoBack)


        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Every time the user comes to this screen, fetch the latest data from the database.
        loadFlashcardsAndSetupUI()
    }

    /**
     * Fetches data from Room DB in a background thread and then sets up the UI on the main thread.
     * This fixes the race condition.
     */
    private fun loadFlashcardsAndSetupUI() {
        lifecycleScope.launch {
            // Background operation: Fetch all flashcards from the database.
            allFlashcards = flashcardDao.getAllFlashcards()

            // UI operation: Now that data is loaded, update the UI on the main thread.
            runOnUiThread {
                if (allFlashcards.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "No flashcards found. Please add some!", Toast.LENGTH_LONG).show()
                }
                // Setup the spinner with the newly fetched data.
                setupCategorySpinner()
            }
        }
    }

    /**
     * Configures the category spinner with categories from the fetched flashcards.
     */
    private fun setupCategorySpinner() {
        // Create a list of unique categories from the flashcards, with "All" as the first option.
        val categories = listOf("All") + allFlashcards.map { it.category }.distinct()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Set a listener to react when the user selects a new category.
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]

                // Filter the list of flashcards based on the selected category.
                currentFilteredList = if (selectedCategory == "All") {
                    allFlashcards
                } else {
                    allFlashcards.filter { it.category == selectedCategory }
                }
                // Reset the question/answer text to the initial state.
                resetQuizView()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // If nothing is selected, default to showing all flashcards.
                currentFilteredList = allFlashcards
            }
        }

        // Manually set the initial state based on the default spinner selection.
        if (categories.isNotEmpty()) {
            val initialCategory = spinnerCategory.selectedItem.toString()
            currentFilteredList = if (initialCategory == "All") allFlashcards else allFlashcards.filter { it.category == initialCategory }
            resetQuizView()
        }
    }

    /**
     * Sets up the OnClickListeners for the buttons.
     */
    private fun setupListeners() {
        btnShowQuestion.setOnClickListener {
            if (currentFilteredList.isNotEmpty()) {
                // Pick a random card from the currently filtered list.
                currentCard = currentFilteredList.random()
                tvQuestion.text = currentCard?.question
                tvAnswer.text = "..."
                tvAnswer.visibility = View.INVISIBLE
            } else {
                Toast.makeText(this, "No cards found in this category.", Toast.LENGTH_SHORT).show()
                resetQuizView()
            }
        }

        btnShowAnswer.setOnClickListener {
            if (currentCard != null) {
                // Show the answer for the current card.
                tvAnswer.text = currentCard!!.answer
                tvAnswer.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Click 'Show Random Question' first", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoBack.setOnClickListener {
            // Finish this activity and return to the previous one (MainActivity).
            finish()
        }
    }

    /**
     * Resets the question and answer views to their initial state.
     */
    private fun resetQuizView() {
        tvQuestion.text = "Click 'Show Random Question' to start."
        tvAnswer.visibility = View.INVISIBLE
        currentCard = null
    }
}
