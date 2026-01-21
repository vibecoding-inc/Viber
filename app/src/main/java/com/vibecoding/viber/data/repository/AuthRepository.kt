package com.vibecoding.viber.data.repository

import com.vibecoding.viber.BuildConfig
import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.remote.GitHubAuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: GitHubAuthService,
    private val preferencesManager: PreferencesManager
) {
    
    /**
     * Start Device Flow authentication (Step 1)
     * This is the secure way to authenticate mobile apps without exposing client_secret
     */
    suspend fun startDeviceFlow(): Result<DeviceFlowData> {
        return try {
            val response = authService.requestDeviceCode(
                clientId = BuildConfig.GITHUB_CLIENT_ID,
                scope = "repo,read:user,user:email,read:org"
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(
                        DeviceFlowData(
                            deviceCode = body.deviceCode,
                            userCode = body.userCode,
                            verificationUri = body.verificationUri,
                            expiresIn = body.expiresIn,
                            interval = body.interval
                        )
                    )
                } else {
                    Result.Error("Failed to start device flow: Empty response body")
                }
            } else {
                Result.Error("Failed to start device flow: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
        }
    }
    
    /**
     * Poll for access token (Step 2)
     * Should be called periodically until authentication completes or expires
     */
    suspend fun pollForAccessToken(deviceCode: String, interval: Int): Result<String> {
        return try {
            // Wait for the recommended interval before polling
            delay(interval * 1000L)
            
            val response = authService.pollForAccessToken(
                clientId = BuildConfig.GITHUB_CLIENT_ID,
                deviceCode = deviceCode
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    when {
                        body.accessToken != null -> {
                            // Success - save token
                            preferencesManager.saveAccessToken(body.accessToken)
                            Result.Success(body.accessToken)
                        }
                        body.error == "authorization_pending" -> {
                            // User hasn't authorized yet - continue polling
                            Result.Loading
                        }
                        body.error == "slow_down" -> {
                            // Polling too fast - wait longer
                            Result.Loading
                        }
                        body.error == "expired_token" -> {
                            Result.Error("Device code expired. Please try again.")
                        }
                        body.error == "access_denied" -> {
                            Result.Error("User denied authorization")
                        }
                        else -> {
                            Result.Error("Authentication error: ${body.errorDescription ?: body.error}")
                        }
                    }
                } else {
                    Result.Error("Failed to poll for token: Empty response body")
                }
            } else {
                Result.Error("Failed to poll for token: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
        }
    }
    
    /**
     * Legacy OAuth flow (DEPRECATED - not secure for mobile)
     * Keeping for backward compatibility but should not be used
     */
    @Deprecated("Use Device Flow instead - this method requires client_secret which is not secure for mobile apps")
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

    @Deprecated("Use Device Flow instead - this method requires client_secret which is not secure for mobile apps")
    suspend fun handleAuthCode(code: String, clientSecret: String): Result<String> {
        return try {
            val response = authService.getAccessToken(
                clientId = BuildConfig.GITHUB_CLIENT_ID,
                clientSecret = clientSecret,
                code = code,
                redirectUri = BuildConfig.GITHUB_REDIRECT_URI
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val accessToken = body.accessToken
                    preferencesManager.saveAccessToken(accessToken)
                    Result.Success(accessToken)
                } else {
                    Result.Error("Failed to get access token: Empty response body")
                }
            } else {
                Result.Error("Failed to get access token: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
        }
    }

    suspend fun getAccessToken(): String? {
        return preferencesManager.accessToken.first()
    }

    suspend fun isAuthenticated(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }
}

/**
 * Data class for Device Flow authentication
 */
data class DeviceFlowData(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresIn: Int,
    val interval: Int
)
