package com.kelompok.rebook.data

import com.kelompok.rebook.data.local.UserDao
import com.kelompok.rebook.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {

    suspend fun login(email: String, pass: String): ApiResult<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.login(email, pass)
            if (user != null) {
                val profile = UserProfile(
                    id = user.email,
                    name = user.name,
                    email = user.email,
                    username = user.email,
                    status = user.status,
                    phone = user.phone
                )
                sessionManager.saveSession(profile, "local_session_token_" + user.email)
                ApiResult.Success(profile)
            } else {
                ApiResult.Error("Login gagal: Email atau password salah")
            }
        } catch (e: Exception) {
            ApiResult.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }

    suspend fun register(name: String, email: String, pass: String, status: String, phone: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                false
            } else {
                val newUser = UserEntity(
                    email = email,
                    name = name,
                    phone = phone,
                    status = status,
                    password = pass
                )
                userDao.registerUser(newUser)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateProfile(name: String, phone: String, status: String) {
        sessionManager.updateProfile(name, phone, status)
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
