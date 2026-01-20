package com.vibecoding.viber.data.auth

import com.vibecoding.viber.BuildConfig
import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.remote.GitHubAuthService
import com.vibecoding.viber.data.repository.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OAuth App authentication provider.
 * Uses the standard OAuth 2.0 authorization code flow for GitHub.
 */
@Singleton
class OAuthAuthProvider @Inject constructor(
    private val authService: GitHubAuthService,
    private val preferencesManager: PreferencesManager
) : AuthProvider {

    override val type: AuthType = AuthType.OAUTH_APP

    override fun isConfigured(): Boolean {
        return BuildConfig.GITHUB_CLIENT_ID.isNotEmpty() &&
                BuildConfig.GITHUB_CLIENT_SECRET.isNotEmpty()
    }

    override suspend fun getAccessToken(): String? {
        return preferencesManager.accessToken.first()
    }

    override suspend fun isAuthenticated(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    override suspend fun logout() {
        preferencesManager.clearAll()
    }

    /**
     * Get the OAuth authorization URL for user login.
     */
    fun getAuthorizationUrl(): String {
        val clientId = BuildConfig.GITHUB_CLIENT_ID
        val redirectUri = BuildConfig.GITHUB_REDIRECT_URI
        val scope = "repo,read:user,user:email,read:org"
        return "https://github.com/login/oauth/authorize" +
                "?client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&scope=$scope" +
                "&state=viber_auth_state"
    }

    /**
     * Exchange authorization code for access token.
     */
    suspend fun handleAuthCode(code: String): Result<String> {
        return try {
            val response = authService.getAccessToken(
                clientId = BuildConfig.GITHUB_CLIENT_ID,
                clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
                code = code,
                redirectUri = BuildConfig.GITHUB_REDIRECT_URI
            )

            if (response.isSuccessful && response.body() != null) {
                val accessToken = response.body()!!.accessToken
                preferencesManager.saveAccessToken(accessToken)
                Result.Success(accessToken)
            } else {
                Result.Error("Failed to get access token: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }
}
