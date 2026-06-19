package com.kelompok.rebook.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok.rebook.ui.viewmodel.BookViewModel
import com.kelompok.rebook.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToExplore: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToMyBooks: () -> Unit,
    bookViewModel: BookViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val user by dashboardViewModel.user.collectAsState()
    val wishlist by bookViewModel.wishlist.collectAsState()
    val myBooks by bookViewModel.myBooks.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Halo, Selamat Datang!", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = user?.name ?: "Mahasiswa Unila",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Mau cari referensi buku apa hari ini?",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Menu Utama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    MenuCard(
                        title = "Explore",
                        icon = Icons.Default.Search,
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onNavigateToExplore
                    )
                }
                item {
                    MenuCard(
                        title = "Wishlist",
                        icon = Icons.Default.Favorite,
                        color = MaterialTheme.colorScheme.error,
                        badge = wishlist.size.toString(),
                        onClick = onNavigateToWishlist
                    )
                }
                item {
                    MenuCard(
                        title = "Upload",
                        icon = Icons.Default.Add,
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = onNavigateToUpload
                    )
                }
                item {
                    MenuCard(
                        title = "Buku Saya",
                        icon = Icons.Default.Book,
                        color = MaterialTheme.colorScheme.primary,
                        badge = myBooks.size.toString(),
                        onClick = onNavigateToMyBooks
                    )
                }
                item {
                    MenuCard(
                        title = "Profil",
                        icon = Icons.Default.Person,
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onNavigateToProfile
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Statistik Re-Book", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📚", fontSize = 32.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Total Koleksi", style = MaterialTheme.typography.labelMedium)
                        Text("${myBooks.size} Buku Anda bagikan", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
            if (badge != null && badge != "0") {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onError,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
