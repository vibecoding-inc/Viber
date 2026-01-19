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
                // Use lifecycleScope to ensure token exchange completes before finishing
                lifecycleScope.launch {
                    val result = authRepository.handleAuthCode(code)
                    if (result is Result.Error) {
                        Log.e("OAuthCallbackActivity", "Auth failed: ${result.message}")
                    }
                    finish()
                }
                return
            }
        }

        // Close this activity and return to main
        finish()
    }
}
