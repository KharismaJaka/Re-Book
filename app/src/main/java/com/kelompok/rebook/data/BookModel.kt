package com.kelompok.rebook.data

enum class BookStatus {
    AVAILABLE,
    BORROWED,
    DONATED
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String?,
    val isbn: String? = null,
    val publishYear: String? = null,
    val subject: String? = null,
    val status: BookStatus = BookStatus.AVAILABLE,
    val ownerName: String,
    val ownerWhatsApp: String,
    val uploadedAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val username: String,
    val image: String? = null,
    val status: String = "Mahasiswa",
    val phone: String = ""
)

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}
