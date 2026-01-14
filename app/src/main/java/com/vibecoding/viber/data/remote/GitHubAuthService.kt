package com.vibecoding.viber.data.remote

import com.vibecoding.viber.data.model.AccessTokenResponse
import retrofit2.Response
import retrofit2.http.*

interface GitHubAuthService {
    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Response<AccessTokenResponse>
}
