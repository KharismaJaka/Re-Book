package com.kelompok.rebook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    private val _errorMsg = MutableStateFlow("")
    val errorMsg: StateFlow<String> = _errorMsg

    fun register(name: String, email: String, pass: String, status: String, phone: String) {
        viewModelScope.launch {
            val success = repository.register(name, email, pass, status, phone)
            if (success) {
                _registerSuccess.value = true
            } else {
                _errorMsg.value = "Pendaftaran gagal"
            }
        }
    }

    fun setError(message: String) {
        _errorMsg.value = message
    }

    fun resetRegisterStatus() {
        _registerSuccess.value = false
        _errorMsg.value = ""
    }
}