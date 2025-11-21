package com.example.mobile_handson_assignment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobile_handson_assignment.data.Flashcard


@Dao
interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flashcard: Flashcard)

    // ADD THIS FUNCTION: A query to select all flashcards from the table.
    @Query("SELECT * FROM flashcards ORDER BY category ASC")
    suspend fun getAllFlashcards(): List<Flashcard>
}
