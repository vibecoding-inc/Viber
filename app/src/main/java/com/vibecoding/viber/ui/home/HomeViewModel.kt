package com.vibecoding.viber.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibecoding.viber.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _vibeMode = MutableStateFlow(false)
    val vibeMode: StateFlow<Boolean> = _vibeMode.asStateFlow()

    private val _catMode = MutableStateFlow(false)
    val catMode: StateFlow<Boolean> = _catMode.asStateFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

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

    fun triggerCelebration() {
        viewModelScope.launch {
            _showCelebration.value = true
            delay(3000) // Show celebration for 3 seconds
            _showCelebration.value = false
        }
    }

    fun toggleVibeMode() {
        viewModelScope.launch {
            val newValue = !_vibeMode.value
            preferencesManager.setVibeMode(newValue)
            if (newValue) {
                triggerCelebration()
            }
        }
    }

    fun toggleCatMode() {
        viewModelScope.launch {
            val newValue = !_catMode.value
            preferencesManager.setCatMode(newValue)
            if (newValue) {
                triggerCelebration()
            }
        }
    }
}
