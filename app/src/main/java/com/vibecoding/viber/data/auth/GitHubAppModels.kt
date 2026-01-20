package com.vibecoding.viber.data.auth

import com.google.gson.annotations.SerializedName

/**
 * Response from GitHub API when requesting an installation access token.
 */
data class InstallationTokenResponse(
    @SerializedName("token") val token: String,
    @SerializedName("expires_at") val expiresAt: String,
    @SerializedName("permissions") val permissions: Map<String, String>?,
    @SerializedName("repository_selection") val repositorySelection: String?
)

/**
 * GitHub App installation information.
 */
data class Installation(
    @SerializedName("id") val id: Long,
    @SerializedName("account") val account: InstallationAccount,
    @SerializedName("app_id") val appId: Long,
    @SerializedName("target_type") val targetType: String,
    @SerializedName("permissions") val permissions: Map<String, String>?,
    @SerializedName("events") val events: List<String>?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

/**
 * Account associated with an installation.
 */
data class InstallationAccount(
    @SerializedName("login") val login: String,
    @SerializedName("id") val id: Long,
    @SerializedName("type") val type: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)
