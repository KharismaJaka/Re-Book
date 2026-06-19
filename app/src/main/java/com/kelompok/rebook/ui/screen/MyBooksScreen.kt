package com.kelompok.rebook.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kelompok.rebook.data.Book
import com.kelompok.rebook.data.BookStatus
import com.kelompok.rebook.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBooksScreen(
    onBackClick: () -> Unit,
    viewModel: BookViewModel
) {
    val myBooks by viewModel.myBooks.collectAsState()
    var bookToEdit by remember { mutableStateOf<Book?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Book?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buku Saya") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (myBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Anda belum mengupload buku apa pun.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myBooks) { book ->
                    MyBookCard(
                        book = book,
                        onStatusChange = { bookToEdit = book },
                        onDelete = { showDeleteDialog = book }
                    )
                }
            }
        }
    }

    bookToEdit?.let { book ->
        AlertDialog(
            onDismissRequest = { bookToEdit = null },
            title = { Text("Ubah Status Buku") },
            text = {
                Column {
                    BookStatus.entries.forEach { status ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = book.status == status,
                                onClick = {
                                    viewModel.updateBookStatus(book.id, status)
                                    bookToEdit = null
                                }
                            )
                            Text(status.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { bookToEdit = null }) { Text("Batal") }
            }
        )
    }

    showDeleteDialog?.let { book ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Buku") },
            text = { Text("Apakah Anda yakin ingin menghapus buku '${book.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBook(book.id)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun MyBookCard(
    book: Book,
    onStatusChange: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(book.author, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                BookStatusBadge(status = book.status)
            }
            IconButton(onClick = onStatusChange) {
                Icon(Icons.Default.Edit, contentDescription = "Ubah Status", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
