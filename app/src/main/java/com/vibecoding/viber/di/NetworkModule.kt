package com.vibecoding.viber.di

import android.content.Context
import com.vibecoding.viber.data.auth.AuthManager
import com.vibecoding.viber.data.auth.GitHubAppAuthProvider
import com.vibecoding.viber.data.auth.GitHubAppService
import com.vibecoding.viber.data.auth.OAuthAuthProvider
import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.remote.GitHubApiService
import com.vibecoding.viber.data.remote.GitHubAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GitHubApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GitHubAuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(preferencesManager: PreferencesManager): Interceptor {
        return Interceptor { chain ->
            val token = runBlocking { preferencesManager.accessToken.first() }
            val requestBuilder = chain.request().newBuilder()
            
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            requestBuilder.addHeader("Accept", "application/vnd.github.v3+json")
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @GitHubApiRetrofit
    fun provideGitHubApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @GitHubAuthRetrofit
    fun provideGitHubAuthRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(@GitHubApiRetrofit retrofit: Retrofit): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubAuthService(@GitHubAuthRetrofit retrofit: Retrofit): GitHubAuthService {
        return retrofit.create(GitHubAuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubAppService(@GitHubApiRetrofit retrofit: Retrofit): GitHubAppService {
        return retrofit.create(GitHubAppService::class.java)
    }

    @Provides
    @Singleton
    fun provideOAuthAuthProvider(
        authService: GitHubAuthService,
        preferencesManager: PreferencesManager
    ): OAuthAuthProvider {
        return OAuthAuthProvider(authService, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideGitHubAppAuthProvider(
        appService: GitHubAppService,
        preferencesManager: PreferencesManager
    ): GitHubAppAuthProvider {
        return GitHubAppAuthProvider(appService, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideAuthManager(
        oauthProvider: OAuthAuthProvider,
        githubAppProvider: GitHubAppAuthProvider,
        preferencesManager: PreferencesManager
    ): AuthManager {
        return AuthManager(oauthProvider, githubAppProvider, preferencesManager)
    }
}
