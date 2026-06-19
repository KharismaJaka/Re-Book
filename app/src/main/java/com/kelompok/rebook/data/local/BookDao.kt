package com.kelompok.rebook.data.local

import androidx.room.*
import com.kelompok.rebook.data.BookStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)

    @Query("SELECT * FROM books WHERE ownerEmail = :email ORDER BY uploadedAt DESC")
    fun getBooksByOwner(email: String): Flow<List<BookEntity>>

    @Query("UPDATE books SET status = :status WHERE id = :bookId")
    suspend fun updateBookStatus(bookId: String, status: BookStatus)

    @Query("SELECT * FROM books ORDER BY uploadedAt DESC")
    fun getAllBooks(): Flow<List<BookEntity>>
}
