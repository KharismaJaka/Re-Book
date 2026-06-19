package com.kelompok.rebook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.rebook.data.ApiResult
import com.kelompok.rebook.data.Book
import com.kelompok.rebook.data.BookRepository
import com.kelompok.rebook.data.BookStatus
import com.kelompok.rebook.data.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _booksState = MutableStateFlow<ApiResult<List<Book>>>(ApiResult.Loading)
    val booksState: StateFlow<ApiResult<List<Book>>> = _booksState

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook

    private val _fullDescription = MutableStateFlow<String?>(null)
    val fullDescription: StateFlow<String?> = _fullDescription

    private val _wishlist = MutableStateFlow<List<Book>>(emptyList())
    val wishlist: StateFlow<List<Book>> = _wishlist

    @OptIn(ExperimentalCoroutinesApi::class)
    val myBooks: StateFlow<List<Book>> = sessionManager.userData
        .flatMapLatest { user ->
            if (user != null) {
                repository.getMyBooks(user.email)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        searchBooks("science")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _booksState.value = ApiResult.Loading
            _booksState.value = repository.searchBooks(query)
        }
    }

    fun selectBook(book: Book) {
        _selectedBook.value = book
        _fullDescription.value = null
        fetchFullDescription(book.id)
    }

    private fun fetchFullDescription(workId: String) {
        viewModelScope.launch {
            if (!workId.contains("-")) { 
                val result = repository.getBookDetail(workId)
                if (result is ApiResult.Success) {
                    _fullDescription.value = result.data["description"]
                }
            }
        }
    }

    suspend fun fetchMetadata(query: String): Book? {
        val result = repository.searchBooks(query, limit = 1)
        return if (result is ApiResult.Success && result.data.isNotEmpty()) {
            result.data.first()
        } else null
    }

    fun uploadBook(book: Book, ownerEmail: String) {
        viewModelScope.launch {
            repository.uploadBook(book, ownerEmail)
        }
    }

    fun updateBookStatus(bookId: String, status: BookStatus) {
        viewModelScope.launch {
            repository.updateBookStatus(bookId, status)
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            repository.deleteBookById(bookId)
        }
    }

    fun toggleWishlist(book: Book) {
        val current = _wishlist.value.toMutableList()
        if (current.any { it.id == book.id }) {
            current.removeAll { it.id == book.id }
        } else {
            current.add(book)
        }
        _wishlist.value = current
    }

    fun isInWishlist(bookId: String): Boolean {
        return _wishlist.value.any { it.id == bookId }
    }
}
