package com.example.mobile_handson_assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mobile_handson_assignment.data.Flashcard
import com.example.mobile_handson_assignment.data.FlashcardDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Correctly initialize the DAO to access the database
    private val flashcardDao by lazy {
        FlashcardDatabase.getDatabase(application).flashcardDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etQuestion = findViewById<EditText>(R.id.etQuestion)
        val etAnswer = findViewById<EditText>(R.id.etAnswer)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnGoToQuiz = findViewById<Button>(R.id.btnGoToQuiz)

        btnAdd.setOnClickListener {
            val question = etQuestion.text.toString().trim()
            val answer = etAnswer.text.toString().trim()
            val category = etCategory.text.toString().trim()

            if (question.isNotEmpty() && answer.isNotEmpty() && category.isNotEmpty()) {
                val newCard = Flashcard(question = question, answer = answer, category = category)

                lifecycleScope.launch {
                    flashcardDao.insert(newCard) // Use the DAO to insert
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Flashcard Added!", Toast.LENGTH_SHORT).show()
                        etQuestion.text.clear()
                        etAnswer.text.clear()
                        etCategory.text.clear()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}
