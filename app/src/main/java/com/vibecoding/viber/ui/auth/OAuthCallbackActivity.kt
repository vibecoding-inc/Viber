package com.vibecoding.viber.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.vibecoding.viber.data.repository.AuthRepository
import com.vibecoding.viber.data.repository.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DEPRECATED: This activity handles the legacy OAuth callback flow.
 * The new Device Flow authentication is recommended for better security.
 * 
 * This callback will not work properly without a server-side component to handle
 * the client_secret, as it cannot be safely embedded in the Android app.
 */
@Deprecated("Use Device Flow authentication instead")
@AndroidEntryPoint
class OAuthCallbackActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent?.data
        if (uri != null && uri.scheme == "viber" && uri.host == "oauth") {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                lifecycleScope.launch {
                    // SECURITY WARNING: client_secret cannot be safely stored in mobile apps
                    // This will fail without a proper backend service
                    @Suppress("DEPRECATION")
                    val result = authRepository.handleAuthCode(code, "")
                    when (result) {
                        is Result.Success -> {
                            Log.i("OAuthCallbackActivity", "Auth succeeded (legacy flow)")
                            finish()
                        }
                        is Result.Error -> {
                            Log.e("OAuthCallbackActivity", "Auth failed: ${result.message}")
                            Log.e("OAuthCallbackActivity", "Use Device Flow authentication for proper security")
                            finish()
                        }
                        else -> finish()
                    }
                }
                return
            }
        }

        Log.w("OAuthCallbackActivity", "Invalid OAuth callback - use Device Flow instead")
        finish()
    }
}
