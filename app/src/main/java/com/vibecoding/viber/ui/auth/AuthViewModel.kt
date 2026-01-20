package com.vibecoding.viber.ui.auth

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vibecoding.viber.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _authUrl = MutableStateFlow("")
    val authUrl: StateFlow<String> = _authUrl.asStateFlow()

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun startAuth() {
        val url = authRepository.getAuthorizationUrl()
        _authUrl.value = url
        
        // Open in Custom Tab
        val context = getApplication<Application>()
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        
        intent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.launchUrl(context, Uri.parse(url))
    }

    fun handleAuthCallback(code: String) {
        viewModelScope.launch {
            val result = authRepository.handleAuthCode(code)
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
}
