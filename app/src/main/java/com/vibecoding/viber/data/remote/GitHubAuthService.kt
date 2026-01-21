package com.vibecoding.viber.data.remote

import com.vibecoding.viber.data.model.AccessTokenResponse
import com.vibecoding.viber.data.model.DeviceAccessTokenResponse
import com.vibecoding.viber.data.model.DeviceCodeResponse
import retrofit2.Response
import retrofit2.http.*

interface GitHubAuthService {
    // Legacy OAuth flow (requires client_secret - not secure for mobile)
    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Response<AccessTokenResponse>
    
    // Device Flow - Step 1: Request device code
    @Headers("Accept: application/json")
    @POST("login/device/code")
    @FormUrlEncoded
    suspend fun requestDeviceCode(
        @Field("client_id") clientId: String,
        @Field("scope") scope: String
    ): Response<DeviceCodeResponse>
    
    // Device Flow - Step 2: Poll for access token
    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun pollForAccessToken(
        @Field("client_id") clientId: String,
        @Field("device_code") deviceCode: String,
        @Field("grant_type") grantType: String = "urn:ietf:params:oauth:grant-type:device_code"
    ): Response<DeviceAccessTokenResponse>
}
