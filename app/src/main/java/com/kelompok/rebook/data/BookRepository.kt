package com.kelompok.rebook.data

import com.kelompok.rebook.data.local.BookDao
import com.kelompok.rebook.data.local.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class BookRepository(private val bookDao: BookDao) {

    private val BASE_SEARCH_URL = "https://openlibrary.org/search.json"

    suspend fun searchBooks(query: String, limit: Int = 20): ApiResult<List<Book>> =
        withContext(Dispatchers.IO) {
            try {
                val cleanQuery = query.trim().replace("-", "").replace(" ", "")
                val isIsbn = cleanQuery.all { it.isDigit() } && (cleanQuery.length == 10 || cleanQuery.length == 13)
                
                val encoded = URLEncoder.encode(query.trim(), "UTF-8")

                val urlString = if (isIsbn) {
                    "$BASE_SEARCH_URL?isbn=$cleanQuery&limit=$limit&fields=key,title,author_name,cover_i,first_publish_year,isbn,subject,first_sentence"
                } else {
                    "$BASE_SEARCH_URL?title=$encoded&limit=$limit&fields=key,title,author_name,cover_i,first_publish_year,isbn,subject,first_sentence"
                }

                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                
                val raw = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(raw)

                val docs = json.getJSONArray("docs")
                val books = mutableListOf<Book>()

                for (i in 0 until docs.length()) {
                    val doc = docs.getJSONObject(i)

                    val coverId = if (doc.has("cover_i")) doc.getInt("cover_i") else null
                    val coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
                    val author = if (doc.has("author_name")) doc.getJSONArray("author_name").getString(0) else "Penulis Tidak Diketahui"
                    val isbn = if (doc.has("isbn")) doc.getJSONArray("isbn").getString(0) else null
                    val subject = if (doc.has("subject")) doc.getJSONArray("subject").getString(0) else null

                    val descRaw = if (doc.has("first_sentence")) {
                        val fs = doc.get("first_sentence")
                        when (fs) {
                            is JSONObject -> fs.optString("value", "Deskripsi tidak tersedia.")
                            is JSONArray -> if (fs.length() > 0) fs.getString(0) else "Deskripsi tidak tersedia."
                            else -> fs.toString()
                        }
                    } else "Deskripsi tidak tersedia."

                    books.add(
                        Book(
                            id          = doc.getString("key").removePrefix("/works/"),
                            title       = doc.optString("title", "Judul Tidak Diketahui"),
                            author      = author,
                            description = descRaw,
                            coverUrl    = coverUrl,
                            isbn        = isbn,
                            publishYear = doc.optInt("first_publish_year", 0).takeIf { it != 0 }?.toString(),
                            subject     = subject,
                            ownerName      = "Komunitas",
                            ownerWhatsApp  = "",
                            status         = BookStatus.AVAILABLE
                        )
                    )
                }
                ApiResult.Success(books)

            } catch (e: Exception) {
                ApiResult.Error("Gagal memuat data (Timeout atau Jaringan)")
            }
        }

    suspend fun getBookDetail(workId: String): ApiResult<Map<String, String>> =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://openlibrary.org/works/$workId.json")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                val raw = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(raw)

                val desc = when {
                    json.has("description") -> {
                        val d = json.get("description")
                        if (d is JSONObject) d.optString("value", "")
                        else d.toString()
                    }
                    else -> "Deskripsi lengkap tidak tersedia."
                }
                ApiResult.Success(mapOf("description" to desc))
            } catch (e: Exception) {
                ApiResult.Error("Gagal memuat detail.")
            }
        }

    suspend fun uploadBook(book: Book, ownerEmail: String) {
        val entity = BookEntity(
            id = book.id,
            title = book.title,
            author = book.author,
            description = book.description,
            coverUrl = book.coverUrl,
            isbn = book.isbn,
            publishYear = book.publishYear,
            subject = book.subject,
            status = book.status,
            ownerEmail = ownerEmail,
            ownerName = book.ownerName,
            ownerWhatsApp = book.ownerWhatsApp,
            uploadedAt = book.uploadedAt
        )
        bookDao.insertBook(entity)
    }

    fun getMyBooks(email: String): Flow<List<Book>> {
        return bookDao.getBooksByOwner(email).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun updateBookStatus(bookId: String, status: BookStatus) {
        bookDao.updateBookStatus(bookId, status)
    }

    suspend fun deleteBookById(bookId: String) {
        bookDao.deleteBookById(bookId)
    }
}

fun BookEntity.toDomain() = Book(
    id = id,
    title = title,
    author = author,
    description = description,
    coverUrl = coverUrl,
    isbn = isbn,
    publishYear = publishYear,
    subject = subject,
    status = status,
    ownerName = ownerName,
    ownerWhatsApp = ownerWhatsApp,
    uploadedAt = uploadedAt
)
