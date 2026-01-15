package com.vibecoding.viber.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _vibeMode = MutableStateFlow(false)
    val vibeMode: StateFlow<Boolean> = _vibeMode.asStateFlow()

    private val _catMode = MutableStateFlow(false)
    val catMode: StateFlow<Boolean> = _catMode.asStateFlow()

    init {
        checkAuth()
        
        viewModelScope.launch {
            preferencesManager.vibeModeEnabled.collect { enabled ->
                _vibeMode.value = enabled
            }
        }
        
        viewModelScope.launch {
            preferencesManager.catModeEnabled.collect { enabled ->
                _catMode.value = enabled
            }
        }
    }

    fun checkAuth() {
        viewModelScope.launch {
            _isAuthenticated.value = authRepository.isAuthenticated()
        }
    }
}
