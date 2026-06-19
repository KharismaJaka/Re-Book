package com.kelompok.rebook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.rebook.data.SessionManager
import com.kelompok.rebook.data.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(sessionManager: SessionManager) : ViewModel() {
    val user: StateFlow<UserProfile?> = sessionManager.userData
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}