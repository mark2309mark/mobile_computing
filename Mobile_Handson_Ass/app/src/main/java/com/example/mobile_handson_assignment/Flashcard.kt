package com.example.mobile_handson_assignment.data

import androidx.room.Entity
import androidx.room.PrimaryKey/**
 * Defines the structure of the "flashcards" table in the Room database.
 * The @Entity annotation marks this as a table.
 */
@Entity(tableName = "flashcards")
data class Flashcard(
    /**
     * The unique identifier for each flashcard.
     * @PrimaryKey marks this as the primary key.
     * autoGenerate = true tells Room to automatically assign an ID.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // The question text for the flashcard.
    val question: String,

    // The answer text for the flashcard.
    val answer: String,

    // The category to which the flashcard belongs.
    val category: String
)
