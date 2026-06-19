package com.kelompok.rebook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.rebook.data.ApiResult
import com.kelompok.rebook.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<ApiResult<UserProfile>?>(null)
    val loginState: StateFlow<ApiResult<UserProfile>?> = _loginState

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = ApiResult.Loading
            _loginState.value = repository.login(username, pass)
        }
    }

    fun resetState() {
        _loginState.value = null
    }
}
