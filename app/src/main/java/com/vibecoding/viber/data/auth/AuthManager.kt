package com.vibecoding.viber.data.auth

import com.vibecoding.viber.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authentication manager that handles both OAuth App and GitHub App authentication.
 * Provides a unified interface for authentication operations.
 */
@Singleton
class AuthManager @Inject constructor(
    val oauthProvider: OAuthAuthProvider,
    val githubAppProvider: GitHubAppAuthProvider,
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val AUTH_TYPE_KEY = "auth_type"
    }

    /**
     * Get the currently active authentication provider.
     * Defaults to OAuth if no preference is set.
     */
    suspend fun getActiveProvider(): AuthProvider {
        val savedType = preferencesManager.getAuthType()
        return when (savedType) {
            AuthType.GITHUB_APP.name -> {
                if (githubAppProvider.isConfigured()) {
                    githubAppProvider
                } else {
                    oauthProvider
                }
            }
            else -> oauthProvider
        }
    }

    /**
     * Check which authentication methods are available.
     */
    fun getAvailableAuthTypes(): List<AuthType> {
        val available = mutableListOf<AuthType>()
        if (oauthProvider.isConfigured()) {
            available.add(AuthType.OAUTH_APP)
        }
        if (githubAppProvider.isConfigured()) {
            available.add(AuthType.GITHUB_APP)
        }
        return available
    }

    /**
     * Set the preferred authentication type.
     */
    suspend fun setAuthType(type: AuthType) {
        preferencesManager.setAuthType(type.name)
    }

    /**
     * Get the current access token from the active provider.
     */
    suspend fun getAccessToken(): String? {
        return getActiveProvider().getAccessToken()
    }

    /**
     * Check if the user is authenticated with any provider.
     */
    suspend fun isAuthenticated(): Boolean {
        return getActiveProvider().isAuthenticated()
    }

    /**
     * Logout from the current provider.
     */
    suspend fun logout() {
        getActiveProvider().logout()
    }

    /**
     * Check if GitHub App authentication is available and configured.
     */
    fun isGitHubAppAvailable(): Boolean {
        return githubAppProvider.isConfigured()
    }

    /**
     * Check if OAuth authentication is available and configured.
     */
    fun isOAuthAvailable(): Boolean {
        return oauthProvider.isConfigured()
    }
}
