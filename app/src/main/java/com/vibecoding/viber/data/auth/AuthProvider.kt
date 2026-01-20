package com.vibecoding.viber.data.auth

/**
 * Interface for different authentication providers.
 * Supports both GitHub OAuth Apps and GitHub Apps.
 */
interface AuthProvider {
    /**
     * Returns the type of authentication provider.
     */
    val type: AuthType

    /**
     * Check if this provider is properly configured with required credentials.
     */
    fun isConfigured(): Boolean

    /**
     * Get the current access token for API calls.
     * Returns null if not authenticated.
     */
    suspend fun getAccessToken(): String?

    /**
     * Check if the user is currently authenticated.
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Clear authentication state (logout).
     */
    suspend fun logout()
}

/**
 * Types of authentication supported.
 */
enum class AuthType {
    /**
     * GitHub OAuth App - User-based authentication with OAuth 2.0 flow.
     * Requires: client_id, client_secret
     * Best for: User-facing apps that need user authorization
     */
    OAUTH_APP,

    /**
     * GitHub App - App-based authentication with JWT and installation tokens.
     * Requires: app_id, private_key, installation_id
     * Best for: Server-to-server integrations, advanced API access (Copilot, etc.)
     */
    GITHUB_APP
}
