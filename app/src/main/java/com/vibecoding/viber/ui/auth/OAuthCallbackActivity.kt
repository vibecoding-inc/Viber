package com.vibecoding.viber.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OAuthCallbackActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent?.data
        if (uri != null && uri.scheme == "viber" && uri.host == "oauth") {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                viewModel.handleAuthCallback(code)
            }
        }

        // Close this activity and return to main
        finish()
    }
}
