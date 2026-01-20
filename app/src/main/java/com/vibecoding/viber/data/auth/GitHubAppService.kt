package com.vibecoding.viber.data.auth

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit service for GitHub App API endpoints.
 */
interface GitHubAppService {
    /**
     * Get an installation access token using a JWT.
     * The Authorization header should contain: Bearer {JWT}
     */
    @POST("app/installations/{installation_id}/access_tokens")
    suspend fun getInstallationToken(
        @Header("Authorization") jwtToken: String,
        @Path("installation_id") installationId: Long
    ): Response<InstallationTokenResponse>

    /**
     * List installations for the authenticated GitHub App.
     */
    @GET("app/installations")
    suspend fun getInstallations(
        @Header("Authorization") jwtToken: String
    ): Response<List<Installation>>

    /**
     * Get a specific installation.
     */
    @GET("app/installations/{installation_id}")
    suspend fun getInstallation(
        @Header("Authorization") jwtToken: String,
        @Path("installation_id") installationId: Long
    ): Response<Installation>
}
