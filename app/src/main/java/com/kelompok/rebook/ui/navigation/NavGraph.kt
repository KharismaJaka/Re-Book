package com.kelompok.rebook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kelompok.rebook.data.AuthRepository
import com.kelompok.rebook.data.Book
import com.kelompok.rebook.data.BookRepository
import com.kelompok.rebook.data.SessionManager
import com.kelompok.rebook.data.local.AppDatabase
import com.kelompok.rebook.ui.screen.*
import com.kelompok.rebook.ui.viewmodel.*

@Suppress("UNCHECKED_CAST")
@Composable
fun ReBookNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val database = remember { AppDatabase.getDatabase(context) }
    val authRepository = remember { AuthRepository(database.userDao(), sessionManager) }
    val bookRepository = remember { BookRepository(database.bookDao()) }

    val bookViewModel: BookViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookViewModel(bookRepository, sessionManager) as T
            }
        }
    )

    val loginViewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = LoginViewModel(authRepository) as T
        }
    )

    val registerViewModel: RegisterViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = RegisterViewModel(authRepository) as T
        }
    )

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = DashboardViewModel(sessionManager) as T
        }
    )

    val profileViewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(authRepository, sessionManager) as T
        }
    )

    val authToken by sessionManager.authToken.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = if (authToken == null) "login" else "dashboard"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                viewModel = loginViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login")
                },
                onLoginClick = {
                    navController.popBackStack()
                },
                viewModel = registerViewModel
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onNavigateToExplore = { navController.navigate("explore") },
                onNavigateToWishlist = { navController.navigate("wishlist") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToUpload = { navController.navigate("upload") },
                onNavigateToMyBooks = { navController.navigate("my_books") },
                bookViewModel = bookViewModel,
                dashboardViewModel = dashboardViewModel
            )
        }
        composable("explore") {
            ExploreScreen(
                viewModel = bookViewModel,
                onBackClick = { navController.popBackStack() },
                onBookClick = { book: Book ->
                    bookViewModel.selectBook(book)
                    navController.navigate("detail")
                }
            )
        }
        composable("detail") {
            val selectedBook by bookViewModel.selectedBook.collectAsState()
            selectedBook?.let { book: Book ->
                BookDetailScreen(
                    book = book,
                    viewModel = bookViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        composable("upload") {
            UploadScreen(
                onBackClick = { navController.popBackStack() },
                onUploadSuccess = { navController.popBackStack() },
                viewModel = bookViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                viewModel = profileViewModel
            )
        }
        composable("wishlist") {
            WishlistScreen(
                viewModel = bookViewModel,
                onBackClick = { navController.popBackStack() },
                onBookClick = { book: Book ->
                    bookViewModel.selectBook(book)
                    navController.navigate("detail")
                }
            )
        }
        composable("my_books") {
            MyBooksScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = bookViewModel
            )
        }
    }
}
