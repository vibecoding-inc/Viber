package com.vibecoding.viber.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.model.User
import com.vibecoding.viber.data.repository.GitHubRepository
import com.vibecoding.viber.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _vibeMode = MutableStateFlow(false)
    val vibeMode: StateFlow<Boolean> = _vibeMode.asStateFlow()

    private val _catMode = MutableStateFlow(false)
    val catMode: StateFlow<Boolean> = _catMode.asStateFlow()

    init {
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

    fun loadUser() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = repository.getCurrentUser()) {
                is Result.Success -> {
                    _user.value = result.data
                }
                is Result.Error -> {
                    // Handle error
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    fun toggleVibeMode() {
        viewModelScope.launch {
            preferencesManager.setVibeMode(!_vibeMode.value)
        }
    }

    fun toggleCatMode() {
        viewModelScope.launch {
            preferencesManager.setCatMode(!_catMode.value)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}
