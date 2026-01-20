package com.vibecoding.viber.data.repository

import com.vibecoding.viber.BuildConfig
import com.vibecoding.viber.data.auth.AuthManager
import com.vibecoding.viber.data.auth.AuthType
import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.remote.GitHubAuthService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations.
 * Supports both OAuth App and GitHub App authentication methods.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authService: GitHubAuthService,
    private val preferencesManager: PreferencesManager,
    private val authManager: AuthManager
) {
    /**
     * Get the OAuth authorization URL for user login.
     */
    fun getAuthorizationUrl(): String {
        return authManager.oauthProvider.getAuthorizationUrl()
    }

    /**
     * Handle OAuth authorization code exchange.
     */
    suspend fun handleAuthCode(code: String): Result<String> {
        val result = authManager.oauthProvider.handleAuthCode(code)
        if (result is Result.Success) {
            // Set OAuth as the active auth type
            authManager.setAuthType(AuthType.OAUTH_APP)
        }
        return result
    }

    /**
     * Get the current access token (from whichever auth method is active).
     */
    suspend fun getAccessToken(): String? {
        return authManager.getAccessToken()
    }

    /**
     * Check if the user is authenticated.
     */
    suspend fun isAuthenticated(): Boolean {
        return authManager.isAuthenticated()
    }

    /**
     * Logout the user.
     */
    suspend fun logout() {
        authManager.logout()
    }

    /**
     * Check if GitHub App authentication is available.
     */
    fun isGitHubAppAvailable(): Boolean {
        return authManager.isGitHubAppAvailable()
    }

    /**
     * Switch to GitHub App authentication (if available).
     */
    suspend fun useGitHubApp(): Result<String> {
        if (!authManager.isGitHubAppAvailable()) {
            return Result.Error("GitHub App is not configured")
        }
        
        authManager.setAuthType(AuthType.GITHUB_APP)
        val token = authManager.githubAppProvider.getAccessToken()
        return if (token != null) {
            Result.Success(token)
        } else {
            Result.Error("Failed to get GitHub App token")
        }
    }

    /**
     * Get the list of available authentication types.
     */
    fun getAvailableAuthTypes(): List<AuthType> {
        return authManager.getAvailableAuthTypes()
    }
}
