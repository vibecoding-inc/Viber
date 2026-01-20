package com.vibecoding.viber.data.auth

import android.util.Log
import com.vibecoding.viber.BuildConfig
import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.repository.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GitHub App authentication provider.
 * Uses JWT authentication to obtain installation access tokens.
 */
@Singleton
class GitHubAppAuthProvider @Inject constructor(
    private val appService: GitHubAppService,
    private val preferencesManager: PreferencesManager
) : AuthProvider {

    companion object {
        private const val TAG = "GitHubAppAuthProvider"
        // Refresh token 5 minutes before expiration
        private const val TOKEN_REFRESH_MARGIN_MS = 5 * 60 * 1000L
    }

    override val type: AuthType = AuthType.GITHUB_APP

    private var cachedToken: String? = null
    private var tokenExpiresAt: Instant? = null
    private val tokenMutex = Mutex()

    override fun isConfigured(): Boolean {
        return BuildConfig.GITHUB_APP_ID.isNotEmpty() &&
                BuildConfig.GITHUB_APP_PRIVATE_KEY.isNotEmpty() &&
                BuildConfig.GITHUB_APP_INSTALLATION_ID.isNotEmpty()
    }

    override suspend fun getAccessToken(): String? {
        if (!isConfigured()) {
            return null
        }

        return tokenMutex.withLock {
            // Check if we have a valid cached token
            val expiresAt = tokenExpiresAt
            if (cachedToken != null && expiresAt != null) {
                val now = Instant.now()
                val refreshThreshold = expiresAt.minusMillis(TOKEN_REFRESH_MARGIN_MS)
                if (now.isBefore(refreshThreshold)) {
                    return@withLock cachedToken
                }
            }

            // Need to fetch a new token
            val result = fetchInstallationToken()
            if (result is Result.Success) {
                cachedToken = result.data.token
                tokenExpiresAt = parseExpiresAt(result.data.expiresAt)
                
                // Also save to preferences for persistence
                preferencesManager.saveAccessToken(result.data.token)
                
                return@withLock cachedToken
            } else {
                Log.e(TAG, "Failed to fetch installation token: ${(result as? Result.Error)?.message}")
                return@withLock null
            }
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return getAccessToken() != null
    }

    override suspend fun logout() {
        tokenMutex.withLock {
            cachedToken = null
            tokenExpiresAt = null
        }
        preferencesManager.clearAll()
    }

    /**
     * Fetch a new installation access token from GitHub.
     */
    private suspend fun fetchInstallationToken(): Result<InstallationTokenResponse> {
        return try {
            val jwt = JwtHelper.generateAppJwt(
                BuildConfig.GITHUB_APP_ID,
                BuildConfig.GITHUB_APP_PRIVATE_KEY
            )
            
            val installationId = BuildConfig.GITHUB_APP_INSTALLATION_ID.toLongOrNull()
                ?: return Result.Error("Invalid installation ID")
            
            val response = appService.getInstallationToken(
                jwtToken = "Bearer $jwt",
                installationId = installationId
            )
            
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to get installation token: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching installation token", e)
            Result.Error("Error: ${e.localizedMessage}")
        }
    }

    /**
     * List all installations for this GitHub App.
     */
    suspend fun getInstallations(): Result<List<Installation>> {
        if (!isConfigured()) {
            return Result.Error("GitHub App is not configured")
        }

        return try {
            val jwt = JwtHelper.generateAppJwt(
                BuildConfig.GITHUB_APP_ID,
                BuildConfig.GITHUB_APP_PRIVATE_KEY
            )
            
            val response = appService.getInstallations("Bearer $jwt")
            
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to get installations: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching installations", e)
            Result.Error("Error: ${e.localizedMessage}")
        }
    }

    private fun parseExpiresAt(expiresAt: String): Instant {
        return try {
            Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(expiresAt))
        } catch (e: Exception) {
            // If parsing fails, assume token expires in 1 hour (default)
            Instant.now().plusSeconds(3600)
        }
    }
}
