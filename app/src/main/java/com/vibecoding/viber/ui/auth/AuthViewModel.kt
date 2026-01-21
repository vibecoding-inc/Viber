package com.vibecoding.viber.ui.auth

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vibecoding.viber.data.repository.AuthRepository
import com.vibecoding.viber.data.repository.DeviceFlowData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val _deviceFlowData = MutableStateFlow<DeviceFlowData?>(null)
    val deviceFlowData: StateFlow<DeviceFlowData?> = _deviceFlowData.asStateFlow()

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var pollingJob: Job? = null

    /**
     * Start Device Flow authentication
     * This is the secure way to authenticate without exposing client_secret
     */
    fun startDeviceFlowAuth() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = authRepository.startDeviceFlow()) {
                is com.vibecoding.viber.data.repository.Result.Success -> {
                    _deviceFlowData.value = result.data
                    
                    // Open verification URL in browser
                    val context = getApplication<Application>()
                    val intent = CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .build()
                    
                    intent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.launchUrl(context, Uri.parse(result.data.verificationUri))
                    
                    // Start polling for access token
                    startPolling(result.data.deviceCode, result.data.interval)
                }
                is com.vibecoding.viber.data.repository.Result.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
                else -> {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun startPolling(deviceCode: String, interval: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 60 // Poll for up to 10 minutes (60 attempts * 10 seconds)

            while (attempts < maxAttempts) {
                when (val result = authRepository.pollForAccessToken(deviceCode, interval)) {
                    is com.vibecoding.viber.data.repository.Result.Success -> {
                        _authSuccess.value = true
                        _isLoading.value = false
                        return@launch
                    }
                    is com.vibecoding.viber.data.repository.Result.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                        return@launch
                    }
                    is com.vibecoding.viber.data.repository.Result.Loading -> {
                        // Continue polling
                        attempts++
                    }
                }
            }

            // Timeout
            _error.value = "Authentication timeout. Please try again."
            _isLoading.value = false
        }
    }

    /**
     * Legacy OAuth flow (DEPRECATED)
     * Keeping for backward compatibility
     */
    @Deprecated("Use startDeviceFlowAuth instead")
    fun startAuth() {
        @Suppress("DEPRECATION")
        val url = authRepository.getAuthorizationUrl()
        
        // Open in Custom Tab
        val context = getApplication<Application>()
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        
        intent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.launchUrl(context, Uri.parse(url))
    }

    @Deprecated("Use Device Flow instead")
    fun handleAuthCallback(code: String) {
        viewModelScope.launch {
            @Suppress("DEPRECATION")
            val result = authRepository.handleAuthCode(code, "")
            when (result) {
                is com.vibecoding.viber.data.repository.Result.Success -> {
                    _authSuccess.value = true
                }
                is com.vibecoding.viber.data.repository.Result.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
