package com.kelompok.rebook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kelompok.rebook.data.BookStatus

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String?,
    val isbn: String? = null,
    val publishYear: String? = null,
    val subject: String? = null,
    val status: BookStatus = BookStatus.AVAILABLE,
    val ownerEmail: String,
    val ownerName: String,
    val ownerWhatsApp: String,
    val uploadedAt: Long = System.currentTimeMillis()
)
